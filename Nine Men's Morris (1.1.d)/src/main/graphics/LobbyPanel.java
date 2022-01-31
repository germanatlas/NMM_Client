package main.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Font;
import java.awt.FontFormatException;
import javax.swing.JButton;
import javax.swing.JPanel;

import main.game.Game;
import main.online.OnlineManager;
import main.online.packs.DataPackage;
import main.online.packs.LobbyPackage;
import main.states.State;

public class LobbyPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Game game;
	private GraphicsLoader graphicsLoader = new GraphicsLoader();
	private OnlineManager oMan;
	
	private final int WIDTH = 1000, 
					  HEIGHT = 700,
					  MAXUSERS_ON_SERVER = 50;
	
	private final BufferedImage WOOD = graphicsLoader.loadImage("/textures/wood.png");
	private Font chalk;
	private Color YELLOW = new Color(255, 225, 0);
	private Thread feed;
	
	private JButton[] enemy;
	
	private boolean inLobby;
	
	public LobbyPanel(Game game) {
		
		this.game = game;
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		inLobby = false;
		
		try {
			chalk = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/chalk.ttf"));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buttonInit();
		this.setLayout(null);
		
	}
	
	public void run() {

		inLobby = true;
		feed = new Thread(() -> {
			
			while(inLobby) {
				
				Object p = null;
				
				//If in Lobby, wait for package, else just continue
				while((p = oMan.receiveData()) == null);
				
				if(!inLobby) {
					newGame((DataPackage) p);
					System.out.println("AAAAAAAAAAAAAAAAAAAAAA");
					break;
				}
				LobbyPackage lp = (LobbyPackage) p;
				System.out.println("FICK DIESE SCHEIßE, ES SOLL FUNKTIONIEREN " + lp.getStatus() + " " + lp.getUser().length);
				if(lp.getStatus() == 2) { //CHALLENGE
					
					try {
						getButtonByUsername(lp.getUser()[0]).setForeground(YELLOW);
						continue;
					} catch(NullPointerException e) {
						
					}
					
				} else if(lp.getStatus() == 1) { //ACCEPT
					
					inLobby = false;

					DataPackage dp = null;
					while((dp = (DataPackage) oMan.receiveData()) == null);
					newGame(dp);
					
				}
				
				setUser(lp);
				
				
			}
			
		});
		
		feed.start();
	}
	
	public void tick() {
		this.repaint();
		
	}
	
	private void buttonInit() {
		
		enemy = new JButton[MAXUSERS_ON_SERVER];
		
		for(int i = 0; i < MAXUSERS_ON_SERVER; i++) {
			
			enemy[i] = new JButton();

			enemy[i].setLocation((i/10)*200 + 10, 60 * (i%10 + 1));
			enemy[i].setSize(180, 50);
			enemy[i].setFocusable(false);
			enemy[i].addActionListener(enemyListener);
			enemy[i].setBackground(Color.black);
			enemy[i].setForeground(Color.white);
			enemy[i].setOpaque(false);
			enemy[i].setFont(chalk.deriveFont(22f));
			enemy[i].setFocusPainted(false);
			enemy[i].setVisible(true);
			enemy[i].setText("");
			
			this.add(enemy[i]);
			
		}
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(WOOD, 0, 0, null);
		
	}
	
	private JButton getButtonByUsername(String user) {
		
		for(JButton b : enemy) {
			
			if(b.getText().equals(user)) {
				return b;
			}
			
		}
		
		return null;
	}
	
	private void setUser(LobbyPackage lp) {
		
		JButton tmp;
		System.out.println("setUser");
		
		if(lp.getStatus() == 3) { //ADD ONE PLAYER
			
			for(int i = 0; i < MAXUSERS_ON_SERVER; i++) {
				
				if(enemy[i].getText().isEmpty()) {
					
					enemy[i].setText(lp.getUser()[0]);
					enemy[i].setVisible(true);
					break;
					
				}
				
			}
			
		} else if(lp.getStatus() == 4) { //REMOVE
			
			for(int i = 0; i < lp.getUser().length; i++) {
				
				if((tmp = getButtonByUsername(lp.getUser()[i])) != null) {
					
					tmp.setText("");
					tmp.setForeground(Color.white);
					tmp.setVisible(false);
					
				}
				
			}
			
		} else if(lp.getStatus() == 5) { //INITIAL BULK OF PLAYERS AFTER LOGIN OR GAME
			
			for(int i = 0; i < MAXUSERS_ON_SERVER && i < lp.getUser().length; i++) {
				
				enemy[i].setText(lp.getUser()[i]);
				enemy[i].setForeground(Color.white);
				enemy[i].setVisible(true);
				System.out.println("Reset");
				
			}
			
		}
		
	}
	
	ActionListener enemyListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			String[] nem = {((JButton) e.getSource()).getText()};
			if(!nem[0].isEmpty()) {
				
				if(((JButton) e.getSource()).getForeground().getRGB() == YELLOW.getRGB()) {
					
					System.out.println("ACCEPT");
					oMan.sendData(new LobbyPackage(nem, 1)); //ACCEPT FIGHT
					inLobby = false;

					DataPackage dp = null;
					while((dp = (DataPackage) oMan.receiveData()) == null);
					newGame(dp);
					
				} else {

					System.out.println("CHALLENGE");
					oMan.sendData(new LobbyPackage(nem, 2)); //CHALLENGE USER
					
				}
				
			}
			
			
		}
		
	};
	
	private void newGame(DataPackage dp) {
		
		game.getWindow().setRunning(true);
		game.getWindow().getPanel().setOnline(true);
		game.getWindow().getPanel().setOnlineManager(oMan);
		game.getWindow().getPanel().setActiveUser(dp.getFromY() % 2 != 1);
		game.reset(true, dp.getFromX() % 2 == 1);
		
		State.setCurrentState(game.getGameState());
		
	}
	
	public void setOnlineManager(OnlineManager onlineManager) {
		this.oMan = onlineManager;
	}
	
	public boolean getLobbyStatus() {
		return inLobby;
	}

}
