package main.states;

import java.awt.Graphics;

import javax.swing.JButton;

import main.game.Game;

public class LobbyState extends State{

	private JButton[] enemy;
	
	private final int id = 4;
	
	public LobbyState(Game game) {
		super(game);
		
		this.enemy = null; //TODO
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		
	}
	
	public int getID() {
		return this.id;
	}

}
