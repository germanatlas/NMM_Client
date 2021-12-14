package mypackages.ninemensmorris.graphics;
/*
 * every variable should be
 * sent to the server after input
 * and then be handled
 * by the server and
 * sent to the client
 * 
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import mypackages.ninemensmorris.game.Game;
import mypackages.ninemensmorris.movment.Figure;
import mypackages.ninemensmorris.movment.OnlineFigure;
import mypackages.ninemensmorris.networking.DataPackage;
import mypackages.ninemensmorris.networking.OnlineManager;
import mypackages.ninemensmorris.states.EndState;
import mypackages.ninemensmorris.states.GameState;
import mypackages.ninemensmorris.states.MenuState;
import mypackages.ninemensmorris.states.State;

public class OnlineGraphicsJPanel extends JPanel{

	static final long serialVersionUID = 1L;
	
	private Game game;
	private State endState;
	
	private GraphicsLoader graphicsLoader = new GraphicsLoader();
	
	private final int WIDTH = 1000, 
					  HEIGHT = 700;
	
	private final BufferedImage WOOD = graphicsLoader.loadImage("/textures/wood.png");
	
	private int moveToX, 
				moveToY, 
				repetition, 
				roundsWithoutMill;
	
	private int count, 
				playerOneFigureCount, 
				playerTwoFigureCount,
				endID;
	
	private Random random;
	
	private List<String> repetitiveField;
	
	private boolean color, 
					mill, 
					lastMill, 
					alreadyAdded, 
					alreadyPressed,
					activeUser,
					stalemateOnline;
	
	private OnlineFigure[]	playerOne, 
					  		playerTwo;
	
	private Figure isMoving;
	
	private MyJPanel cursor, 
					 tempPanel;
	
	private OnlineManager oMan;
	
	public OnlineGraphicsJPanel(Game game, int WIDTH, int HEIGHT) {
		
		this.game = game;
		
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		
		this.random = new Random();
		
		this.repetition = 0;
		this.repetitiveField = new ArrayList<String>();
		this.roundsWithoutMill = 0;
		this.playerOneFigureCount = 9;
		this.playerTwoFigureCount = 9;
		this.count = -1;
		this.endID = 0;
		
		this.mill = false;
		this.color = random.nextBoolean();
		this.lastMill = false;
		this.alreadyPressed = false;
		this.alreadyAdded = false;
		
		this.isMoving = null;
		this.cursor = null;
		this.tempPanel = null;
		
		this.playerOne = new OnlineFigure[9];
		this.playerTwo = new OnlineFigure[9];
		
		for(int i = 0; i < 9; i++) {
			playerOne[i] = new OnlineFigure(true, this, 140, 50 + i * 70);
			playerTwo[i] = new OnlineFigure(false, this, 820, 50 + i * 70);
		}
		
	}
	
	public void tick() {
	
	//Daniel
		
		if(!game.getMouseManager().isLeftPressed()) {
			alreadyPressed = false;
		}
		
		if(!activeUser) {
			
			getOnlineData();
			
		}
		
		if(!mill) {
			//PlacingPhase
		
			
			if(!alreadyPressed) {
				
				if(count < 17 && game.getMouseManager().isLeftPressed() && !game.getMouseManager().getPanelPressed().isFigurePlaced() && activeUser) {
					
					tempPanel = game.getMouseManager().getPanelPressed();
					moveToX = game.getMouseManager().getPanelPressedX() + 5;
					moveToY = game.getMouseManager().getPanelPressedY() + 5;
					
					count++;
					
					if (activeUser) {
						tempPanel.setFigure(playerOne[count / 2]);
						if (!mill) {
							playerOne[count / 2].move(moveToX, moveToY);
						}

						sendOnlineData(0, isMoving.getX() + "" + isMoving.getY(), moveToX + "" + moveToY);
						
					}
					alreadyPressed = true;
				}
				
			}
		
			//choose moving Piece
			if(!alreadyPressed) {
				
				/*
				 * if(server) receive;
				 * else {send to server};
				 */
				if(count >= 17 && game.getMouseManager().isLeftPressed() && game.getMouseManager().getPanelPressed().isFigurePlaced()) {
					alreadyPressed = true;
					
					//black
					if (color && game.getMouseManager().getPanelPressed().getFigure().getFigureType()) {
						isMoving = game.getMouseManager().getPanelPressed().getFigure();
						cursor = game.getMouseManager().getPanelPressed();
					}
				
					//white
					if(!color && !game.getMouseManager().getPanelPressed().getFigure().getFigureType()) {
						isMoving = game.getMouseManager().getPanelPressed().getFigure();
						cursor = game.getMouseManager().getPanelPressed();
					}
				}
			}
			
			//MovePhase
			if(isMoving != null) {
				//black
				if(playerOneFigureCount > 3 && color && !game.getMouseManager().getPanelPressed().isFigurePlaced() && activeUser) {
					
					tempPanel = game.getMouseManager().getPanelPressed();
				
					
					if(tempPanel.isNeighborFrom(cursor)) {
						
						moveToX = game.getMouseManager().getPanelPressedX() + 5;
						moveToY = game.getMouseManager().getPanelPressedY() + 5;
						
						isMoving.move(moveToX, moveToY);
						tempPanel.setFigure(isMoving);
						
						cursor.delFigure();
						isMoving = null;
						
//Max
						if(checkMill(tempPanel)) {
							
							roundsWithoutMill = 0;
							lastMill = color;
							mill = true;
						}
						else if(lastMill == color) {
							
							roundsWithoutMill++;
						}
						
						tempPanel = null;
						
						if(checkForRepetition(color)) {
							repetition++;
						}
						else {
							repetition = 0;
						}
					}
				}
				
			}

