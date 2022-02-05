package main.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.game.Game;

public class OptionsState extends State {

	
	/*
	 * TODO
	 * -----
	 * |	IP
	 * |	LOGIN / REGISTRATION
	 * ----- Deactivated when connected
	 * 
	 * Different Designs?
	 * 
	 * 
	 */
	
	private JPanel 	panel;
	
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

	private boolean escapePressed;
	
	private final int id = 3;
	
	public OptionsState(Game game) {
		super(game);
	
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
		panel.setVisible(false);
	}

	@Override
	public void tick() {
		
		if(!escapePressed && game.getKeyboardManager().isEscapePressed()){
			escapePressed = true;
		}
		if(escapePressed && !game.getKeyboardManager().isEscapePressed()) {
			escapePressed = false;
			State.setCurrentState(game.getMenuState());
		}
		
		panel.setVisible(false);
		
	}

	@Override
	public void render(Graphics g) {
		
		startButton.setVisible(false);
		continueButton.setVisible(false);
		joinButton.setVisible(false);
		optionButton.setVisible(false);
		closeButton.setVisible(false);
		exitButton.setVisible(true);
		
		if(!game.getWindow().getIfOnline() && !game.getWindow().isRunning()) {
			
			uNameTF.setVisible(true);
			passTF.setVisible(true);
			loginButton.setVisible(true);
			registerButton.setVisible(true);
			
		}
		
	}
	
	public int getID() {
		return this.id;
	}

}
