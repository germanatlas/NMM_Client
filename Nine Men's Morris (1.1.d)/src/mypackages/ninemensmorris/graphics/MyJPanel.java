package mypackages.ninemensmorris.graphics;

import java.util.ArrayList;

import javax.swing.JPanel;

import mypackages.ninemensmorris.movment.Figure;

public class MyJPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	/*
	 * acts as invisible Button
	 * for Movements
	 * 
	 * saves if figure is placed
	 * and ,in that case, its color
	 * 
	 */
	
	private Figure figure;
	private boolean figurePlaced;
	
	private ArrayList<MyJPanel> neighbors;
	
	public MyJPanel() {
		neighbors = new ArrayList<MyJPanel>();
		figure = null;
		figurePlaced = false;
	}
	
	public void setFigure(Figure figure) {
		this.figure = figure;
		this.figurePlaced = true;
	}
	
	public void delFigure() {
		figure.delete();
		this.figure = null;
		this.figurePlaced = false;
	}
	
	public Figure getFigure() {
		return this.figure;
	}
	
	public boolean isFigurePlaced() {
		return this.figurePlaced;
	}
	
	public boolean isNeighborFrom(MyJPanel panel){
		return this.neighbors.contains(panel);
	}
	
	public void addNeighbor(MyJPanel neighbor) {
		this.neighbors.add(neighbor);
	}
	
	public ArrayList<MyJPanel> getNeighbors(){
		return this.neighbors;
	}
	
	/*
	 * getFieldX / getFieldY convert
	 * graphic coordinates to field
	 * coordinates
	 * 
	 */
	
	public int getFieldX() {
		
		if (getX() < 270) {
			return 0;
		}
		else if (getX() < 350) {
			return 1;
		}
		else if(getX() < 460) {
			return 2;
		}
		else if(getX() < 500) {
			return 3;
		}
		else if(getX() < 600) {
			return 4;
		}
		else if(getX() < 700) {
			return 5;
		}
		else {
			return 6;
		}
		
	}
	
	public int getFieldY() {
		
		if (getY() < 100) {
			return 0;
		}
		else if (getY() < 200) {
			return 1;
		}
		else if(getY() < 300) {
			return 2;
		}
		else if(getY() < 400) {
			return 3;
		}
		else if(getY() < 470) {
			return 4;
		}
		else if(getY() < 550) {
			return 5;
		}
		else {
			return 6;
		}
		
	}
	

}
