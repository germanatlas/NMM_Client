package main.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.game.Game;

public class EndState extends State{

	/*
	 * this state is called, 
	 * after the game ended.
	 * 
	 */
	
	private JButton startButton, 
					closeButton, 
					optionButton, 
					continueButton,
					joinButton,
					exitButton;
	
	private JTextField	inetTF,
						uNameTF,
						passTF;
	
	private JPanel panel;
	private JLabel label;
	
	private Boolean color;
	
	public EndState(Game game) {
		super(game);
		
		startButton = game.getWindow().getStartButton();
		continueButton = game.getWindow().getContinueButton();
		closeButton = game.getWindow().getCloseButton();
		optionButton = game.getWindow().getOptionButton();
		exitButton = game.getWindow().getExitButton();
		joinButton = game.getWindow().getJoinButton();

		inetTF = game.getWindow().getinetTF();
		uNameTF = game.getWindow().getUNameTF();
		passTF = game.getWindow().getPassTF();
		
		panel = game.getWindow().getMessagePanel();
		label = game.getWindow().getLabel();
		
	}
	
	public EndState(Game game, boolean color) {
		this(game);
		this.color = color;
	}

	@Override
	public void tick() {
		
		
		
	}

	@Override
	public void render(Graphics g) {
		
		panel.setVisible(true);
		
		if(color == null) {
			label.setText("Stalemate!");
		}
		/*else if(color) {
			label.setText("Black wins!");
		}
		else if(!color) {
			label.setText("White wins!");
		}*/
		
		if(!(game.getWindow().getIfOnline() && game.getWindow().isRunning())) {
			
			startButton.setVisible(true);
			startButton.setOpaque(false);
			
		} else {
			
			exitButton.setVisible(true);
			exitButton.setOpaque(false);
			
		}
		
		continueButton.setVisible(true);
		
		closeButton.setVisible(true);
		closeButton.setOpaque(false);
		
		joinButton.setVisible(true);
		joinButton.setOpaque(false);
		
		optionButton.setVisible(true);
		optionButton.setOpaque(false);
		
		inetTF.setVisible(false);
		uNameTF.setVisible(false);
		passTF.setVisible(false);
	
	}
	
	public Boolean getColor(){
		return this.color;
	}

}
