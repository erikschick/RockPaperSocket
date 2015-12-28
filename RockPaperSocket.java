import java.util.Scanner;
import java.io.*;
import java.net.*;

public class RockPaperSocket {
	private static String serverType = "";
	private static Scanner sn = new Scanner(System.in);
	private final static int GAME_PORT = 6789;
	
	public static void main(String[] args) throws Exception {
		
		while(!serverType.equals("s") && !serverType.equals("c")) {
			System.out.println("Input s for server and c for client: ");
			serverType = sn.nextLine().toLowerCase();
		}
		
		if(serverType.equals("s")) {
			runServer();
		} else if(serverType.equals("c")) {
			runClient();
		}
		
		sn.close();
	}
	
	private static void runServer() throws Exception {
		System.out.println("Server: " + InetAddress.getLocalHost() + 
							" on port: " + GAME_PORT);
		
		System.out.println("Waiting for client connection...");
		
		ServerSocket welcomeSocket = new ServerSocket(GAME_PORT);
		Socket connectionSocket = welcomeSocket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
		while(true) {
			outToClient.writeBytes("Rock, Paper, Scissors!\n");
			System.out.println("\nEnter r, p, or s");
			String x = sn.nextLine();
			
			String s = inFromClient.readLine();
			System.out.println("Client picked " + s);
			
			//Tell the client what was picked
			outToClient.writeBytes(x + "\n");
			
			String winText = "";
			if((x.equals("r") && s.equals("s")) || (x.equals("s") && s.equals("p"))
					|| (x.equals("p") && s.equals("r"))) {
				winText = "Winner is: Server!";
			} else if (x.equals(s)){
				winText = "Tie!";
			} else {
				winText = "Winner is: Client!";
			}
			System.out.println(winText);
			outToClient.writeBytes(winText + "\n");
			
			System.out.print("Play again? (y/n): ");
			if(!sn.nextLine().equals("y")) {
				break;
			}
			outToClient.writeBytes("1\n");
			System.out.println("\n\n");
		}
		
		outToClient.writeBytes("0\n");
		connectionSocket.close();
		welcomeSocket.close();
	}
	
	private static Socket tryConnect() {
		int tries = 0;
		
		while(tries < 10000) {
			try {
				Socket s = new Socket("localhost", GAME_PORT);
				return s;
			} catch (Exception e) {
				tries++;
			}
		}
		
		
		System.out.println("Connection failed: timeout");
		System.exit(0);
		return null; // This line is never reached
	}
	
	private static void runClient() throws Exception {
		System.out.println("Trying to connect to localhost on port " + GAME_PORT);
		System.out.println("Waiting for server connection...");
		
		Socket clientSocket = tryConnect();
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		while(true) {
			String s = inFromServer.readLine();
			System.out.println("Message from server: " + s);
			
			System.out.println("\nEnter r, p, or s");
			s = sn.nextLine();
			outToServer.writeBytes(s + '\n');
			
			s = inFromServer.readLine();
			System.out.println("Server picked " + s);
			
			s = inFromServer.readLine();
			System.out.println(s);
			
			if(!inFromServer.readLine().equals("1")) {
				break;
			}
			
			System.out.println("\n\n");
		}
		
		System.out.println("\n\n-- Host has ended the game. --");
		clientSocket.close();
	}

}
