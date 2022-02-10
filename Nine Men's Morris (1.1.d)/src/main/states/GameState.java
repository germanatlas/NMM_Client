package main.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.game.Game;
import main.graphics.GraphicsJPanel;

public class GameState extends State{
	
	/*
	 * this state is called,
	 * when the game is running
	 * and focused.
	 * 
	 */
	
	private GraphicsJPanel jPanel;
	
	private JButton startButton, 
					closeButton, 
					optionButton, 
					joinButton,
					continueButton,
					exitbButton;
	
	private JTextField	uNameTF,
						passTF;
	
	private JPanel panel;
	
	private JFrame lobbyFrame;
	
	private boolean	escapePressed;
	
	public GameState(Game game) {
		super(game);
		
		this.jPanel = game.getWindow().getPanel();
		this.startButton = game.getWindow().getStartButton();
		this.continueButton = game.getWindow().getContinueButton();
		this.joinButton = game.getWindow().getJoinButton();
		this.closeButton = game.getWindow().getCloseButton();
		this.optionButton = game.getWindow().getOptionButton();
		this.exitbButton = game.getWindow().getExitButton();
		
		lobbyFrame = game.getWindow().getLobbyFrame();
		
		uNameTF = game.getWindow().getUNameTF();
		passTF = game.getWindow().getPassTF();
		
		this.panel = game.getWindow().getMessagePanel();
		
		escapePressed = false;
		
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

		panel.setVisible(true);
		jPanel.tick();
		
	}

	@Override
	public void render(Graphics g) {
		
		this.startButton.setVisible(false);
		
		this.continueButton.setVisible(false);
		
		this.joinButton.setVisible(false);
		
		this.closeButton.setVisible(false);
		
		this.optionButton.setVisible(false);
		
		this.exitbButton.setVisible(false);

		panel.setVisible(true);
		
		uNameTF.setVisible(false);
		passTF.setVisible(false);
		
		lobbyFrame.setVisible(false);
		
	}

}
