package main.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
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
	private LobbyPackage lastPack;
	
	private final int WIDTH = 1000, 
					  HEIGHT = 700,
					  MAXUSERS_ON_SERVER = 50;
	
	private final BufferedImage WOOD = graphicsLoader.loadImage("/textures/wood.png");
	private Font chalk;
	private Color YELLOW = new Color(255, 225, 0);
	private Thread feed;
	private ConcurrentHashMap<String, Boolean> challengers;
	
	private JButton[] enemy;
	
	private boolean inLobby;
	
	public LobbyPanel(Game game) {
		
		this.game = game;
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		inLobby = false;
		challengers = new ConcurrentHashMap<String, Boolean>();
		
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
					break;
				}
				LobbyPackage lp = (LobbyPackage) p;
				
				if(lp.getStatus() == 2) { //CHALLENGE
					
					try {
						challengers.put(lp.getUser()[0], true);
						getButtonByUsername(lp.getUser()[0]).setForeground(YELLOW);
						continue;
					} catch(NullPointerException e) {
						
					}
					
				} else if(lp.getStatus() == 1) { //ACCEPT
					
					inLobby = false;
					lastPack = new LobbyPackage(lp.getUser(), 100);
					oMan.sendData(lastPack);

					DataPackage dp = null;
					while((dp = (DataPackage) oMan.receiveData()) == null);
					newGame(dp);
					
				} else if(lp.getStatus() == 3) { //UPDATE LIST

					setEachElementFalse();
					
					for(int i = 0; !enemy[i].getText().isEmpty(); i++) {
						
						enemy[i].setText("");
						enemy[i].setForeground(Color.white);
						enemy[i].setVisible(false);
						
					}
					
					for(int i = 0, b = 0; i < MAXUSERS_ON_SERVER && i < lp.getUser().length; i++, b++) {
						
						if(lp.getUser()[i].equals(game.getWindow().getUsername())) {
							b--;
							continue;
						}
						
						enemy[b].setText(lp.getUser()[i]);
						
						if(challengers.get(lp.getUser()[i]) != null) {
							enemy[b].setForeground(YELLOW);
							challengers.replace(lp.getUser()[i], true);
						}
						
						removeChallengers();
						
						enemy[b].setVisible(true);
						
					}
					
					
				} else if(lp.getStatus() == 5) { //INITIAL BULK OF PLAYERS AFTER LOGIN OR GAME
					
					for(int i = 0; i < MAXUSERS_ON_SERVER && i < lp.getUser().length; i++) {
						
						enemy[i].setText(lp.getUser()[i]);
						enemy[i].setForeground(Color.white);
						enemy[i].setVisible(true);
						
					}
					
				} else if(lp.getStatus() == 7) { //RESEND REQUEST

					oMan.sendData(lastPack);
					
				}
				
				
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
			enemy[i].setVisible(false);
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
	
	private void removeChallengers() {
		
		String[] names = challengers.keySet().toArray(new String[0]);
		
		for(String n : names) {

			if(!challengers.get(n)) {
				
				challengers.remove(n);
				
			}
			
		}
		
	}

	private void setEachElementFalse() {
		
		String[] names = challengers.keySet().toArray(new String[0]);
		
		for(String n : names) {
			
			challengers.replace(n, false);
			
		}
		
	}

	ActionListener enemyListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			String[] nem = {((JButton) e.getSource()).getText()};
			if(!nem[0].isEmpty()) {
				
				if(((JButton) e.getSource()).getForeground().getRGB() == YELLOW.getRGB()) {
					
					lastPack = new LobbyPackage(nem, 1);
					oMan.sendData(lastPack); //ACCEPT FIGHT
					inLobby = false;

					DataPackage dp = null;
					while((dp = (DataPackage) oMan.receiveData()) == null);
					
					challengers = new ConcurrentHashMap<String, Boolean>();
					newGame(dp);
					
				} else {

					lastPack = new LobbyPackage(nem, 2);
					oMan.sendData(lastPack); //CHALLENGE USER
					
				}
				
			}
			
			
		}
		
	};
	
	private void newGame(DataPackage dp) {
		
		
		for(int i = 0; !enemy[i].getText().isEmpty(); i++) {
			
			enemy[i].setText("");
			enemy[i].setForeground(Color.white);
			enemy[i].setVisible(false);
			
		}
		
		challengers = new ConcurrentHashMap<String, Boolean>();
		
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
