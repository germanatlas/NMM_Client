package main.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import main.game.Game;
import main.graphics.LobbyPanel;

public class LobbyState extends State{
	
	private JFrame lobbyFrame;
	private LobbyPanel lobbyPanel;
	
	private JButton startButton, 
					continueButton, 
					optionButton, 
					closeButton,
					joinButton,
					exitButton,
					loginButton,
					registerButton;
	
	public LobbyState(Game game) {
		super(game);
		

		startButton = game.getWindow().getStartButton();
		continueButton = game.getWindow().getContinueButton();
		joinButton = game.getWindow().getJoinButton();
		optionButton = game.getWindow().getOptionButton();
		closeButton = game.getWindow().getCloseButton();
		exitButton = game.getWindow().getExitButton();
		loginButton = game.getWindow().getLoginButtton();
		registerButton = game.getWindow().getRegisterButton();
		
		lobbyFrame = game.getWindow().getLobbyFrame();
		lobbyPanel = game.getWindow().getLobbyPanel();
		lobbyFrame.setVisible(true);
		lobbyPanel.setVisible(true);
		lobbyPanel.run();
		
	}

	@Override
	public void tick() {
		
		
	}

	@Override
	public void render(Graphics g) {

		lobbyFrame.setVisible(true);
		startButton.setVisible(false);
		continueButton.setVisible(false);
		joinButton.setVisible(true);
		optionButton.setVisible(false);
		exitButton.setVisible(true);
		closeButton.setVisible(true);
		loginButton.setVisible(false);
		registerButton.setVisible(false);
		
		
	}

}