//Daniel
			//JumpPhase
			if(playerOneFigureCount == 3 && !alreadyPressed && game.getMouseManager().isLeftPressed()) {
				
				if(isMoving != null) {
				
					if(color && !game.getMouseManager().getPanelPressed().isFigurePlaced()) {
						
						tempPanel = game.getMouseManager().getPanelPressed();
					
						moveToX = game.getMouseManager().getPanelPressedX() + 5;
						moveToY = game.getMouseManager().getPanelPressedY() + 5;

						sendOnlineData(1, isMoving.getX() + "" + isMoving.getY(), moveToX + "" + moveToY);
						isMoving.move(moveToX, moveToY);
						tempPanel.setFigure(isMoving);
						
						cursor.delFigure();
						isMoving = null;

//Max					
						if (checkMill(tempPanel)) {
							
							lastMill = color;
							mill = true;
							//6 if black has mill, 5 if white has mill
							sendOnlineData(color?6:5, isMoving.getX() + "" + isMoving.getY(), moveToX + "" + moveToY);
						}
						else if(lastMill == color) {
							roundsWithoutMill++;
						}
						
						if(!mill) {
							sendOnlineData(1, isMoving.getX() + "" + isMoving.getY(), moveToX + "" + moveToY);
						}
						
						tempPanel = null;
						
						if(checkForRepetition(color)) {

							repetition++;
						}
						else {
							repetition = 0;
						}
						
					}
				}
			}
			
//Max
			//checkForEnd
			if(checkStalemate()) {
				endState = new EndState(game);
				State.setCurrentState(endState);
			}
			if(playerOneFigureCount > 3 && count > 17 && !checkForLegalMoves(color)) {
				endState = new EndState(game, !color);
				State.setCurrentState(endState);
			}
			else if(playerOneFigureCount < 3) {
				endState = new EndState(game, !color);
				State.setCurrentState(endState);
			}
			
			/*if(color) {
				
				
			}
			else {
				
				if(checkStalemate()) {
					endState = new EndState(game);
					State.setCurrentState(endState);
				}
				if(playerTwoFigureCount > 3 && count > 17 && !checkForLegalMoves(color)) {
					endState = new EndState(game, !color);
					State.setCurrentState(endState);
				}
				else if(playerTwoFigureCount < 3) {
					endState = new EndState(game, !color);
					State.setCurrentState(endState);
				}
			}*/
			
		}
		
//Daniel
		//delete Stone
		/*
		 * server:
		 * - send variables to clients
		 * - receive request of deletion of stone
		 * - handle request
		 * - answer with deleted and new variables or
		 *   can't be deleted
		 *   
		 * client:
		 * - choose stone to be deleted
		 * - send to server
		 * - receive answer and renew Variables
		 *   or choose new stone if the other
		 *   couldn't be deleted
		 * 
		 */
		else if (!alreadyPressed && game.getMouseManager().isLeftPressed() && game.getMouseManager().getPanelPressed().isFigurePlaced())
		{
			alreadyPressed = true;
			if(withoutMill(!color)) {
				if(!checkMill(game.getMouseManager().getPanelPressed())) {
					
					while (!removeFigure(game.getMouseManager().getPanelPressed())) {
					}
					mill = false;
					
				}
			}
			else if(!withoutMill(!color)){

				while (!removeFigure(game.getMouseManager().getPanelPressed())) {
				}
				mill = false;
				
			}
		}
		
