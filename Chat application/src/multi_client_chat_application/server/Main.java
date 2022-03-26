package multi_client_chat_application.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

//reference: https://gyawaliamit.medium.com/multi-client-chat-server-using-sockets-and-threads-in-java-2d0b64cad4a7
//reference: https://medium.com/nerd-for-tech/create-a-chat-app-with-java-sockets-8449fdaa933
//reference: https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/

public class Main {

	public static void main(String[] args) {
		
		HashMap<String, ArrayList<ServerThread>> roomThreadLists = new HashMap<String, ArrayList<ServerThread>>();
		HashMap<String, ArrayList<String>> roomMessageHistories = new HashMap<String, ArrayList<String>>(); 
		
		//create a default room for when clients first join 
		ArrayList<ServerThread> homeThreadList = new ArrayList<>();
		roomThreadLists.put("none", homeThreadList);
		
		ArrayList<String> homeMessageList = new ArrayList<>();
		roomMessageHistories.put("none", homeMessageList);
		
		//**add dates/times to this string**
		roomMessageHistories.get("none").add("***Server Started***");
		
		//add set up function to check date of last message of each chatroom, if older than a week, delete it
		
		//Hashmap 
		
 		
		try (ServerSocket serversocket = new ServerSocket(5000)) {
			
			System.out.println("Chat Server Started.....");
			
			
			while(true) {
				Socket socket = serversocket.accept();
				
				System.out.println("Client thread started....");
				
				ServerThread serverThread = new ServerThread(socket, "none", roomThreadLists, roomMessageHistories);
				serverThread.start();
			}
		} catch (Exception e) {
			System.out.println("Error occured in main: " + e.getStackTrace());
		}

	}

}


