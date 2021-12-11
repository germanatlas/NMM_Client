package mypackages.ninemensmorris.states;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mypackages.ninemensmorris.game.Game;

public class EndState extends State{

	/*
	 * this state is called, 
	 * after the game ended.
	 * 
	 */
	
	private JButton startButton, 
					closeButton, 
					optionButton, 
					continueButton;
	
	private JPanel panel;
	private JLabel label;
	
	private Boolean color;
	
	public EndState(Game game) {
		super(game);
		
		startButton = game.getWindow().getStartButton();
		continueButton = game.getWindow().getContinueButton();
		closeButton = game.getWindow().getCloseButton();
		optionButton = game.getWindow().getOptionButton();
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
		else if(color) {
			label.setText("Black wins!");
		}
		else if(!color) {
			label.setText("White wins!");
		}
		
		startButton.setVisible(true);
		startButton.setOpaque(false);
		
		continueButton.setVisible(true);
		
		closeButton.setVisible(true);
		closeButton.setOpaque(false);
		
		optionButton.setVisible(true);
		optionButton.setOpaque(false);
	
	}
	
	public Boolean getColor(){
		return this.color;
	}

}
