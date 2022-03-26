package multi_client_chat_application.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//reference: https://gyawaliamit.medium.com/multi-client-chat-server-using-sockets-and-threads-in-java-2d0b64cad4a7
//reference: https://medium.com/nerd-for-tech/create-a-chat-app-with-java-sockets-8449fdaa933
//reference: https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/

public class ClientThread extends Thread {

	//used to send and receive data from the client
	private Socket clientSocket;
	
	//used to read data from the clientSocket object
	private BufferedReader in;
	
	public ClientThread(Socket s) throws IOException {
		this.clientSocket = s;
		this.in = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				String response = in.readLine();
				
				if (response == null) {
					break;
				}
				
				System.out.println(response);
			}
		}catch (IOException e) {
			System.out.println("IO error in client thread!");
			e.printStackTrace();
		} finally {
			try {
				in.close();
				clientSocket.close();
				System.exit(0); 
			} catch (Exception e) {
				System.out.println("Error trying to close socket! in client thread!");
				e.printStackTrace();
			}
		}
		
	}
}
