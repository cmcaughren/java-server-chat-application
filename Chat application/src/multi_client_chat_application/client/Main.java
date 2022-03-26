package multi_client_chat_application.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

//reference: https://gyawaliamit.medium.com/multi-client-chat-server-using-sockets-and-threads-in-java-2d0b64cad4a7
//reference: https://medium.com/nerd-for-tech/create-a-chat-app-with-java-sockets-8449fdaa933
//reference: https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/

public class Main {

	public static void main(String[] args) {
		//IP address of the server
		//In this case, server and client are both implemented on the same machine
		//so we use address localhost
		String hostName = "127.0.0.1"; //can use command line to get args[0]? 
		
		//port number which is the port number defined by the server in Server.java
		int portNumber =  5000; //use command line to get args[1]? 
		
		//socket used by client to send and receive data from server
		try (Socket clientSocket = new Socket(hostName, portNumber)){
		
			//object to read data from socket
			//final BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
			//object to write data into socket
			final PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		
			//object to read data from user's keyboard
			final Scanner sc= new Scanner(System.in); 
			
			String userInput;
		
			ClientThread clientThread = new ClientThread(clientSocket);
			
			clientThread.start();
			
			do {
				
				userInput = sc.nextLine();
				out.println(userInput);
							
			} while (!userInput.equals("exit")); 
						
		} catch (Exception e) {
			System.out.println("Exception occured in client main: " + e.getStackTrace());
			
		}
	}
}
