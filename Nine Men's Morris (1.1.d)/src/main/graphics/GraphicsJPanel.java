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
import main.online.OnlineManager;
import main.online.packs.DataPackage;
import main.states.EndState;
import main.states.GameState;
import main.states.LobbyState;
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
				endID = 6;
	
	private Random random;
	private Thread receiver;
	
	private List<String> repetitiveField;
	
	private boolean color, 
					mill, 
					lastMill, 
					alreadyAdded, 
					alreadyPressed,
					activeUser,
					online,
					stalemateOnline,
					placingPhase,
					initMoving,
					iHaveMill;
	
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
		
		this.placingPhase = true;
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
		
		this.repaint();
		
		if(online) {
			
			if(!game.getMouseManager().isLeftPressed()) {
				alreadyPressed = false;
			}
			
			if(!receiver.isAlive()) {
				
				receiver = new Thread(() -> {
					
					getOnlineData();
					
				});
				
				receiver.start();
			}
			
			if(!alreadyPressed) {
				
				if(activeUser && game.getMouseManager().isLeftPressed() && (placingPhase || mill)) {
					
					int toX = game.getMouseManager().getPanelPressedX() + 5,
						toY = game.getMouseManager().getPanelPressedY() + 5;
					
					sendOnlineData(0,0,toX,toY);
					alreadyPressed = true;
					
				} else if(activeUser && game.getMouseManager().isLeftPressed() && game.getMouseManager().getPanelPressed().isFigurePlaced()) {
					
					isMoving = game.getMouseManager().getPanelPressed().getFigure();
					
				} else if(activeUser && game.getMouseManager().isLeftPressed() && isMoving != null) {
					int fromX = isMoving.getX(),
						fromY = isMoving.getY();
					int toX = game.getMouseManager().getPanelPressedX() + 5,
						toY = game.getMouseManager().getPanelPressedY() + 5;
					sendOnlineData(fromX,fromY,toX,toY);
					
					alreadyPressed = true;
				}
			}

	//Daniel
			
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
		
	}
	
	private void sendOnlineData(int fromX, int fromY, int toX, int toY) {
		int tX, tY, fX, fY;
		
		if (fromX < 270) 		fX = 0;
		else if (fromX < 350) 	fX = 1;
		else if(fromX < 460) 	fX = 2;
		else if(fromX < 500) 	fX = 3;
		else if(fromX < 600) 	fX = 4;
		else if(fromX < 700) 	fX = 5;
		else 					fX = 6;
		
		if (fromY < 100) 		fY = 0;
		else if (fromY < 200) 	fY = 1;
		else if(fromY < 300) 	fY = 2;
		else if(fromY < 400) 	fY = 3;
		else if(fromY < 470) 	fY = 4;
		else if(fromY < 550) 	fY = 5;
		else 					fY = 6;
		
		if (toX < 270) 			tX = 0;
		else if (toX < 350) 	tX = 1;
		else if(toX < 460) 		tX = 2;
		else if(toX < 500) 		tX = 3;
		else if(toX < 600) 		tX = 4;
		else if(toX < 700) 		tX = 5;
		else 					tX = 6;
		
		if (toY < 100) 			tY = 0;
		else if (toY < 200) 	tY = 1;
		else if(toY < 300) 		tY = 2;
		else if(toY < 400) 		tY = 3;
		else if(toY < 470) 		tY = 4;
		else if(toY < 550) 		tY = 5;
		else 					tY = 6;
		
		oMan.sendData(new DataPackage(0, fX, fY, tX, tY));
		activeUser = false;
		
	}
	
	private void getOnlineData() {
		
		DataPackage dp = null;
		
		try {
			
			dp = (DataPackage) oMan.receiveData();
			
		} catch(ClassCastException e) {
			
			State.setCurrentState(new LobbyState(game));
			
		}
		
		if(dp == null) {
			
			return;
			
		}
		
		//System.out.println("Count: " + count + "\tStatus: " + dp.getStatus());
		int s = dp.getStatus();
		
		if(s < 6) { // General game moves received from enemy

			if(s == 0) {
				placingPhase = true;
				count++;
			} else {
				placingPhase = false;
			}
			drawBoardFromString(dp);
			activeUser = true;
			if(count == 17) { 
				count++;
			}
			
		} else if(s == 6) { // Mill allowed / Enemy has made mill
			
			drawBoardFromString(dp);
			mill = false;
			activeUser = !iHaveMill;
			
		} else if(s == 20) { // move allowed, mill
			
			count++;
			if(count > 17)
				placingPhase = false;
			
			drawBoardFromString(dp);
			mill = true;
			activeUser = true;
			iHaveMill = true;
			
			if(count == 17) { 
				count++;
			}
			
		} else if(s == 21) { // nmy made mill
			
			count++;
			if(count > 17)
				placingPhase = false;
			drawBoardFromString(dp);
			//count--;
			iHaveMill = false;
			mill = true;
			activeUser = false;
			
		} else if(s == 50) { //move was not allowed by server
			
			activeUser = true;
			
		} else if(s == 51) { //move was allowed by server, now getting confirmed here

			if(placingPhase) {
				count++;
			}
			drawBoardFromString(dp);
			activeUser = false;
			
		} else if(s == 98) { //Start new Game
			
			reset(dp.getFromX()%2 == 1);
			
		} else if(s == 99) {
			
			endID = 5;
			oMan.sendData(new DataPackage(0, 0, 0, 0, 0));
			State.setCurrentState(new LobbyState(game));
			
		} else if(s == 23) { //YOU WIN
			
			if(placingPhase) {
				count++;
			}
			drawBoardFromString(dp);
			if(color) { //Black wins (YOU)
				endID = 4;
				endState = new EndState(game, true);
				State.setCurrentState(endState);
			}
			else { //White wins (YOU)
				endID = 3;
				endState = new EndState(game, false);
				State.setCurrentState(endState);
				
			}
			
			State.setCurrentState(new LobbyState(game));
			
		} else if(s == 24) { //NMY WIN

			if(placingPhase) {
				count++;
			}
			drawBoardFromString(dp);
			if(!color) { //Black wins (NMY)
				endID = 4;
				endState = new EndState(game, true);
				State.setCurrentState(endState);
			}
			else { //White wins (NMY)
				endID = 3;
				endState = new EndState(game, false);
				State.setCurrentState(endState);
				
			}

			State.setCurrentState(new LobbyState(game));
			
		} else if(s == 8) {

			drawBoardFromString(dp);
			stalemateOnline = true;
			endState = new EndState(game);
			State.setCurrentState(endState);
			

			State.setCurrentState(new LobbyState(game));
			
			
		}
		
		
	}
	
	private void drawBoardFromString(DataPackage dp) {
		
		int s = dp.getStatus();
		MyJPanel[][] f = game.getWindow().getJPanel();
		
		int 	fX = dp.getFromX(), fY = dp.getFromY(),
				tX = dp.getToX(), tY = dp.getToY();
		
		if(s == 0 || (!initMoving && s == 1)) { // Placing Phase Move by Enemy
			
			tempPanel = f[tX][tY];
			tempPanel.setFigure(playerTwo[count / 2]);
			playerTwo[count / 2].move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
			tempPanel = null;
			
			if(s != 0)
				initMoving = true;
			
		} else if(s < 6) { //Any other phase, writing after win, lose or stalemate
			
			tempPanel = f[tX][tY];
			tempPanel.setFigure(f[fX][fY].getFigure());
			f[fX][fY].getFigure().move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
			f[fX][fY].delFigure();
			tempPanel = null;
			
		} else if(s == 6) {
			
			removeFigureOnline(f[tX][tY]);
			
		} else if(s == 51 || s == 20) { //move allowed by server
			
			if(placingPhase) {
				
				tempPanel = f[tX][tY];
				tempPanel.setFigure(playerOne[count / 2]);
				playerOne[count / 2].move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
				tempPanel = null;
				
			} else {
				
				tempPanel = f[tX][tY];
				tempPanel.setFigure(f[fX][fY].getFigure());
				f[fX][fY].getFigure().move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
				f[fX][fY].delFigure();
				tempPanel = null;
				
			}
			
		} else if(s == 21) { // Enemy has mill
			
			if(placingPhase) {

				tempPanel = f[tX][tY];
				tempPanel.setFigure(playerTwo[count / 2]);
				playerTwo[count / 2].move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
				tempPanel = null;
				
			} else {
				
				tempPanel = f[tX][tY];
				tempPanel.setFigure(f[fX][fY].getFigure());
				f[fX][fY].getFigure().move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
				f[fX][fY].delFigure();
				tempPanel = null;
				
			}
			
		} else if(s == 23 || s == 24 || s == 8) {
			
			if(!mill) {
				
				if(placingPhase) {
					
					tempPanel = f[tX][tY];
					if(s == 23) {
						
						tempPanel.setFigure(playerOne[count / 2]);
						playerOne[count / 2].move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
						
					} else {
						
						tempPanel.setFigure(playerTwo[count / 2]);
						playerTwo[count / 2].move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
						
					}
					tempPanel = null;
					
				} else {
					
					tempPanel = f[tX][tY];
					tempPanel.setFigure(f[fX][fY].getFigure());
					f[fX][fY].getFigure().move(f[tX][tY].getX() + 5, f[tX][tY].getY() + 5);
					f[fX][fY].delFigure();
					tempPanel = null;
					
				}
				
			} if(mill) {
				
				removeFigureOnline(f[tX][tY]);
				
			}
			
		}
		
		this.repaint();
		
		
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
				
				if(endID == 4) {
					if(color) {
							
						game.getWindow().getLabel().setText("Black (YOU) wins! New Game?");
							
					} else {

						game.getWindow().getLabel().setText("Black (NMY) wins! New Game?");
							
					}
					
				} else if(endID == 3) {
					
					if(!color) {
						
						game.getWindow().getLabel().setText("White (YOU) wins! New Game?");
						
					} else {

						game.getWindow().getLabel().setText("White (NMY) wins! New Game?");
						
					}
					
				} else if(endID == 5) {
					
					game.getWindow().getLabel().setText("NMY left the game.");
					
				} else if(endID == 6) {
					
					game.getWindow().getLabel().setText("YOU left the game.");
					
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
		//count++;
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
		
		this.initMoving = false; if(!activeUser) initMoving = true;
		this.placingPhase = true;
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
		
		if(online) {
			
			receiver = new Thread(() -> {
				
				getOnlineData();
				
			});
			
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
