import java.util.Random;
import java.util.Scanner;

import java.io.*;
import java.net.*;

public class RockPaperSocket {
	private static String serverType = "";
	private static Scanner sn = new Scanner(System.in);
	private final static int GAME_PORT = 6789;
	private final static int MAX_CONNECT_ATTEMPTS = 100;
	
	/**
	 * Asks the user for input, repeats until valid input is given
	 * @param message the message to ask
	 * @param choices all valid choices
	 * @return Valid user input
	 */
	private static String getValidString(String message, String... choices) {
		String input = "";
		while(true) {
			System.out.println(message);
			input = sn.nextLine();
			for(String choice : choices) {
				if(input.equals(choice)) {
					return input;
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		serverType = getValidString(
				"Input s for server, c for client, or a for alone: ",
				"s", "c" , "a");
		
		if(serverType.equals("s")) {
			runServer();
		} else if(serverType.equals("c")) {
			runClient();
		} else if(serverType.equals("a")) {
			runAlone();
		}
		
		sn.close();
	}
	
	
	/**
	 * Single player mode, computer randomly selects enemy choice
	 */
	private static void runAlone() {
		Random rand = new Random();
		
		while(true) {
			System.out.println("\n");
			String x = getValidString("Enter r, p, or s", "r", "p", "s");
			
			String choices[] = {"r", "p", "s"};
			String s = choices[rand.nextInt(3)];
			
			System.out.println("Client picked " + s);
			
			
			String winText = "";
			if((x.equals("r") && s.equals("s")) || (x.equals("s") && s.equals("p"))
					|| (x.equals("p") && s.equals("r"))) {
				winText = "Winner is: Player!";
			} else if (x.equals(s)){
				winText = "Tie!";
			} else {
				winText = "Winner is: Computer!";
			}
			
			System.out.println(winText);
			
			if(getValidString("Play again? (y/n): ", "y", "n").equals("n")) {
				break;
			}
			System.out.println("\n\n");
		}
	}
	
	/**
	 * Server mode, requires a client to connect
	 * @throws Exception network failure
	 */
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
			System.out.println("\n");
			String x = getValidString("Enter r, p, or s", "r", "p", "s");
			
			System.out.println("Awaiting opponent's response...");
			
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
			
			if(getValidString("Play again? (y/n): ", "y", "n").equals("n")) {
				break;
			}
			
			outToClient.writeBytes("1\n");
			System.out.println("\n\n");
		}
		
		outToClient.writeBytes("0\n");
		connectionSocket.close();
		welcomeSocket.close();
	}
	
	/**
	 * Attempts to establish a connection to the server.
	 * @return The socket that was connected to, or null on failure
	 */
	private static Socket tryConnect(String hostname) {
		int tries = 0;
		
		while(tries < MAX_CONNECT_ATTEMPTS) {
			try {
				Socket s = new Socket(hostname, GAME_PORT);
				return s;
			} catch (Exception e) {
				tries++;
			}
		}
		return null;
	}
	
	
	/**
	 * Client mode, requires a server to connect to
	 * @throws Exception network failure
	 */
	private static void runClient() throws Exception {
		System.out.println("Enter the server IP address:");
		String IPaddress = sn.nextLine();
		System.out.println("Trying to connect to " + IPaddress + " on port " + GAME_PORT);
		System.out.println("Waiting for server connection...");
		Socket clientSocket = tryConnect(IPaddress);
		
		if(clientSocket == null) {
			System.err.println("Failed to connect to server");
			System.exit(0);
		}
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		while(true) {
			String s = inFromServer.readLine();
			System.out.println("Message from server: " + s);
			
			System.out.println("\n");
			s = getValidString("Enter r, p, or s", "r", "p", "s");
			outToServer.writeBytes(s + '\n');
			
			System.out.println("Awaiting opponent's response...");
			
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
