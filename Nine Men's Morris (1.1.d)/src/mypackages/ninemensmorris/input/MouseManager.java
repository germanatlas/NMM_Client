package mypackages.ninemensmorris.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import mypackages.ninemensmorris.graphics.MyJPanel;

public class MouseManager implements MouseListener, MouseMotionListener{
	
	/*
	 * scans for mouse inputs, so
	 * the pieces can be placed
	 * on the board
	 * 
	 */
	
	private boolean leftPressed, 
					rightPressed;
	
	private int posX, 
				posY, 
				panelPressedX, 
				panelPressedY;
	
	private MyJPanel panelPressed;

	public MouseManager() {
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
			
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getSource() instanceof MyJPanel) {
				leftPressed = true;
				panelPressed = (MyJPanel) e.getSource();
				panelPressedX = panelPressed.getX();
				panelPressedY = panelPressed.getY();
			}
		}
		else
			if(e.getButton() == MouseEvent.BUTTON3)
				rightPressed = true;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		if(e.getButton() == MouseEvent.BUTTON1)
			leftPressed = false;
		else
			if(e.getButton() == MouseEvent.BUTTON3)
				rightPressed = false;
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		posX = e.getX();
		posY = e.getY();
		
	}
	
	public boolean isLeftPressed() {
		return this.leftPressed;
	}
	
	public boolean isRightPressed() {
		return this.rightPressed;
	}
	
	public int getPosX() {
		return this.posX;
	}
	
	public int getPosY() {
		return this.posY;
	}
	
	public int getPanelPressedX() {
		return this.panelPressedX;
	}
	
	public int getPanelPressedY() {
		return this.panelPressedY;
	}
	
	public MyJPanel getPanelPressed() {
		return this.panelPressed;
	}

}