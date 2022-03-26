package chat_application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		//socket used by client to send and receive data from server
		final Socket clientSocket;
		
		//object to read data from socket
		final BufferedReader in;
		
		//object to write data into socket
		final PrintWriter out;
		
		//object to read data from user's keyboard
		final Scanner sc = new Scanner(System.in); 
		
		//IP address of the server
		//In this case, server and client are both implemented on the same machine
		//so we use address localhost
		String hostName = "127.0.0.1"; //can use command line to get args[0]? 
		
		//port number which is the port number defined by the server in Server.java
		int portNumber =  5000; //use command line to get args[1]? 
		
		try {
			
			//declare the clients socket which we will use to communicate with the server
			clientSocket = new Socket(hostName, portNumber);
			
			//thread for sending of data
			out = new PrintWriter(clientSocket.getOutputStream());
			//thread for receiving data
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			Thread sender = new Thread(new Runnable() {
				String msg;
				@Override
				public void run() {
					while(true) {
						msg = sc.nextLine();
						out.println(msg);
						out.flush();
						
					}
				}
			});
			sender.start();
			
			Thread receiver = new Thread(new Runnable() {
				String msg;
				@Override
				public void run() {
					try {
						msg = in.readLine();
						while (msg!=null) {
							System.out.println("Server: "+msg);
							msg = in.readLine();
						}
						System.out.println("Server out of service");
						out.close();
						sc.close();
						clientSocket.close();
						
						
					} catch (IOException e){
						e.printStackTrace();
					}
				}
			});
			receiver.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
