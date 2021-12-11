package mypackages.ninemensmorris.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mypackages.ninemensmorris.game.Game;

public class OptionsState extends State {

	
	private JPanel 	panel,
					optionsPanel;
	
	private JButton startButton, 
					continueButton, 
					optionButton, 
					closeButton,
					joinButton,
					exitButton;
	
	private JTextField addrTF;

	private boolean escapePressed;
	
	public OptionsState(Game game) {
		super(game);
	
		startButton = game.getWindow().getStartButton();
		continueButton = game.getWindow().getContinueButton();
		joinButton = game.getWindow().getJoinButton();
		optionButton = game.getWindow().getOptionButton();
		closeButton = game.getWindow().getCloseButton();
		exitButton = game.getWindow().getExitButton();
		
		addrTF = new JTextField();
		
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
			game.getWindow().setAddress(getTFContent());
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
		
	}
	
	public String getTFContent() {
		
		return "Test";
		
	}

}
