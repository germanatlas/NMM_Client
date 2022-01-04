package main.online;

import java.io.IOException;

import main.game.Game;

public class OnlineManager {
	
	private Client client;
	private final int port = 42069;
	private boolean	activeClient;
	private Game game;
	//private boolean init;
	
	public OnlineManager(Game game) {
		
		activeClient = false;
		this.game = game;
		
		createClient();
		
		if(activeClient) {
			
			new Thread(() -> {
				
				
				while(true) {
					
					if(activeClient) {
						
						while(client.getIfActive());
						client.stopClient();
						activeClient = false;
						System.out.println("Connection to Server ended.");
						
					}
					
				}
				
			}).start();
			
		}
		
		//init = true;
		
	}
	
	public void sendData(DataPackage data) {
		
		if(activeClient) {
			
			client.sendData(data);
		
		}
		
	}
	
	public DataPackage receiveData() {
		
		if(activeClient) {
			
			return (DataPackage) client.receiveData();
			
		}
		
		return null;
		
	}
	
	private void createClient() {
		
		if(!activeClient) {
			
			String address = ""; //game.getWindow().getAddress();
			if(address == "") address = "localhost";
			try {
				client = new Client(address, port);
				game.getWindow().getLabel().setText("Connected.");
				game.getWindow().repaint();
				activeClient = true;
				System.out.println("Connected to Server.");
			} catch (IOException e) {
				game.getWindow().getLabel().setText("Cannot reach Server");
				System.out.println(game.getWindow().getLabel().getText());
				game.getWindow().repaint();
				System.out.println("Client Start Error:\n" + e);
			}
			
		}
		
	}
	
	public Client getClient() {
		
		return client;
		
	}
	
	public void endConnection() {
		
		if(activeClient) {
			
			client.stopClient();
			activeClient = false;
			
		}
		
	}
	
	public boolean getIfActive() {
		return activeClient;
	}

}