//Max		
		//checks for mill while placing phase
		if(count >= -1 && count <= 17) {
			if(tempPanel != null) {
				
				if(color) {
					mill = checkMill(tempPanel);
					if(mill) {
						roundsWithoutMill = 0;
						count--;
						
						lastMill = color;
					}
					else if(lastMill == !color) {
						roundsWithoutMill++;
					}
				}
				
				else {
					mill = checkMill(tempPanel);
					if(mill) {
						roundsWithoutMill = 0;
						count--;
						
						lastMill = color;
					}
					else if(lastMill == !color) {
						roundsWithoutMill++;
					}
				}
				
			}
			tempPanel = null;
		}
		
		if(count == 17) { 
			count++;
		}

		//first field that can be repeated (should be saved in the server and if needed sent to the clients)
				if(count > 17 && !alreadyAdded) {
					
					String s = getStringFromBoard();
					/*save in server and send to client if needed*/ repetitiveField.add(s);
					/*handled by server*/ alreadyAdded = true;
				}

//Daniel
		//messages		
		String msg = "";
				
		if(!color) {
			
			if(activeUser) {
				msg += "White (YOU): ";
			} else {
				
				msg += "Black (NMY): ";
			}
			
		} else {
			
			if(activeUser) {
				msg += "Black (YOU): ";
			} else {
				
				msg += "White (NMY): ";
			}
			
		}
		
		if(mill) {
			msg += "mill";
		} else {
			
			if(count < 17) {
				msg += "place a stone";
			} else {
				msg += "choose a stone";
			}
			
		}
		
		
		
		game.getWindow().getLabel().setText(msg);
				
		/*		
		/*
		 * shall be handled by server
		 * and sent to the clients
		 * and afterwards shown in the clients		
		 
		if(/*for example this shall be received by the client from the server count < 17) {
			if(color) {
				if(!mill) {
					game.getWindow().getLabel().setText("Black: place a stone");
				}
				else {
					game.getWindow().getLabel().setText("Black: mill");
				}
			}
			else {
				if(!mill) {
					game.getWindow().getLabel().setText("White: place a stone");
				}
				else {
					game.getWindow().getLabel().setText("White: mill");
				}
			}
		}
		if(count > 17) {
			if(isMoving == null) {
				if(color) {
					if(!mill) {
						game.getWindow().getLabel().setText("Black: choose a stone");
					}
					else {
						game.getWindow().getLabel().setText("Black: mill");
					}
				}
				else{
					if(!mill) {
						game.getWindow().getLabel().setText("White: choose a stone");
					}
					else {
						game.getWindow().getLabel().setText("White: mill");
					}
				}
			}
			else {
				if(color) {
					if(!mill) {
						game.getWindow().getLabel().setText("Black: place a stone");
					}
					else {
						game.getWindow().getLabel().setText("Black: mill");
					}
				}
				else{
					if(!mill) {
						game.getWindow().getLabel().setText("White: place a stone");
					}
					else {
						game.getWindow().getLabel().setText("White: mill");
					}
				}
			}
		}
		*/
		/*this can be done by server and clients*/ this.repaint();
		
	}
	
	private void sendOnlineData(int status, String fromCoords, String toCoords) {
		
		String s = fromCoords + "_" + toCoords;
		oMan.sendData(new DataPackage(status, s));
		activeUser = false;
		
	}
	
	private void getOnlineData() {
		
		DataPackage dp = oMan.receiveData();
		drawBoardFromString(dp.getMove());
		
		if(dp.getStatus() <= 1) {
			
			if(dp.getStatus() == 0) {
				//Placing Phase
				count++;
				
			}
			
			//TODO
			//Spielstatus anpassen
			activeUser = true;
			
		} else if(dp.getStatus() <= 4) {

			if(dp.getStatus() == 2) {
				//Stalemate / Draw
				endState = new EndState(game);
				State.setCurrentState(endState);
				
			} else if(dp.getStatus() == 3) {
				//White wins
				endState = new EndState(game, false);
				State.setCurrentState(endState);
				
			} else if(dp.getStatus() == 4) {
				//Black wins
				endState = new EndState(game, true);
				State.setCurrentState(endState);
				
			}
			
			//TODO
			//End Game Accordingly
			endID = dp.getStatus();
			activeUser = false;
			
		} else if(dp.getStatus() <= 6) {
			
			//TODO
			//Mills
			roundsWithoutMill = 0;
			lastMill = !color;
			
			activeUser = false;
			
		} else if(dp.getStatus() == 99) {
			
			boolean thisColor = Integer.parseInt(dp.getMove())%10 == 1;
			reset(thisColor);
			
		}
		
	}
	
	private String getStringFromBoard() {

		MyJPanel[][] f = game.getWindow().getJPanel();
		String s = "";
		
		for (int i = 0; i < f.length; i++) {
			for (int j = 0; j < f[i].length; j++) {
				if (f[i][j].isFigurePlaced()) {
					if (f[i][j].getFigure().getColor()) {
						
						s += "2";
						
					}
					else {
						
						s += "1";
						
					}
				}
				else {
					
					s += "0";
					
				}
			}
		}
		
		return s;
		
	}
	
	private void drawBoardFromString(String move) {
		
		MyJPanel[][] f = game.getWindow().getJPanel();
		String[] data = move.split("_");
		
		String[] from = data[0].split("");
		String[] to = data[1].split("");
		
		
		
		if(data[1].equals("99")) {
			
			playerOneFigureCount--;
			f[Integer.parseInt(from[0])][Integer.parseInt(from[1])].delFigure();
			
		} else {
			
			f[Integer.parseInt(from[0])][Integer.parseInt(from[1])].getFigure().move(Integer.parseInt(to[0]), Integer.parseInt(to[1]));
			
		}
		
		
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//background
		Color beige=new Color(254, 255, 209), brown = new Color(48,32,24);
		g.drawImage(WOOD, 0, 0, null);
		
		
		//begin board
		
			g.setColor(brown);
			g.fillRect(200, 50, WIDTH - 400, HEIGHT - 100);
			g.setColor(beige);
			g.fillRect(220, 70, WIDTH - 440, HEIGHT - 140);
		
			//begin borders
				//outer border
				g.setColor(Color.black);
				g.fillRect(240, 90, WIDTH - 480, HEIGHT - 180);
				g.setColor(beige);
				g.fillRect(246, 96, WIDTH - 492, HEIGHT - 192);
		
				//mid border
				g.setColor(Color.black);
				g.fillRect(325, 175, WIDTH - 650, HEIGHT - 350);
				g.setColor(beige);
				g.fillRect(331, 181, WIDTH - 662, HEIGHT - 362);
		
				//inner border
				g.setColor(Color.black);
				g.fillRect(410, 260, WIDTH - 820, HEIGHT - 520);
			
			//end borders
		
			//begin lines
			
				//horizontal
				g.setColor(Color.black);
				g.fillRect(246, 347, WIDTH - 492, HEIGHT - 694);	
				
				//perpendicular
				g.fillRect(497, 96, WIDTH - 994, HEIGHT - 192);
			
			//end lines

			g.setColor(beige);
			g.fillRect(416, 266, WIDTH - 832, HEIGHT - 532);
			g.setColor(Color.black);
		
	//end board
		if(State.getCurrentState() instanceof GameState || State.getCurrentState() instanceof EndState) {	
			for (int i = 0; i < 9; i++) {
				if(playerOne[i] != null) {
					playerOne[i].render(g);
				}
				if(playerTwo[i] != null) {
					playerTwo[i].render(g);
				}
			}
		}
		
		
		
		if(State.getCurrentState() instanceof MenuState && game.getWindow().isRunning()){
			for (int i = 0; i < 9; i++) {
				if(playerOne[i] != null) {
					playerOne[i].render(g);
				}
				if(playerTwo[i] != null) {
					playerTwo[i].render(g);
				}
			}
		}
		
		//game ended
		else if(State.getCurrentState() instanceof EndState) {
			game.getWindow().setRunning(false);
			EndState tempState = (EndState) endState;
			if(tempState.getColor() != null && tempState.getColor() || endID == 4) {
				//TODO Send Info
				if(color) {
						
					game.getWindow().getLabel().setText("Black (YOU) wins!");
						
				} else {

					game.getWindow().getLabel().setText("Black (NMY) wins!");
						
				}
				
			} else if(endID == 3) {
				
				if(!color) {
					
					game.getWindow().getLabel().setText("White (YOU) wins!");
					
				} else {

					game.getWindow().getLabel().setText("White (NMY) wins!");
					
				}
				
			}
		}
		
		
	}
	
	public boolean removeFigure(MyJPanel m) {
		
		if (m.getFigure() == null) {
			
			return false;
			
		}
		
		if(m.getFigure().getColor() == color) {
			
			return false;
			
		}
		
		if(m.getFigure().getColor()) {
			playerOneFigureCount--;
		}
		else {
			playerTwoFigureCount--;
		}
		
		m.getFigure().delete();
		m.delFigure();
		count++;
		return true;
	}

	public int getMoveToX() {
		return this.moveToX;
	}
	
	public int getMoveToY() {
		return this.moveToY;
	}
	
	//resets the graphics to play a new game
	
	public void reset(boolean c) {
		this.repetition = 0;
		this.repetitiveField = new ArrayList<String>();
		this.roundsWithoutMill = 0;
		this.playerOneFigureCount = 9;
		this.playerTwoFigureCount = 9;
		this.count = -1;
		
		this.mill = false;
		this.color = c;
		this.lastMill = false;
		this.alreadyPressed = false;
		
		this.isMoving = null;
		this.cursor = null;
		this.tempPanel = null;
		
		this.playerOne = new OnlineFigure[9];
		this.playerTwo = new OnlineFigure[9];
		
		for(int i = 0; i < 9; i++) {
			playerOne[i] = new OnlineFigure(true, this, 140, 50 + i * 70);
			playerTwo[i] = new OnlineFigure(false, this, 820, 50 + i * 70);
		}
		
	}
	
