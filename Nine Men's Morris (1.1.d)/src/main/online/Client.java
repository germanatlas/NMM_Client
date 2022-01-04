package main.online;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class Client {

	private static final int TIMEOUT = 1000;
	private BufferedInputStream in;
	private BufferedOutputStream out;
	private Socket client;
	private boolean isInitiated = false;
	
	public Client(String address, int port) throws UnknownHostException, IOException {
		
		System.out.println("Starting Client...");
		client = new Socket(address, port);
		System.out.println("Client Address:\t" + getPublicAddress());
		System.out.println("Connected to Client:\t" + client.getInetAddress().getHostAddress());
		
		System.out.println("Creating Streams...");
		in = new BufferedInputStream(client.getInputStream());
		out = new BufferedOutputStream(client.getOutputStream());
		isInitiated = true;
		System.out.println("Finished creating Client.");
		
	}
	
	public void stopClient() {
		
		if(isInitiated) {
			
			try {
				client.close();
			} catch (IOException e) {
				System.out.println("Server Stop Error\n" + e);
			}
			
		}
		
	}
	
	public Object receiveData() {
		
		if(isInitiated) {
			
			int count;
			try {
				//idk, just many bytes as buffer
				byte[] buffer = new byte[2048];
				count = in.read(buffer);
				System.out.println(count + " Bytes Received.");
				
				byte[] pack = new byte[count];
				pack = shorten(buffer, count);
				Object obj = toObject(pack);
				
				return obj;
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Receiving Error:\n"); e.printStackTrace();
				return null;
			}
		} else {
			
			return null;
			
		}
		
	}
	
	public void sendData(Object data) {
		
		if(isInitiated) {
			
			try {
				byte[] bytes = toBytes(data);
				out.write(bytes);
				out.flush();
				System.out.println(bytes.length + " Bytes Sent.");
			} catch (IOException e) {
				System.out.println("Sending Error:\n" + e);
			}
		}
		
	}

	//TODO Remove
	public String getPublicAddress() throws IOException {
		
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));

		String ip = in.readLine();
		
		return ip;
	}

	private static byte[] toBytes(Object obj) throws IOException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		
		oos.writeObject(obj);
		oos.flush();
		
		byte[] bytes = bos.toByteArray();
		
		bos.close();
		
		return bytes;
		
	}

	private static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object obj = ois.readObject();
		ois.close();
		return obj;
		
	}

	private static byte[] shorten(byte[] srcmat, int limit) {
		
		byte[] b = new byte[limit];
		
		for(int i = 0; i < limit; i++) {
			
			b[i] = srcmat[i];
			
		}
		
		return b;
		
	}
	
	public boolean getIfActive() {
		try {
			return !client.isClosed() || client.getInetAddress().isReachable(TIMEOUT);
		} catch (IOException e) {
			return false;
		}
	}
	
}
