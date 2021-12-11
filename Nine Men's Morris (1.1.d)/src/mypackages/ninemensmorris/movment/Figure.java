package mypackages.ninemensmorris.movment;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import mypackages.ninemensmorris.graphics.GraphicsJPanel;
import mypackages.ninemensmorris.graphics.GraphicsLoader;

public class Figure extends Movable{

	protected GraphicsLoader graphicsLoader = new GraphicsLoader();
	
	private GraphicsJPanel jPanel;
	
	private final BufferedImage playerOne = graphicsLoader.loadImage("/textures/figure_brown.png"),
			                    playerTwo = graphicsLoader.loadImage("/textures/figure_white_new.png");
	
	public Figure(boolean figureType, GraphicsJPanel jPanel, int x, int y) {
		super(figureType, x, y);
		this.jPanel = jPanel;
	}
	
	public void move(int x, int y) {
		this.x = x;
		this.y = y;  
	}
	
	public boolean getColor() {
		return this.figureType;
	}
	
	public void delete() {
		x = -50;
		y = -50;
	}
	
	public int getX() {
		return (int) this.x;
	}
	
	public int getY() {
		return (int) this.y;
	}

	@Override
	public void tick() {
		if(jPanel.getMoveToX() > 0)
			this.move(jPanel.getMoveToX(), jPanel.getMoveToY());
	}
	
	public boolean getFigureType() {
		return this.figureType;
	}

	@Override
	public void render(Graphics g) {
		if (figureType) {
			g.drawImage(playerOne, x, y, null);
		}
		else {
			g.drawImage(playerTwo, x, y, null);
		}
	}

}
