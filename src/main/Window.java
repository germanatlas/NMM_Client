package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import main.game.Game;
import main.graphics.GraphicsJPanel;
import main.graphics.GraphicsLoader;
import main.graphics.LobbyPanel;
import main.graphics.MyJPanel;
import main.online.OnlineManager;
import main.online.packs.DataPackage;
import main.online.packs.LoginPackage;
import main.states.LobbyState;
import main.states.State;

public class Window extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private JFrame	frame,
					enemySelection;
	
	private JButton startButton, 
					optionButton, 
					closeButton, 
					continueButton,
					joinButton,
					exitButton,
					loginButton,
					registerButton;
	
	private JTextField 	uNameTF;
	private JPasswordField passTF;
	
	private JLabel label;
	private GraphicsJPanel panel;
	private GraphicsLoader graphicsLoader;
	private MyJPanel jPanel[][];
	private Game game;
	private JPanel	messagePanel;
	private OnlineManager oMan;
	private LobbyPanel lobby;
	
	private boolean running,
					activeClient,
					isOnline,
					wasOnline,
					loginreg,
					rdytologin;
	
	private Font chalk;
	private final Color	GREENISH = new Color(53, 104, 45),
						REDDISH = new Color(156, 16, 6);
	
	private final int WIDTH = 1000, 
					  HEIGHT = 700, 
					  SMALLWIDTH = 50, 
					  SMALLHEIGHT = 50;
	
	private final String TITLE = "Nine Men's Morris";
	private final GridBagConstraints STANDARDCONSTRAINTS = new GridBagConstraints();

	private String username;
	
	private int password;

