package mypackages.ninemensmorris.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;

import mypackages.ninemensmorris.game.Game;
import mypackages.ninemensmorris.graphics.GraphicsJPanel;
import mypackages.ninemensmorris.graphics.OnlineGraphicsJPanel;

public class GameState extends State{
	
	/*
	 * this state is called,
	 * when the game is running
	 * and focused.
	 * 
	 */
	
	private GraphicsJPanel jPanel;
	private OnlineGraphicsJPanel ojPanel;
	
	private JButton startButton, 
					closeButton, 
					optionButton, 
					joinButton,
					continueButton;
	
	private JPanel panel;
	
	private boolean	escapePressed,
					isOnline;
	
	public GameState(Game game) {
		super(game);
		
		this.jPanel = game.getWindow().getPanel();
		this.ojPanel = game.getWindow().getOnlinePanel();
		this.startButton = game.getWindow().getStartButton();
		this.continueButton = game.getWindow().getContinueButton();
		this.joinButton = game.getWindow().getJoinButton();
		this.closeButton = game.getWindow().getCloseButton();
		this.optionButton = game.getWindow().getOptionButton();
		this.isOnline = game.getWindow().getIfOnline();
		
		
		
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
		
		if(isOnline) {
			ojPanel.tick();
		} else {
			jPanel.tick();
		}
		panel.setVisible(true);
		
	}

	@Override
	public void render(Graphics g) {
		
		this.startButton.setVisible(false);
		
		this.continueButton.setVisible(false);
		
		this.joinButton.setVisible(false);
		
		this.closeButton.setVisible(false);
		
		this.optionButton.setVisible(false);
		
	}

}
