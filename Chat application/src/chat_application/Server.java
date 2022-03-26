package chat_application;

//referenced: https://medium.com/nerd-for-tech/create-a-chat-app-with-java-sockets-8449fdaa933
//referenced: https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
//referenced: https://github.com/jayskhatri/multi-client-chat-ce344-computer-networks

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

	public static void main(String[] args) {
		
		//listens to connection requests from the clients
		final ServerSocket serverSocket;
		
		//used to send and receive data from the client
		final Socket clientSocket;
		
		//used to read data from the clientSocket object
		final BufferedReader in;
		
		//used to write data into the clientSocket object
		final PrintWriter out;
		
		//sc will be used to read data from the users keyboard
		final Scanner sc = new Scanner(System.in);
		
		//set this up to take a port number as a command line args[0] ?
		//server will listen for client requests at this port 
		int portNumber = 5000;

		//ClientThread comes from where? https://github.com/jayskhatri/multi-client-chat-ce344-computer-networks
		//private static final ClientThread[] threads = new ClientThread[maxClientsCount];
		
		try {
			//instantiate ServerSocket object
			serverSocket = new ServerSocket(portNumber);
			//use accept() method to wait for a request from client, then
			//create an instance of the Socket class "clientSocket"
			clientSocket = serverSocket.accept();
			
			//instantiate PrintWriter object out setting clientSocket as the output stream
			//to write data into (clientSocket is responsible for sending data to the client)
			out = new PrintWriter(clientSocket.getOutputStream());
			
			//instantiate BufferedReader object to read from clientSocket.
			//InputStreamReader creates stream reader for socket, reading the data as bytes
			//then passing it to BufferedReader to be converted to characters 
			in = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
			
			System.out.println("Chat Server Started.....");
			

		
			Thread sender = new Thread(new Runnable() {
				String msg; //to hold data written by the user
				@Override //we use @Override since every different thread will have a different run() method
				public void run() {
					while(true) {
						msg = sc.nextLine(); //read data from users keyboard
						out.println(msg); //write data stored in msg in the clientSocket
						out.flush(); //forces sending of data
					}
				}
			});
			//Thread class has default run() method, contains code the thread will execute
			//use start() on the thread to execute
			sender.start();
		
			Thread recieve = new Thread(new Runnable() {
				String msg;
				@Override
				public void run() {
					try {
						msg = in.readLine(); //read data from clientSocket using "in" object
						
						//while the client is still connected to the server, read line by line
						while(msg!=null) {
							System.out.println("Client: " + msg); //print to screen the message sent by client
							msg = in.readLine(); //read data from the clientSocket using "in" object
						}
						//if msg == null, the client is not connected anymore
						System.out.println("Client disconnected.");
						//closing the sockets and streams
						out.close();
						sc.close();
						clientSocket.close();
						serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			});
			recieve.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
