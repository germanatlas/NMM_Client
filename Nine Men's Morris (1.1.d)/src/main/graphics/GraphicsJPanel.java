package main.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import main.game.Game;
import main.movment.Figure;
import main.online.DataPackage;
import main.online.OnlineManager;
import main.states.EndState;
import main.states.GameState;
import main.states.MenuState;
import main.states.State;

public class GraphicsJPanel extends JPanel {

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
				roundsWithoutMill,
				count, 
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
					online,
					stalemateOnline;
	
	private Figure [] playerOne, 
					  playerTwo;
	
	private Figure isMoving;
	
	private OnlineManager oMan;
	
	private MyJPanel cursor, 
					 tempPanel;
	
	public GraphicsJPanel(Game game, int WIDTH, int HEIGHT) {
		
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
		
		this.mill = false;
		this.color = random.nextBoolean();
		this.lastMill = false;
		this.alreadyPressed = false;
		this.alreadyAdded = false;
		
		this.isMoving = null;
		this.cursor = null;
		this.tempPanel = null;
		
		this.playerOne = new Figure[9];
		this.playerTwo = new Figure[9];
		
		for(int i = 0; i < 9; i++) {
			playerOne[i] = new Figure(color, this, 140, 50 + i * 70);
			playerTwo[i] = new Figure(!color, this, 820, 50 + i * 70);
		}
		
	}
	
