package main.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;

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
					exitButton;
	
	public MenuState(Game game) {
		super(game);
		
	
		startButton = game.getWindow().getStartButton();
		continueButton = game.getWindow().getContinueButton();
		joinButton = game.getWindow().getJoinButton();
		optionButton = game.getWindow().getOptionButton();
		closeButton = game.getWindow().getCloseButton();
		exitButton = game.getWindow().getExitButton();
		
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
		optionButton.setVisible(true);
		closeButton.setVisible(true);
		//exitButton.setVisible(false);
		
		if(game.getWindow().getIfOnline() && game.getWindow().isRunning()) {
			exitButton.setVisible(true);
			startButton.setVisible(false);
		} else {
			exitButton.setVisible(false);
			startButton.setVisible(true);
		}
	
	}
	

}
