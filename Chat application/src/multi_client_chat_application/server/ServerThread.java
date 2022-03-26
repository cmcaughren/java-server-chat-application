package multi_client_chat_application.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

//reference: https://gyawaliamit.medium.com/multi-client-chat-server-using-sockets-and-threads-in-java-2d0b64cad4a7
//reference: https://medium.com/nerd-for-tech/create-a-chat-app-with-java-sockets-8449fdaa933
//reference: https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/

public class ServerThread extends Thread {
	
	//used to send and receive data from the client
	private Socket clientSocket;
	
	private HashMap<String, ArrayList<ServerThread>> roomThreadLists;
	
	private HashMap<String, ArrayList<String>> roomMessageHistories;
	
	//used to read data from the clientSocket object
	private BufferedReader in;
	
	//used to write data into the clientSocket object
	private PrintWriter out;
	
	//name of chat "room" the user in
	private String roomname;
	
	//nickname of the user
	private String nickname;
	
	public ServerThread(Socket socket, String roomname, HashMap<String, ArrayList<ServerThread>> roomThreadLists, HashMap<String, ArrayList<String>> roomMessageHistories) {
		this.clientSocket = socket;
		this.roomThreadLists = roomThreadLists;
		this.roomMessageHistories = roomMessageHistories;
		this.roomname = roomname;
		this.nickname = "Unset nickname";
	} 
	
	@Override
	public void run() {
		try {
			//instantiate PrintWriter object out setting clientSocket as the output stream
			//to write data into (clientSocket is responsible for sending data to the client)
			//true flushes the buffer (otherwise must do it manually)
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			//instantiate BufferedReader object to read from clientSocket.
			//InputStreamReader creates stream reader for socket, reading the data as bytes
			//then passing it to BufferedReader to be converted to characters 
			in = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
			
			while(true) {
				
				String outputString = in.readLine();
				
				//client will send "/nickname somenickname" to set the nickname 
				if(outputString.startsWith("/nickname ")) {
					//**add error checking to ensure name given is one word? on server or client side, or both?
					//**add error checking to ensure that name is not already being used by another thread
					
					//do we want to let client change their username? if so output message to chatroom about it and save to chatroom history
					
					//parse the second half of the string for the new nickname (beginning at index 10 after "/nickname ")  
					this.nickname = outputString.substring(10);
					System.out.println("Set nickname to...." + nickname);
					
					continue;
				}
				
				//add another if else for /roomlist message, and then return all the chat rooms available
				

				//client will send "/exit" to leave and delete thread
				else if(outputString.equals("/exit")) {
					//System.out.println("Closing client " + nickname + " thread ....");
					System.out.println(nickname + " is disconnecting...");
					
					//output message to current chat room, saying client is leaving
					for (ServerThread thread: roomThreadLists.get(roomname)) {
						 
						//TODO** update the way the message is updated to include timestamp
						thread.out.println("***" + this.nickname + " has left the chatroom " + this.roomname + " and quit the application***");
						
					}
					
					//write message to the history for the room
					roomMessageHistories.get(roomname).add("***" + this.nickname + " has left the chatroom " + this.roomname + " and quit the application***");
					//remove self serverthread from room in roomThreadLists
					roomThreadLists.get(roomname).remove(this);
					
					out.close();
					clientSocket.close();
					break; 
				}				
				
				//client will send "/room somechatroomname" to indicate user wants to move to another chat room. 
				//If "somechatroomname" already exists, client thread will be moved to that room and message history will be output.
				//else a new chatroom will be created by that name (and new message history record will be added), then the client thread will be moved to it. 
				else if(outputString.startsWith("/room ")) {
					//** add synchronization?? 
					//**add error checking to ensure room name given is ok (one word)? checked on client side before being sent, or server side, or both?

					String oldroom = this.roomname;
					
					//output message to current chat room, saying client is leaving
					for (ServerThread thread: roomThreadLists.get(roomname)) {
						 
						//TODO** update the way the message is updated to include timestamp
						thread.out.println("***" + this.nickname + " has left the chatroom " + this.roomname + "***");
						
					}
					
					//write message to the history for the room
					roomMessageHistories.get(roomname).add("***" + this.nickname + " has left the chatroom " + this.roomname + "***");
					
					//remove self serverthread from room in roomThreadLists
					roomThreadLists.get(roomname).remove(this);
					
					//parse the second half of the string for the new room name (beginning at index 6 after "/room ")  
					this.roomname = outputString.substring(6); 
					
					//if chatroom does not exist yet, create a new one 
					if (!roomThreadLists.containsKey(roomname)) {
						
						ArrayList<ServerThread> newThreadList = new ArrayList<>();
						roomThreadLists.put(roomname, newThreadList);
						
						//add self serverthread to the room the user wants to move into
						roomThreadLists.get(roomname).add(this);
						
						//create a new message history list for the new room and add it to roomMessageHistories
						ArrayList<String> newRoomMessageList = new ArrayList<>();
						roomMessageHistories.put(roomname, newRoomMessageList);
						
						
						
						//output message to current chat room, saying client is joining and new room created
						for (ServerThread thread: roomThreadLists.get(roomname)) {
				 
							//TODO** update the way the message is updated to include timestamp
							thread.out.println("************************************************************");
							thread.out.println("***" + roomname + " has been created by " + this.nickname + "***");
							thread.out.println("***" + this.nickname + " has joined the chatroom " + this.roomname + "***");
							
						}
						//write message to the history for the room
						//**add dates/times to this string**
						roomMessageHistories.get(roomname).add("*************************MESSAGE HISTORY***********************************");
						roomMessageHistories.get(roomname).add("***" + roomname + " has been created by " + this.nickname + "***");
						roomMessageHistories.get(roomname).add("***" + this.nickname + " has joined the chatroom " + this.roomname + "***");
						
						System.out.println("Created new chat room named " + roomname + "....");
						//** add room in to store messages??
						continue;
		
					}
					
					//add self serverthread to the room the user wants to move into
					roomThreadLists.get(roomname).add(this);
					System.out.println("Moved client " + nickname + " from " + oldroom + " to " + roomname + ".....");
						
					//**output all history messages for the newly moved to room - empty if new room but still outputs a top 
					for (int i =0; i < roomMessageHistories.get(roomname).size(); i++) {
						
						//TODO** update the way the message is updated to include timestamp
						this.out.println(roomMessageHistories.get(roomname).get(i));
						
					}
					
					
					
					//output message to current chat room, saying client is joining
					for (ServerThread thread: roomThreadLists.get(roomname)) {
										
						//TODO** update the way the message is updated to include timestamp
						thread.out.println("***" + this.nickname + " has joined the chatroom " + this.roomname + "***");
											
					}
					//write message to the history for the room
					roomMessageHistories.get(roomname).add("***" + this.nickname + " has joined the chatroom " + this.roomname + "***");
				
					continue;
				}
				//if none of the above commands were given, the message sent on the socket should be output to 
				//all client threads which are in the same room as this client thread
				//output the message to all 
				for (ServerThread thread: roomThreadLists.get(roomname)) {
										 
					
					//TODO** update the way the message is updated to include timestamp
					thread.out.println("(" + this.nickname + "):" + outputString);
					System.out.println("Chatroom " + roomname + " recieved: " + "(" + this.nickname + "):" + outputString );
					
					//write message to the history for the room
					roomMessageHistories.get(roomname).add("(" + this.nickname + "):" + outputString);
				}

			}
		} catch (Exception e) {
			System.out.println("Error occured in main: " + e.getStackTrace());
		}

		
	}

}
