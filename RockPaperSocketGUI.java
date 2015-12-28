import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import java.io.*;
import java.net.*;

public class RockPaperSocketGUI {
	private static Scanner sn = new Scanner(System.in);
	private final static int GAME_PORT = 6789;
	private final static int MAX_CONNECT_ATTEMPTS = 100;
	
	private static RPSpanel panel = new RPSpanel();
	private static boolean waiting = false;
	
	public static void choiceMade() {
		waiting = false;
	}
	
	private static void waitOnChoice() {
		waiting = true;
		while(waiting) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private static String getChoice() {
		waitOnChoice();
		return panel.getChoice();
	}
	
	public static void main(String[] args) throws Exception {		
		JFrame frame = new JFrame("Rock Paper Sockets");
		frame.setMinimumSize(new Dimension(400, 100));
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		
		Object[] options = {"Server", "Client", "Alone"};
		int serverType = JOptionPane.showOptionDialog(panel,
			"How are you playing?",
			"Server mode selection",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[2]);
		
		if(serverType == 0) {
			frame.setTitle("Rock Paper Sockets (Server)");
			runServer();
		} else if(serverType == 1) {
			frame.setTitle("Rock Paper Sockets (Client)");
			runClient();
		} else if(serverType == 2) {
			runAlone();
		}
		
		sn.close();
	}
	
	
	/**
	 * Single player mode, computer randomly selects enemy choice
	 */
	private static void runAlone() throws Exception {
		Random rand = new Random();
		
		while(true) {
			panel.clearText();
			
			String x = getChoice();
			panel.addLine("You picked " + x);
			
			String choices[] = {"r", "p", "s"};
			String s = choices[rand.nextInt(3)];
			
			panel.addLine("Computer picked " + s);
			
			String winText = "";
			if((x.equals("r") && s.equals("s")) || (x.equals("s") && s.equals("p"))
					|| (x.equals("p") && s.equals("r"))) {
				winText = "Winner is: Player!";
			} else if (x.equals(s)){
				winText = "Tie!";
			} else {
				winText = "Winner is: Computer!";
			}
			
			panel.addLine(winText);
			
			Thread.sleep(1000);
			int reply = JOptionPane.showConfirmDialog(
					panel, "Play again?", null, JOptionPane.YES_NO_OPTION);
			if(reply == JOptionPane.NO_OPTION) {
				break;
			}
		}
	}
	
	/**
	 * Server mode, requires a client to connect
	 * @throws Exception network failure
	 */
	private static void runServer() throws Exception {
		panel.clearText();
		panel.addLine("Server: " + InetAddress.getLocalHost() + 
							" on port: " + GAME_PORT);
		
		panel.addLine("Waiting for client connection...");
		
		ServerSocket welcomeSocket = new ServerSocket(GAME_PORT);
		Socket connectionSocket = welcomeSocket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
		while(true) {
			panel.clearText();
			
			outToClient.writeBytes("Rock, Paper, Scissors!\n");
			
			String x = getChoice();
			panel.addLine("You picked " + x);
			panel.addLine("Awaiting opponent's response...");
			
			String s = inFromClient.readLine();
			panel.addLine("Client picked " + s);
			
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
			panel.addLine(winText);
			outToClient.writeBytes(winText + "\n");
			
			Thread.sleep(1000);
			int reply = JOptionPane.showConfirmDialog(
					panel, "Play again?", null, JOptionPane.YES_NO_OPTION);
			if(reply == JOptionPane.NO_OPTION) {
				break;
			}
			
			outToClient.writeBytes("1\n");
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
		panel.clearText();
		String IPaddress = (String)JOptionPane.showInputDialog(
                panel, "Enter the server IP address", null,
                JOptionPane.PLAIN_MESSAGE, null, null, null);
		
		panel.addLine("Trying to connect to " + IPaddress + " on port " + GAME_PORT);
		panel.addLine("Waiting for server connection...");
		Socket clientSocket = tryConnect(IPaddress);
		
		if(clientSocket == null) {
			System.err.println("Failed to connect to server");
			System.exit(0);
		}
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		while(true) {
			panel.clearText();
			
			String s = inFromServer.readLine();
			panel.addLine("Message from server: " + s);
			
			s = getChoice();
			panel.addLine("You picked " + s);
			outToServer.writeBytes(s + '\n');
			
			panel.addLine("Awaiting opponent's response...");
			s = inFromServer.readLine();
			panel.addLine("Server picked " + s);
			
			s = inFromServer.readLine();
			panel.addLine(s);
			
			if(!inFromServer.readLine().equals("1")) {
				break;
			}
		}
		
		panel.addLine("\n\n-- Host has ended the game. --");
		clientSocket.close();
	}

}
