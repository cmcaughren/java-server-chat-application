package multi_client_chat_application.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

//reference: https://gyawaliamit.medium.com/multi-client-chat-server-using-sockets-and-threads-in-java-2d0b64cad4a7
//reference: https://medium.com/nerd-for-tech/create-a-chat-app-with-java-sockets-8449fdaa933
//reference: https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/

public class Main {

	public static void main(String[] args) {
		
		ConcurrentHashMap<String, ArrayList<ServerThread>> roomThreadLists = new ConcurrentHashMap<String, ArrayList<ServerThread>>();
		ConcurrentHashMap<String, ArrayList<String>> roomMessageHistories = new ConcurrentHashMap<String, ArrayList<String>>(); 
		
		//create a default room for when clients first join 
		ArrayList<ServerThread> homeThreadList = new ArrayList<>();
		roomThreadLists.put("DefaultRoom", homeThreadList);
		
		ArrayList<String> homeMessageList = new ArrayList<>();
		roomMessageHistories.put("DefaultRoom", homeMessageList);
		
		//set the string format we want for datetimestamps
		SimpleDateFormat dtformat = new SimpleDateFormat("dd/mm/yy HH:mm:ss");
		//get the current date and time
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		//convert that time stamp using the format outlined
		String ts = dtformat.format(timestamp); 

		//Add a log message of the server start to the default none chatroom 
		roomMessageHistories.get("DefaultRoom").add("***[" + ts + "] Chatroom Application Server Started***");
		
 		//Start the server chat
		try (ServerSocket serversocket = new ServerSocket(8888)) {
			
			System.out.println("Chat Server Started.....");
			
			//wait for clients to connect, then start a serverthread to handle each of their requests 
			while(true) {
				Socket socket = serversocket.accept();
				
				System.out.println("Client thread started....");
				
				ServerThread serverThread = new ServerThread(socket, "DefaultRoom", roomThreadLists, roomMessageHistories);
				serverThread.start();
			}
		} catch (Exception e) {
			System.out.println("Error occured in main: " + e.getStackTrace());
		}

	}

}


