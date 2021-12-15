package mypackages.ninemensmorris.networking;

import java.io.IOException;

import mypackages.ninemensmorris.game.Game;

public class OnlineManager {
	
	private Client client;
	private final int port;
	private boolean	activeClient;
	private Game game;
	//private boolean init;
	
	public OnlineManager(Game game) {
		
		activeClient = false;
		port = 42069;
		this.game = game;
		
		createClient();
		
		if(activeClient) {
			
			new Thread(() -> {
				
				connectionloop:
				while(true) {
					
					if(activeClient) {
						
						while(client.getIfActive());
						client.stopClient();
						activeClient = false;
						game.getWindow().setIfClientActive(activeClient);
						System.out.println("Connection to Server ended."); //TODO
						break connectionloop;
						
					}
					
				}
				
			}).start();
			
		}
		
		//init = true;
		
	}
	
	public void sendData(DataPackage data) {
		
		if(activeClient || game.getWindow().getIfClientActive()) {
			
			client.sendData(data);
		
		}
		
	}
	
	public DataPackage receiveData() {
		
		if(activeClient || game.getWindow().getIfClientActive()) {
			
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
				activeClient = true;
				game.getWindow().setIfClientActive(activeClient);
				System.out.println("Connected Client.");
			} catch (IOException e) {
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
