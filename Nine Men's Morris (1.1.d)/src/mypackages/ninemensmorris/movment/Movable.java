package mypackages.ninemensmorris.movment;

import java.awt.Graphics;

public abstract class Movable {
	
	protected int x, y;
	protected boolean figureType;
	
	public Movable(boolean figureType, int x, int y) {
		this.x = x;
		this.y = y;
		this.figureType = figureType;
	}

	public abstract void tick();
	
	public abstract void render(Graphics g);
	
}
