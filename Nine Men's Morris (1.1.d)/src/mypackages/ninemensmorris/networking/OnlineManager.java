package mypackages.ninemensmorris.networking;

import java.io.IOException;

import mypackages.ninemensmorris.game.Game;

public class OnlineManager {
	
	private Client client;
	private final int port;
	private boolean	activeClient;
	private Game game;
	
	public OnlineManager(Game game) {
		
		activeClient = false;
		port = 42069;
		this.game = game;
		
		createClient();
		
		new Thread(() -> {
			
			connectionloop:
			while(true) {
				
				if(activeClient) {
					
					while(client.getIfActive());
					client.stopClient();
					activeClient = false;
					System.out.println("Connection to Server ended."); //TODO
					break connectionloop;
					
				}
				
			}
			
		}).start();
		
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
			
			String address = game.getWindow().getAddress();
			if(address == "") address = "localhost";
			try {
				client = new Client(address, port);
				activeClient = true;
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
