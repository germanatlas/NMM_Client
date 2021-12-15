package main;

import main.game.Game;

//starts the game thread

public class Launcher {
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}
}