//Daniel	
	public Window(Game game) {
		this.game = game;
		
		try {
			chalk = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/chalk.ttf"));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		graphicsLoader = new GraphicsLoader();
		
		createWindow();
	}
	
	private void createWindow() {
		running = false;
		
		username = "";
		password = 0;
		activeClient = false;
		rdytologin = false;
		
		frame = new JFrame(TITLE);
		enemySelection = new JFrame("Players Online");
		
		panel = new GraphicsJPanel(game, WIDTH, HEIGHT);
		lobby = new LobbyPanel(game);
		jPanel = new MyJPanel[7][7];
		messagePanel = new JPanel();
		
		label = new JLabel();
		
		startButton = new JButton();
		continueButton = new JButton();
		joinButton = new JButton();
		optionButton = new JButton();
		closeButton = new JButton();
		exitButton = new JButton();
		loginButton = new JButton();
		registerButton = new JButton();
		
		uNameTF = new JTextField();
		passTF = new JPasswordField();
		
		frame.setSize(WIDTH, HEIGHT);
		frame.addKeyListener(game.getKeyboardManager());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setFocusable(false);
		frame.setLayout(new GridBagLayout());
		frame.setIconImage(graphicsLoader.loadImage("/textures/figure_brown.png"));
		
		enemySelection.setSize(WIDTH, HEIGHT);
		enemySelection.addKeyListener(game.getKeyboardManager());
		enemySelection.setResizable(false);
		enemySelection.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		enemySelection.setLocationRelativeTo(frame);
		enemySelection.setFocusable(false);
		enemySelection.setLayout(new GridBagLayout());
		enemySelection.setIconImage(graphicsLoader.loadImage("/textures/figure_brown.png"));
		
		panel.setFocusable(true);
		panel.setLayout(null);
		panel.setVisible(true);
		panel.addKeyListener(game.getKeyboardManager());
		
		
		
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7 ; j++) {
					jPanel[i][j] = new MyJPanel();
					jPanel[i][j].setBackground(new Color(i * 30, j * 30, 0));
					jPanel[i][j].setMinimumSize(new Dimension(50, 50));
					jPanel[i][j].setPreferredSize(new Dimension(50, 50));
					jPanel[i][j].setMaximumSize(new Dimension(50, 50));
					jPanel[i][j].setFocusable(false);
					jPanel[i][j].addMouseListener(game.getMouseManager());
					jPanel[i][j].addMouseMotionListener(game.getMouseManager());
					jPanel[i][j].addKeyListener(game.getKeyboardManager());
					jPanel[i][j].setBounds(WIDTH, HEIGHT, 1, 1);
					
					/*
					 * Opaque = false : set background transparent
					 *
					 * Opaque = true : set background visible
					*/
					
					jPanel[i][j].setOpaque(false);
			}
		}
		
		this.addMouseListener(game.getMouseManager());
		this.addMouseMotionListener(game.getMouseManager());
		this.addKeyListener(game.getKeyboardManager());
		this.setFocusable(false);
		
		//neighbors
		
		jPanel[0][0].setBounds(218, 68, SMALLWIDTH, SMALLHEIGHT);
		jPanel[0][0].addNeighbor(jPanel[0][3]);
		jPanel[0][0].addNeighbor(jPanel[3][0]);
		
		jPanel[3][0].setBounds(475, 68, SMALLWIDTH, SMALLHEIGHT);
		jPanel[3][0].addNeighbor(jPanel[0][0]);
		jPanel[3][0].addNeighbor(jPanel[6][0]);
		jPanel[3][0].addNeighbor(jPanel[3][1]);
		
		
		jPanel[6][0].setBounds(732, 68, SMALLWIDTH, SMALLHEIGHT);
		jPanel[6][0].addNeighbor(jPanel[3][0]);
		jPanel[6][0].addNeighbor(jPanel[6][3]);
		
		
		jPanel[0][3].setBounds(218, 325, SMALLWIDTH, SMALLHEIGHT);
		jPanel[0][3].addNeighbor(jPanel[1][3]);
		jPanel[0][3].addNeighbor(jPanel[0][0]);
		jPanel[0][3].addNeighbor(jPanel[0][6]);
		
		jPanel[6][3].setBounds(732, 325, SMALLWIDTH, SMALLHEIGHT);
		jPanel[6][3].addNeighbor(jPanel[5][3]);
		jPanel[6][3].addNeighbor(jPanel[6][0]);
		jPanel[6][3].addNeighbor(jPanel[6][6]);
		
		jPanel[0][6].setBounds(218, 582, SMALLWIDTH, SMALLHEIGHT);
		jPanel[0][6].addNeighbor(jPanel[0][3]);
		jPanel[0][6].addNeighbor(jPanel[3][6]);
		
		jPanel[3][6].setBounds(475, 582, SMALLWIDTH, SMALLHEIGHT);
		jPanel[3][6].addNeighbor(jPanel[0][6]);
		jPanel[3][6].addNeighbor(jPanel[6][6]);
		jPanel[3][6].addNeighbor(jPanel[3][5]);
		
		jPanel[6][6].setBounds(732, 582, SMALLWIDTH, SMALLHEIGHT);
		jPanel[6][6].addNeighbor(jPanel[6][3]);
		jPanel[6][6].addNeighbor(jPanel[3][6]);
		
		jPanel[1][1].setBounds(302, 153, SMALLWIDTH, SMALLHEIGHT);
		jPanel[1][1].addNeighbor(jPanel[1][3]);
		jPanel[1][1].addNeighbor(jPanel[3][1]);
		
		jPanel[3][1].setBounds(475, 153, SMALLWIDTH, SMALLHEIGHT);
		jPanel[3][1].addNeighbor(jPanel[1][1]);
		jPanel[3][1].addNeighbor(jPanel[5][1]);
		jPanel[3][1].addNeighbor(jPanel[3][0]);
		jPanel[3][1].addNeighbor(jPanel[3][2]);
		
		jPanel[5][1].setBounds(647, 153, SMALLWIDTH, SMALLHEIGHT);
		jPanel[5][1].addNeighbor(jPanel[5][3]);
		jPanel[5][1].addNeighbor(jPanel[3][1]);
		
		jPanel[1][3].setBounds(302, 325, SMALLWIDTH, SMALLHEIGHT);
		jPanel[1][3].addNeighbor(jPanel[0][3]);
		jPanel[1][3].addNeighbor(jPanel[2][3]);
		jPanel[1][3].addNeighbor(jPanel[1][1]);
		jPanel[1][3].addNeighbor(jPanel[1][5]);
		
		jPanel[5][3].setBounds(647, 325, SMALLWIDTH, SMALLHEIGHT);
		jPanel[5][3].addNeighbor(jPanel[6][3]);
		jPanel[5][3].addNeighbor(jPanel[4][3]);
		jPanel[5][3].addNeighbor(jPanel[5][1]);
		jPanel[5][3].addNeighbor(jPanel[5][5]);
		
		jPanel[1][5].setBounds(302, 498, SMALLWIDTH, SMALLHEIGHT);
		jPanel[1][5].addNeighbor(jPanel[1][3]);
		jPanel[1][5].addNeighbor(jPanel[3][5]);
		
		jPanel[3][5].setBounds(475, 498, SMALLWIDTH, SMALLHEIGHT);
		jPanel[3][5].addNeighbor(jPanel[1][5]);
		jPanel[3][5].addNeighbor(jPanel[3][6]);
		jPanel[3][5].addNeighbor(jPanel[3][4]);
		jPanel[3][5].addNeighbor(jPanel[5][5]);
		
		jPanel[5][5].setBounds(647, 498, SMALLWIDTH, SMALLHEIGHT);
		jPanel[5][5].addNeighbor(jPanel[3][5]);
		jPanel[5][5].addNeighbor(jPanel[5][3]);
		
		jPanel[2][2].setBounds(388, 239, SMALLWIDTH, SMALLHEIGHT);
		jPanel[2][2].addNeighbor(jPanel[2][3]);
		jPanel[2][2].addNeighbor(jPanel[3][2]);
		
		jPanel[3][2].setBounds(475, 239, SMALLWIDTH, SMALLHEIGHT);
		jPanel[3][2].addNeighbor(jPanel[2][2]);
		jPanel[3][2].addNeighbor(jPanel[4][2]);
		jPanel[3][2].addNeighbor(jPanel[3][1]);
		
		jPanel[4][2].setBounds(562, 239, SMALLWIDTH, SMALLHEIGHT);
		jPanel[4][2].addNeighbor(jPanel[3][2]);
		jPanel[4][2].addNeighbor(jPanel[4][3]);
		
		jPanel[2][3].setBounds(388, 325, SMALLWIDTH, SMALLHEIGHT);
		jPanel[2][3].addNeighbor(jPanel[2][2]);
		jPanel[2][3].addNeighbor(jPanel[2][4]);
		jPanel[2][3].addNeighbor(jPanel[1][3]);
		
		jPanel[4][3].setBounds(562, 325, SMALLWIDTH, SMALLHEIGHT);
		jPanel[4][3].addNeighbor(jPanel[4][2]);
		jPanel[4][3].addNeighbor(jPanel[4][4]);
		jPanel[4][3].addNeighbor(jPanel[5][3]);
		
		jPanel[2][4].setBounds(388, 412, SMALLWIDTH, SMALLHEIGHT);
		jPanel[2][4].addNeighbor(jPanel[2][3]);
		jPanel[2][4].addNeighbor(jPanel[3][4]);
		
		jPanel[3][4].setBounds(475, 412, SMALLWIDTH, SMALLHEIGHT);
		jPanel[3][4].addNeighbor(jPanel[2][4]);
		jPanel[3][4].addNeighbor(jPanel[4][4]);
		jPanel[3][4].addNeighbor(jPanel[3][5]);
		
		jPanel[4][4].setBounds(562, 412, SMALLWIDTH, SMALLHEIGHT);
		jPanel[4][4].addNeighbor(jPanel[4][3]);
		jPanel[4][4].addNeighbor(jPanel[3][4]);
		
		
		messagePanel.setFocusable(false);
		messagePanel.setBounds(325, 7, 350, 37);
		messagePanel.setOpaque(true);
		messagePanel.setBackground(GREENISH);
		
		
		label.setFocusable(false);
		label.setBounds(5, 10, 330, 50);
		label.setForeground(Color.white);
		label.setFont(chalk.deriveFont(22f));
		label.setText(TITLE);
		label.setVisible(true);
		
		ActionListener buttonManager = new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource() == startButton) {
					setRunning(true);
					isOnline = false;
					if(wasOnline)
						oMan.endConnection();
					panel.setOnline(isOnline);
					game.reset(isOnline, true);
				}
				if(e.getSource() == continueButton) {
					State.setCurrentState(game.getGameState());
				}
				if(e.getSource() == closeButton) {
					if(wasOnline) {
						oMan.sendData(new DataPackage(99, 0, 0, 0, 0)); //TODO
						oMan.endConnection();
					}
					System.exit(0);
				}
				if(e.getSource() == joinButton) {
					
					if(rdytologin) {
						
						if(isOnline) {

							return;
							
						}
						
						oMan = new OnlineManager(game);
						oMan.sendData(new LoginPackage(username, password, loginreg?0:1));

						LoginPackage lp = null;
						
						while((lp = (LoginPackage) oMan.receiveData()) == null);
						if(lp.getIfNewAccount() == 2) {
							
							//TODO frame für gegnersuche
							isOnline = true;
							wasOnline = true;
							lobby.setOnlineManager(oMan);
							State.setCurrentState(new LobbyState(game));
							
						}
						
						else {
							
							if(lp.getIfNewAccount() == 3) {

								label.setText("This account already exists.");
								
							} else if(lp.getIfNewAccount() == 4) {
								
								label.setText("Unknown Username");
								
							} else if(lp.getIfNewAccount() == 5) {
								
								label.setText("Wrong Password");
								
							} else if(lp.getIfNewAccount() == 6) {
								
								label.setText("Username too long");
								
							}else if(lp.getIfNewAccount() == 7) {
								
								label.setText("Username too short");
								
							}else if(lp.getIfNewAccount() == 8) {
								
								label.setText("Illegal Username");
								
							} else if(lp.getIfNewAccount() == 9) {
								
								label.setText("User already Online");
								
							}
							
							oMan.endConnection();
							
						}
						/*while(dp == null) {
							
							dp = (DataPackage) oMan.receiveData();
							
						}
						
						if(dp.getStatus() == 98) {
							
							setRunning(true);
							isOnline = true;
							wasOnline = true;
							panel.setOnlineManager(oMan);
							panel.setOnline(isOnline);

							panel.setActiveUser(dp.getFromY() % 2 != 1);
							//Starts a new game, online and which color the local player has
							game.reset(isOnline, dp.getFromX() % 2 == 1);
							
						}*/
						
					
						
					}
					
				}
				if(e.getSource() == optionButton) {
					
					State.setCurrentState(game.getOptionsState());
					
				}
				if(e.getSource() == exitButton) {
					
					if(State.getCurrentState() != game.getOptionsState()) {
						
						if(wasOnline) {
							oMan.sendData(new DataPackage(99, 0, 0, 0, 0)); //TODO
						}
						running = false;
						State.setCurrentState(new LobbyState(game));
						
					} else {
						
						username = uNameTF.getText();
						password = passTF.getText().hashCode();
						State.setCurrentState(game.getMenuState());
						
						if(passTF.getText().split("").length < 5 || passTF.getText().equalsIgnoreCase("Password") || uNameTF.getText().equalsIgnoreCase("Username") || uNameTF.getText().equals("")) {
							rdytologin = false;
						} else {
							rdytologin = true;
						}
						
						
					}
					
				}
				if(e.getSource() == loginButton) {
					
					loginButton.setBackground(GREENISH);
					registerButton.setBackground(REDDISH);
					loginreg = true;
					rdytologin = true;
					
				}
				if(e.getSource() == registerButton) {

					loginButton.setBackground(REDDISH);
					registerButton.setBackground(GREENISH);
					loginreg = false;
					rdytologin = true;
					
				}
				
			}	
		};
		
		FocusListener textfieldListener = new FocusListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void focusGained(FocusEvent e) {
				
				if(e.getSource() == uNameTF) {
					
					if(uNameTF.getText().equals("Username")) {
						
						uNameTF.setText("");
						uNameTF.setBackground(GREENISH);
						
					}
					
				} else if(e.getSource() == passTF) {

					if(passTF.getText().equals("Password")) {
						passTF.setEchoChar('*');
						passTF.setText("");
						passTF.setBackground(GREENISH);
						
						
					}
					
				}
				
			}

			@SuppressWarnings("deprecation")
			@Override
			public void focusLost(FocusEvent e) {
				if(e.getSource() == uNameTF) {
					
					if(uNameTF.getText().isEmpty()) {
						
						uNameTF.setText("Username");
						uNameTF.setBackground(REDDISH);
						
					} else if(uNameTF.getText().equalsIgnoreCase("Username")) {

						uNameTF.setText("Username");
						uNameTF.setBackground(REDDISH);
						
					}
					
				} else if(e.getSource() == passTF) {
					
					if(passTF.getText().isEmpty()) {
						
						passTF.setText("Password");
						passTF.setEchoChar((char) 0);
						passTF.setBackground(REDDISH);
						
					} else if(passTF.getText().equalsIgnoreCase("Password")) {

						passTF.setText("Password");
						passTF.setEchoChar((char) 0);
						passTF.setBackground(REDDISH);
						
					}
					
				}
				
			}
			
			
			
		};
		
		startButton.setBounds(10, 50, 180, 50);
		startButton.setFocusable(false);
		startButton.addActionListener(buttonManager);
		startButton.setBackground(Color.black);
		startButton.setForeground(Color.white);
		startButton.setOpaque(false);
		startButton.setFont(chalk.deriveFont(22f));
		startButton.setFocusPainted(false);
		startButton.setText("New Game");
		
		continueButton.setBounds(10, 110, 180, 50);
		continueButton.setFocusable(false);
		continueButton.setVisible(false);
		continueButton.addActionListener(buttonManager);
		continueButton.setBackground(Color.black);
		continueButton.setForeground(Color.white);
		continueButton.setOpaque(false);
		continueButton.setFont(chalk.deriveFont(22f));
		continueButton.setFocusPainted(false);
		continueButton.setText("Continue");
		
		joinButton.setBounds(10, 170, 180, 50);
		joinButton.setFocusable(false);
		joinButton.addActionListener(buttonManager);
		joinButton.setBackground(Color.black);
		joinButton.setForeground(Color.white);
		joinButton.setOpaque(false);
		joinButton.setFont(chalk.deriveFont(22f));
		joinButton.setFocusPainted(false);
		joinButton.setText("Join");
		
		optionButton.setBounds(10, 230, 180, 50);
		optionButton.setFocusable(false);
		optionButton.addActionListener(buttonManager);
		optionButton.setBackground(Color.black);
		optionButton.setForeground(Color.white);
		optionButton.setOpaque(false);
		optionButton.setFont(chalk.deriveFont(22f));
		optionButton.setFocusPainted(false);
		optionButton.setText("Options");
		
		exitButton.setBounds(10, 290, 180, 50);
		exitButton.setFocusable(false);
		exitButton.addActionListener(buttonManager);
		exitButton.setBackground(Color.black);
		exitButton.setForeground(Color.white);
		exitButton.setOpaque(false);
		exitButton.setFont(chalk.deriveFont(22f));
		exitButton.setFocusPainted(false);
		exitButton.setText("Exit");
		exitButton.setVisible(false);
		
		closeButton.setBounds(10, 350, 180, 50);
		closeButton.setFocusable(false);
		closeButton.addActionListener(buttonManager);
		closeButton.setBackground(Color.black);
		closeButton.setForeground(Color.white);
		closeButton.setOpaque(false);
		closeButton.setFont(chalk.deriveFont(22f));
		closeButton.setFocusPainted(false);
		closeButton.setText("Close");
		
		loginButton.setBounds(610, 90, 150, 70);
		loginButton.setFocusable(true);
		loginButton.addActionListener(buttonManager);
		loginButton.setBackground(REDDISH);
		loginButton.setForeground(Color.white);
		loginButton.setOpaque(true);
		loginButton.setFont(chalk.deriveFont(22f));
		loginButton.setFocusPainted(false);
		loginButton.setText("Login");
		
		registerButton.setBounds(610, 170, 150, 70);
		registerButton.setFocusable(true);
		registerButton.addActionListener(buttonManager);
		registerButton.setBackground(REDDISH);
		registerButton.setForeground(Color.white);
		registerButton.setOpaque(true);
		registerButton.setFont(chalk.deriveFont(22f));
		registerButton.setFocusPainted(false);
		registerButton.setText("Register");
		
		uNameTF.setSize(350, 70);
		uNameTF.setLocation(240, 90);
		uNameTF.setFocusable(true);
		uNameTF.setVisible(true);
		uNameTF.setBackground(REDDISH);
		uNameTF.setForeground(Color.white);
		uNameTF.setFont(chalk.deriveFont(22f));
		uNameTF.setText("Username");
		uNameTF.addFocusListener(textfieldListener);
		
		passTF.setSize(350, 70);
		passTF.setLocation(240, 170);
		passTF.setFocusable(true);
		passTF.setVisible(true);
		passTF.setEchoChar((char) 0);
		passTF.setBackground(REDDISH);
		passTF.setForeground(Color.white);
		passTF.setFont(chalk.deriveFont(22f));
		passTF.setText("Password");
		passTF.addFocusListener(textfieldListener);
		
		panel.add(uNameTF);
		panel.add(passTF);
		panel.add(loginButton);
		panel.add(registerButton);
		messagePanel.add(label);
		
		
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7 ; j++) {
					panel.add(jPanel[i][j]);
			}
		}
		
		enemySelection.add(lobby);
		enemySelection.pack();
		

		panel.add(messagePanel);
		panel.add(startButton);
		panel.add(continueButton);
		panel.add(joinButton);
		panel.add(optionButton);
		panel.add(closeButton);
		panel.add(exitButton);
		frame.add(panel, STANDARDCONSTRAINTS);
		
		frame.setVisible(true);
		frame.pack();
	}

	public void tick() {
		
		continueButton.setEnabled(running);
		
		optionButton.setEnabled(!isOnline);
		startButton.setEnabled(!isOnline);
		
	}
	
	public JFrame getFrame() {
		return this.frame;
	}
	
	public GraphicsJPanel getPanel() {
		return this.panel;
	}
	
	public JPanel getJPanel(int x, int y) {
		return this.jPanel[x][y];
	}
	
	public MyJPanel[][] getJPanel() {
		return this.jPanel;
	}
	
	public JLabel getLabel() {
		return this.label;
	}
	
	public JButton getStartButton() {
		return this.startButton;
	}
	
	public JButton getJoinButton() {
		return this.joinButton;
	}
	
	public JButton getContinueButton() {
		return this.continueButton;
	}
	
	public JButton getCloseButton() {
		return this.closeButton;
	}
	
	public JButton getOptionButton() {
		return this.optionButton;
	}
	
	public JButton getExitButton() {
		return this.exitButton;
	}
	
	public JPanel getMessagePanel() {
		return this.messagePanel;
	}
	
	public void setRunning(boolean b) {
		this.running = b;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public OnlineManager getOnlineManager() {
		return oMan;
	}
	
//Max
	public MyJPanel getLastInRow(MyJPanel jPanelOne, MyJPanel jPanelTwo) {
		if (jPanelOne.isNeighborFrom(jPanelTwo)) {
			
			MyJPanel ret = null;
			int x1 = jPanelOne.getFieldX(), x2 = jPanelTwo.getFieldX(), y1 = jPanelOne.getFieldY(), y2 = jPanelTwo.getFieldY();
		
			String[] lines = {	"00,03,06","00,30,60",
									"11,13,15","11,31,51",
									"22,23,24","22,32,42",
									"30,31,32","03,13,23",
									"34,35,36","43,53,63",
									"42,43,44","24,34,44",
									"51,53,55","15,35,55",
									"60,63,66","06,36,66"
			};
		
			for (int i = 0; i < lines.length; i++) {
			
				if (lines[i].contains(x1 + "" + y1) && lines[i].contains(x2 + "" + y2)) {
				
					int id1 = -1, id2 = -1;
					String [] tempS = lines[i].split(",");
				
					for (int j = 0; j < tempS.length; j++) {
					
						if(tempS[j].contains(x1 + "" +y1)) {
							id1 = j;
						}
						else if(tempS[j].contains(x2 + "" + y2)) { 
							id2 = j;
						}
					}
				
					for (int j = 0; j < tempS.length; j++) {
					
						if(!(j == id1 || j == id2)) {
						
							String[] tempSM = tempS[j].split("");
							ret = jPanel[Integer.parseInt(tempSM[0])][Integer.parseInt(tempSM[1])];
						
						}
					}
				}
			}
		
			if(ret != null && ret.isFigurePlaced()) {
				return ret;
			}
		}
		
		return null;
		
	}
	
	public JTextField getUNameTF() {
		return uNameTF;
	}
	
	public JTextField getPassTF() {
		return passTF;
	}

	public boolean getIfOnline() {
		return isOnline;
	}
	
	public boolean getIfClientActive() {
		return activeClient;
	}
	
	public void setIfClientActive(boolean b) {
		this.activeClient = b;
	}
	
	public JButton getLoginButtton() {
		return loginButton;
	}
	
	public JButton getRegisterButton() {
		return registerButton;
	}
	
	public String getUsername() {
		return username;
	}

	public JFrame getLobbyFrame() {
		return enemySelection;
	}

	public LobbyPanel getLobbyPanel() {
		return lobby;
	}
	
}








