package mypackages.ninemensmorris.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardManager implements KeyListener{
	
	/*
	 * scans for keyboard inputs
	 * 
	 * this is for scanning for escape presses
	 * so pause can be entered.
	 * 
	 */
	
	private boolean escapePressed;
	
	public KeyboardManager() {
		this.escapePressed = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			escapePressed = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			escapePressed = false;
		}
	}
	
	public boolean isEscapePressed() {
		return this.escapePressed;
	}
	
	public void setEscapePressed(boolean b) {
		this.escapePressed = b;
	}

}