	public void tick() {
	
	//Daniel
		
		if(online) {
			
			if(!game.getMouseManager().isLeftPressed()) {
				alreadyPressed = false;
			}
			
			if(!activeUser) {
				
				getOnlineData();
				
			}
			
			//System.out.println("IST ONLINE");
			
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
							
							mill = checkMill(tempPanel);
							if(mill) {
								roundsWithoutMill = 0;
								count--;
								
								lastMill = activeUser;
								//6 if black has mill, 5 if white has mill
								sendOnlineData(5, "", moveToX + "-" + moveToY);
							}
							else {
								sendOnlineData(1, "", moveToX + "-" + moveToY);
							}
							
							if(lastMill == !activeUser) {
								roundsWithoutMill++;
							}

							
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
					if(playerOneFigureCount > 3 && !game.getMouseManager().getPanelPressed().isFigurePlaced() && activeUser) {
						
						tempPanel = game.getMouseManager().getPanelPressed();
					
						
						if(tempPanel.isNeighborFrom(cursor)) {
							
							moveToX = game.getMouseManager().getPanelPressedX() + 5;
							moveToY = game.getMouseManager().getPanelPressedY() + 5;

							int tmpX = isMoving.getX(), tmpY = isMoving.getY();
							isMoving.move(moveToX, moveToY);
							tempPanel.setFigure(isMoving);
							
							cursor.delFigure();
							isMoving = null;
							
	//Max
							if(checkMill(tempPanel)) {
								
								roundsWithoutMill = 0;
								lastMill = color;
								mill = true;
								//6 if black has mill, 5 if white has mill
								sendOnlineData(5, tmpX + "-" + tmpY, moveToX + "-" + moveToY);
							}
							else if(lastMill == activeUser) {
								
								roundsWithoutMill++;
							}

							if(!mill) {
								sendOnlineData(1, tmpX + "-" + tmpY, moveToX + "-" + moveToY);
							}
							
							tempPanel = null;
							
							if(checkForRepetition(activeUser)) {
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
					
						if(activeUser && !game.getMouseManager().getPanelPressed().isFigurePlaced()) {
							
							tempPanel = game.getMouseManager().getPanelPressed();
						
							moveToX = game.getMouseManager().getPanelPressedX() + 5;
							moveToY = game.getMouseManager().getPanelPressedY() + 5;

							int tmpX = isMoving.getX(), tmpY = isMoving.getY();
							isMoving.move(moveToX, moveToY);
							tempPanel.setFigure(isMoving);
							
							cursor.delFigure();
							isMoving = null;

	//Max					
							if (checkMill(tempPanel)) {
								
								lastMill = activeUser;
								mill = true;
								//5 if mill
								sendOnlineData(5, tmpX + "-" + tmpY, moveToX + "-" + moveToY);
							}
							else if(lastMill == activeUser) {
								roundsWithoutMill++;
							}
							
							if(!mill) {
								sendOnlineData(1, tmpX + "-" + tmpY, moveToX + "-" + moveToY);
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
					sendOnlineData(2, "", "");
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
				
			}
			
			else if (!alreadyPressed && game.getMouseManager().isLeftPressed() && game.getMouseManager().getPanelPressed().isFigurePlaced() && activeUser)
			{
				alreadyPressed = true;
				if(withoutMill(!color)) {
					if(!checkMill(game.getMouseManager().getPanelPressed())) {
						
						while (!removeFigure(game.getMouseManager().getPanelPressed()));
						sendOnlineData(6, "", game.getMouseManager().getPanelPressedX() + "-" + game.getMouseManager().getPanelPressedY());
						mill = false;
						
					}
				}
				else if(!withoutMill(!color)){

					while (!removeFigure(game.getMouseManager().getPanelPressed()));
					sendOnlineData(6, "", game.getMouseManager().getPanelPressedX() + "-" + game.getMouseManager().getPanelPressedY());
					mill = false;
					
				}
				//check for end of game
				if(checkStalemate()) {
					sendOnlineData(2, "", "");
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
			}
			
	//Max		
			//checks for mill while placing phase
			/*if(count >= -1 && count <= 17) {
				if(tempPanel != null) {
					
					if(activeUser) {
						mill = checkMill(tempPanel);
						if(mill) {
							roundsWithoutMill = 0;
							count--;
							
							lastMill = activeUser;
							//6 if black has mill, 5 if white has mill
							sendOnlineData(5, "", moveToX + "-" + moveToY);
						}
						else if(lastMill == !activeUser) {
							roundsWithoutMill++;
						}
					}
					
				}
				tempPanel = null;
			}*/
			
			if(count == 17) { 
				count++;
			}

					if(count > 17 && !alreadyAdded) {
						
						String s = getStringFromBoard();
						repetitiveField.add(s);
						alreadyAdded = true;
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
			
		}
			 // ONLINE
		else //TODO SPLIT //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			 // OFFLINE
		{
		
			if(!game.getMouseManager().isLeftPressed()) {
				alreadyPressed = false;
			}
			
			if(!mill) {
				//PlacingPhase
			
				
				if(!alreadyPressed) {
					
					if(count < 17 && game.getMouseManager().isLeftPressed() && !game.getMouseManager().getPanelPressed().isFigurePlaced()) {
						tempPanel = game.getMouseManager().getPanelPressed();
						moveToX = game.getMouseManager().getPanelPressedX() + 5;
						moveToY = game.getMouseManager().getPanelPressedY() + 5;
						count++;
						//Black
						if (color) {
							tempPanel.setFigure(playerOne[count / 2]);
							if (!mill) {
								playerOne[count / 2].move(moveToX, moveToY);
							}
							
							color = !color;
						}
						//White
						else {
							tempPanel.setFigure(playerTwo[count / 2]);
							if (!mill) {
								playerTwo[count / 2].move(moveToX, moveToY);
							}
							
							color = !color;
						}
					
						alreadyPressed = true;
					}
					
				}
			
				//choose moving Piece
				if(!alreadyPressed) {
					
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
					if(playerOneFigureCount > 3 && color && !game.getMouseManager().getPanelPressed().isFigurePlaced()) {
						tempPanel = game.getMouseManager().getPanelPressed();
					
						if(tempPanel.isNeighborFrom(cursor)) {
							moveToX = game.getMouseManager().getPanelPressedX() + 5;
							moveToY = game.getMouseManager().getPanelPressedY() + 5;
							isMoving.move(moveToX, moveToY);
							tempPanel.setFigure(isMoving);
							
							color = false;
							
							cursor.delFigure();
							isMoving = null;
							
	//Max
							if(checkMill(tempPanel)) {
								color = !color;
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
				
	//Daniel			
					//white
					if(playerTwoFigureCount > 3 && !color && !game.getMouseManager().getPanelPressed().isFigurePlaced()) {
						tempPanel = game.getMouseManager().getPanelPressed();
					
						if (tempPanel.isNeighborFrom(cursor) || cursor.isNeighborFrom(tempPanel)) {
							moveToX = game.getMouseManager().getPanelPressedX() + 5;
							moveToY = game.getMouseManager().getPanelPressedY() + 5;
							isMoving.move(moveToX, moveToY);
							tempPanel.setFigure(isMoving);
							
							color = true;
							
							cursor.delFigure();
							isMoving = null;
							
	//Max
							if(checkMill(tempPanel)) {
								color = !color;
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
			
				//black
				if(playerOneFigureCount == 3 && !alreadyPressed && game.getMouseManager().isLeftPressed()) {
					if(isMoving != null) {
					
						if(color && !game.getMouseManager().getPanelPressed().isFigurePlaced()) {
							tempPanel = game.getMouseManager().getPanelPressed();
						
							moveToX = game.getMouseManager().getPanelPressedX() + 5;
							moveToY = game.getMouseManager().getPanelPressedY() + 5;
							isMoving.move(moveToX, moveToY);
							tempPanel.setFigure(isMoving);
							color = false;
							
							cursor.delFigure();
							isMoving = null;
	
	//Max						
							if (checkMill(tempPanel)) {
								color = !color;
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
				//white
				if(playerTwoFigureCount == 3 && !alreadyPressed && game.getMouseManager().isLeftPressed()) {
					if(isMoving != null) {
						
						if(!color && !game.getMouseManager().getPanelPressed().isFigurePlaced()) {
							tempPanel = game.getMouseManager().getPanelPressed();
							
							moveToX = game.getMouseManager().getPanelPressedX() + 5;
							moveToY = game.getMouseManager().getPanelPressedY() + 5;
							isMoving.move(moveToX, moveToY);	
							tempPanel.setFigure(isMoving);
							color = true;
							
							cursor.delFigure();
							isMoving = null;
	
	//Max						
							if (checkMill(tempPanel)) {
								color = !color;
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
				
	//Max
				//checkForEnd
				if(color) {
					
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
				}
				
			}
			
	//Daniel
			//delete Stone
			
			else if (!alreadyPressed && game.getMouseManager().isLeftPressed() && game.getMouseManager().getPanelPressed().isFigurePlaced())
			{
				alreadyPressed = true;
				if(withoutMill(!color)) {
					if(!checkMill(game.getMouseManager().getPanelPressed())) {
						
						while (!removeFigure(game.getMouseManager().getPanelPressed())) {
						}
						mill = false;
						color = !color;
					}
				}
				else if(!withoutMill(!color)){
	
					while (!removeFigure(game.getMouseManager().getPanelPressed())) {
					}
					mill = false;
					color = !color;
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
							color = !color;
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
							color = !color;
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
	
			//first field that can be repeated
					if(count > 17 && !alreadyAdded) {
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
						repetitiveField.add(s);
						alreadyAdded = true;
					}
	
	//Daniel
			//messages		
	
			if(count < 17) {
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
		
		}
		
		this.repaint();
		
	}
	
	private void sendOnlineData(int status, String fromCoords, String toCoords) {
		
		String s = fromCoords + "_" + toCoords;
		System.out.println(s);
		System.out.println("PC: " + playerOneFigureCount + " " + playerTwoFigureCount);
		oMan.sendData(new DataPackage(status, s));
		if(status != 5) activeUser = false;
		
	}
	
	private void getOnlineData() {
		
		DataPackage dp = oMan.receiveData();
		
		if(dp == null) {
			
			return;
			
		}

		System.out.println(dp.getStatus() + "  " + dp.getMove());
		
		if(dp.getStatus() == 1) {
			
			if(count < 17) {
				//Placing Phase
				count++;
				
			}
			
			//TODO
			//Spielstatus anpassen
			drawBoardFromString(dp);
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
			
		} else if(dp.getStatus() == 5) {
			
			if(count < 17) {
				//Placing Phase
				count++;
				
			}
			
			drawBoardFromString(dp);
			count--;
			mill = true;
			activeUser = false;
			
		} else if(dp.getStatus() == 6) {

			drawBoardFromString(dp);
			roundsWithoutMill = 0;
			lastMill = !color;
			mill = false;
			activeUser = true;
			
		} else if(dp.getStatus() == 98) {
			//TODO
			
		} else if(dp.getStatus() == 99) {
			activeUser = Integer.parseInt(dp.getMove())/10 == 1;
			boolean thisColor = Integer.parseInt(dp.getMove())%10 == 1;
			reset(thisColor);
			
		}
		
		System.out.println("PC: " + playerOneFigureCount + " " + playerTwoFigureCount);
		
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
	
	private void drawBoardFromString(DataPackage dp) {
		
		String move = dp.getMove();
		int status = dp.getStatus();
		
		MyJPanel[][] f = game.getWindow().getJPanel();
		String[] data = move.split("_");
		
		String[] from = data[0].split("-");
		String[] to = data[1].split("-");
		
		
		
		if((status == 1 || status == 5)) {
			
			int x = Integer.parseInt(to[0]);
			int y = Integer.parseInt(to[1]);
			int valxt = 0, valyt = 0;
			
			if (x < 270) 		valxt = 0;
			else if (x < 350) 	valxt = 1;
			else if(x < 460) 	valxt = 2;
			else if(x < 500) 	valxt = 3;
			else if(x < 600) 	valxt = 4;
			else if(x < 700) 	valxt = 5;
			else 				valxt = 6;
			
			if (y < 100) 		valyt = 0;
			else if (y < 200) 	valyt = 1;
			else if(y < 300) 	valyt = 2;
			else if(y < 400) 	valyt = 3;
			else if(y < 470) 	valyt = 4;
			else if(y < 550) 	valyt = 5;
			else 				valyt = 6;
			
			if(count > 17) {
				int xf = Integer.parseInt(from[0]);
				int yf = Integer.parseInt(from[1]);
				int valxf = 0, valyf = 0;
				
				if (xf < 270) 		valxf = 0;
				else if (xf < 350) 	valxf = 1;
				else if(xf < 460) 	valxf = 2;
				else if(xf < 500) 	valxf = 3;
				else if(xf < 600) 	valxf = 4;
				else if(xf < 700) 	valxf = 5;
				else 				valxf = 6;
				
				if (yf < 100) 		valyf = 0;
				else if (yf < 200) 	valyf = 1;
				else if(yf < 300) 	valyf = 2;
				else if(yf < 400) 	valyf = 3;
				else if(yf < 470) 	valyf = 4;
				else if(yf < 550) 	valyf = 5;
				else 				valyf = 6;
				System.out.println("Vals: " + valxf + " " + valyf);
				tempPanel = f[valxt][valyt];
				tempPanel.setFigure(f[valxf][valyf].getFigure());
				f[valxf][valyf].getFigure().move(x, y);
				f[valxf][valyf].delFigure();
				tempPanel = null;
				return;
				
			}
			
			System.out.println(valxt + " " + valyt);
			tempPanel = f[valxt][valyt];
			tempPanel.setFigure(playerTwo[count / 2]);
			playerTwo[count / 2].move(x, y);
			tempPanel = null;
			
		} else if(status == 6) {
			int x = Integer.parseInt(to[0]);
			int y = Integer.parseInt(to[1]);
			int valx = 0, valy = 0;
			
			if (x < 270) 		valx = 0;
			else if (x < 350) 	valx = 1;
			else if(x < 460) 	valx = 2;
			else if(x < 500) 	valx = 3;
			else if(x < 600) 	valx = 4;
			else if(x < 700) 	valx = 5;
			else 				valx = 6;
			
			if (y < 100) 		valy = 0;
			else if (y < 200) 	valy = 1;
			else if(y < 300) 	valy = 2;
			else if(y < 400) 	valy = 3;
			else if(y < 470) 	valy = 4;
			else if(y < 550) 	valy = 5;
			else 				valy = 6;
			System.out.println(valx + " x - y " + valy + "\t" + f[valx][valy].getFieldX() + " " + f[valx][valy].getFieldY());
			removeFigureOnline(f[valx][valy]);
		
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
			if(online) {
				
				if(tempState.getColor() != null && tempState.getColor() || endID == 4) {
					if(color) {
							
						game.getWindow().getLabel().setText("Black (YOU) wins!");
							
					} else {

						game.getWindow().getLabel().setText("Black (NMY) wins!");
							
					}
					
					if(endID != 4) {
						
						sendOnlineData(4, "", "");
						
					}
					
				} else if(endID == 3) {
					
					if(!color) {
						
						game.getWindow().getLabel().setText("White (YOU) wins!");
						
					} else {

						game.getWindow().getLabel().setText("White (NMY) wins!");
						
					}
					
				}
				
			} else {
				
				if(tempState.getColor() != null && tempState.getColor()) {
					game.getWindow().getLabel().setText("Black wins!");
				}
				else {
					game.getWindow().getLabel().setText("White wins!");
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
		
		if(m.getFigure().getColor() && !online) {
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
	
	public boolean removeFigureOnline(MyJPanel m) {
		
		if (m.getFigure() == null) {
			
			return false;
			
		}
		
		if(m.getFigure().getColor() == color) {
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
	
	public void reset(boolean color) {
		this.repetition = 0;
		this.repetitiveField = new ArrayList<String>();
		this.roundsWithoutMill = 0;
		this.playerOneFigureCount = 9;
		this.playerTwoFigureCount = 9;
		this.count = -1;
		
		this.mill = false;
		this.color = color;
		this.lastMill = false;
		this.alreadyPressed = false;
		
		this.isMoving = null;
		this.cursor = null;
		this.tempPanel = null;
		
		this.playerOne = new Figure[9];
		this.playerTwo = new Figure[9];
		
		for(int i = 0; i < 9; i++) {
			playerOne[i] = new Figure(this.color, this, 140, 50 + i * 70);
			playerTwo[i] = new Figure(!this.color, this, 820, 50 + i * 70);
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
	
	public Figure [] getPlayerOne() {
		return this.playerOne;
	}
	
	public Figure [] getPlayerTwo() {
		return this.playerTwo;
	}
	
	public void setOnline(boolean isOnline) {
		this.online = isOnline;
	}
	
	public void setActiveUser(boolean b) {
		this.activeUser = b;
	}
	
	public void setOnlineManager(OnlineManager onlineManager) {
		this.oMan = onlineManager;
	}
	
}