//Max
	
	private boolean withoutMill (boolean color) {
		
		boolean ret = false;
		
		MyJPanel [][] field = game.getWindow().getJPanel();
		List<MyJPanel> pieces = new ArrayList<MyJPanel>();
		
		for(int i = 0; i < 7; i++) {
			
			for(int j = 0; j < 7; j++) {
			
				if(field[i][j].getFigure() != null && field[i][j].getFigure().getColor() == color) {
					
					pieces.add(field[i][j]);
					
				}
				
			}
			
		}
		
		for(MyJPanel p : pieces) {
		
			if(p.getFigure() != null) {
				if (!checkMill(p)) {
					ret = true;
				}
			}
			
		}
		return ret;
	}
	
	private boolean checkForLegalMoves(boolean color) {
		
		for(int i = 0; i < 7; i++) {
			
			for(int j = 0; j < 7; j++) {
				
				if(game.getWindow().getJPanel()[i][j].getFigure() != null && game.getWindow().getJPanel()[i][j].getFigure().getColor() == color) {
					
					List<MyJPanel> list = game.getWindow().getJPanel()[i][j].getNeighbors();
					
					for(MyJPanel l : list) {
						
						if(l.getFigure() == null) {
							
							return true;
							
						}
						
					}
					
				}
				
			}
			
		}
		
		return false;
		
	}
	
	private boolean checkStalemate() {
		
		if(this.roundsWithoutMill == 20)
			return true;
			
		if(this.repetition == 3)
			return true;
		
		if(stalemateOnline)
			return true;
		
		if(endID == 2)
			return true;
		
		return false;
		
	}
	
	private boolean checkForRepetition(boolean color) {
		
		MyJPanel[][] f = game.getWindow().getJPanel();
		String s = "";
		
		for (int i = 0; i < f.length; i++) {
			
			for (int j = 0; j < f[i].length; j++) {
				
				if (f[i][j].isFigurePlaced()) {
					
					if (f[i][j].getFigure().getColor()) {
						s += "2";
					}
					else {
						s += "1";
					}
					
				}
				else {
					
					s += "0";
					
				}
				
			}
			
			if(repetitiveField.contains(s)) {
				return true;
				
			}
			
		}
		
		repetitiveField.add(s);
		return false;
	
	}
	
	private boolean checkMill(MyJPanel p) {
		
		List<MyJPanel> neighbors = p.getNeighbors();
		
		for (MyJPanel n : neighbors) {
			
			if(n.getFigure() != null && n.getFigure().getColor() == p.getFigure().getColor()) {
				
				MyJPanel third = game.getWindow().getLastInRow(p, n);
				if(third != null && third.getFigure() != null && third.getFigure().getColor() == p.getFigure().getColor()) {
					return true;
					
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public OnlineFigure [] getPlayerOne() {
		return this.playerOne;
	}
	
	public OnlineFigure [] getPlayerTwo() {
		return this.playerTwo;
	}
	
}
