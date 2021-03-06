package main.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.game.Game;

public class MenuState extends State {
	
	/*
	 * this state is the start state
	 * and while running it is called 
	 * when escape is pressed
	 * 
	 */
	
	private JPanel panel;
	
	private JButton startButton, 
					continueButton, 
					optionButton, 
					closeButton,
					joinButton,
					exitButton,
					loginButton,
					registerButton;
	
	private JTextField	uNameTF,
						passTF;
	
	private JFrame lobbyFrame;
	
	public MenuState(Game game) {
		super(game);
		
	
		lobbyFrame = game.getWindow().getLobbyFrame();
		startButton = game.getWindow().getStartButton();
		continueButton = game.getWindow().getContinueButton();
		joinButton = game.getWindow().getJoinButton();
		optionButton = game.getWindow().getOptionButton();
		closeButton = game.getWindow().getCloseButton();
		exitButton = game.getWindow().getExitButton();
		loginButton = game.getWindow().getLoginButtton();
		registerButton = game.getWindow().getRegisterButton();

		uNameTF = game.getWindow().getUNameTF();
		passTF = game.getWindow().getPassTF();
		
		panel = game.getWindow().getMessagePanel();
		panel.setVisible(true);
		
	}


	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics g) {
		
		continueButton.setVisible(true);
		joinButton.setVisible(true);
		closeButton.setVisible(true);
		//exitButton.setVisible(false);
		
		if(!game.getWindow().getIfOnline()) {
			
			optionButton.setVisible(true);
			
		}
		
		if(game.getWindow().getIfOnline() && game.getWindow().isRunning()) {
			exitButton.setVisible(true);
			startButton.setVisible(false);
		} else {
			exitButton.setVisible(false);
			startButton.setVisible(true);
		}
		
		uNameTF.setVisible(false);
		passTF.setVisible(false);
		loginButton.setVisible(false);
		registerButton.setVisible(false);
		panel.setVisible(true);
		lobbyFrame.setVisible(false);
	
	}

}
