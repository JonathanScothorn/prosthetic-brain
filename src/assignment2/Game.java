package assignment2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
	
	private Board gameBoard;
	private int[] playerScores;
	private Move[] secondLastMove;
	private Move[] lastMove;
	public static final int MAX_HEURISTIC_CHOICE = 3;
	private PrintWriter logger;
	private Move initiatorMove; // move that is used to start the generateMove method

	public Game(){
		
		try {
			logger = new PrintWriter("log.txt", "UTF-8");
		} catch (IOException e){
			System.out.println("Unable to create print writer.");
		}
		
		gameBoard = new Board();
		initiatorMove = new Move(0,0,1,0,1,-1);
		
		System.out.println("Initial Board State: \n");
		System.out.println(gameBoard.toString());
		
	}
	
	public void initializeSavedMoves(int players){
		secondLastMove = new Move[players];
		lastMove = new Move[players];
		for(int i=0; i<secondLastMove.length; i++){
			secondLastMove[i] = new Move(0,0,0,0,0,0);
			lastMove[i] = new Move(0,0,0,0,0,0);
		}
		
	}
	
	public void runPvP(Scanner scanner, int players){
		
		// initialize both move tracking arrays to moves that are impossible.  These will be overwritten in the first 2 turns of the game.
		initializeSavedMoves(players);	
		
		boolean gameInProgress = true;
		playerScores = new int[players]; // defaults to zero filled
		
		// game starts with player 1
		int currentPlayer = 1;
		
		while(gameInProgress){
			
			// execute a player's turn
			gameInProgress = executeHumanTurn(scanner, currentPlayer);
			
			System.out.println(gameBoard.toString());
			
			if(!gameInProgress){
				System.out.println("Game complete.  Player "+currentPlayer+" is victorious!");
			}
			
			// increment the player number, and roll over if the maximum number of players has been exceeded
			currentPlayer++;
			if(currentPlayer > players){
				currentPlayer = 1;
			}
			
		}
		
		//System.out.println(Arrays.toString(generateMoves(1).toArray()));
	}
	
	public void runPvC(Scanner scanner, int players, int ply, int heuristicChoice){
		
		// initialize both move tracking arrays to moves that are impossible.  These will be overwritten in the first 2 turns of the game.
		initializeSavedMoves(players);
		
		boolean gameInProgress = true;
		playerScores = new int[players];
		int currentPlayer = 1;
		
		while(gameInProgress){
			
			// player 1 will be the human player, player 2 the computer player
			if(currentPlayer == 1){
				gameInProgress = executeHumanTurn(scanner, currentPlayer);
			} else if(currentPlayer == 2){
				gameInProgress = executeComputerTurn(currentPlayer, ply, heuristicChoice);
			}
			
			System.out.println(gameBoard.toString());
			
			if(!gameInProgress){
				System.out.println("Game complete.  Player "+currentPlayer+" is victorious!");
			}
			
			// increment the player number, and roll over if the maximum number of players has been exceeded
			currentPlayer++;
			if(currentPlayer > players){
				currentPlayer = 1;
			}
			
		}
		
	}
	
	public void runCvC(int players, int ply1, int ply2, int c1HeuristicChoice, int c2HeuristicChoice){
		
		// initialize both move tracking arrays to moves that are impossible.  These will be overwritten in the first 2 turns of the game.
		initializeSavedMoves(players);
		
		boolean gameInProgress = true;
		playerScores = new int[players];
		int currentPlayer = 1;
		
		while(gameInProgress){
			
			if(currentPlayer == 1){
				gameInProgress = executeComputerTurn(currentPlayer, ply1, c1HeuristicChoice);
			} else {
				gameInProgress = executeComputerTurn(currentPlayer, ply2, c2HeuristicChoice);
			}
						
			System.out.println(gameBoard.toString());
			
			if(!gameInProgress){
				System.out.println("Game complete.  Player "+currentPlayer+" is victorious!");
			}
			
			// increment the player number, and roll over if the maximum number of players has been exceeded
			currentPlayer++;
			if(currentPlayer > players){
				currentPlayer = 1;
			}
			
		}
		
	}
	
	private boolean executeComputerTurn(int currentPlayerToken, int ply, int heuristicChoice){
		
		logger.println("Player "+currentPlayerToken+"'s turn begins.");
		
		if(generateMove(currentPlayerToken, gameBoard, initiatorMove) == null){
			return false;
		}
		
		// now start minimaxing the moves, up to the maximum ply depth
		//Node initial = new Node(gameBoard, legalMoves, true, currentPlayerToken);
		Node initial = new Node(gameBoard, true, currentPlayerToken);
		evaluateNode(initial, ply, heuristicChoice);
		secondLastMove[currentPlayerToken - 1] = lastMove[currentPlayerToken - 1];
		lastMove[currentPlayerToken - 1] = initial.getBestNextMove();
		logger.println("Executing move "+initial.getBestNextMove().toString());
		playerScores[currentPlayerToken - 1] += performMove(gameBoard, initial.getBestNextMove());
		
		System.out.println("Computer player "+currentPlayerToken+" performs move "+initial.getBestNextMove().toString()+" with heuristic value "+initial.getAlphaOrBeta());
		
		return true;
		
	}
	
	// recursive function that will build and evaluate the solution tree
	private int evaluateNode(Node node, int remainingPly, int heuristicChoice){
		
		if(node.getParent() != null){
			//System.out.println("Evaluating node generated by "+node.toString()+" at Ply level "+remainingPly);
			//System.out.println(node.getBoardState());
		}
		
		Move nextMove = generateMove(node.getPlayerNumber(), node.getBoardState(), initiatorMove);
		//System.out.println(nextMove.toString());
		
		// if the node is terminal, the game has been either won or lost
		if(nextMove == null){
			if(node.getIsMaxNode()){
				// opponent wins! return the minimum heuristic value
				return Integer.MIN_VALUE;
			} else {
				// we win! return the maximum heuristic value
				return Integer.MAX_VALUE;
			}
		}
		
		// if the maximum tree depth has been reached return the node's heuristic value
		if(remainingPly == 0){
			int heuristicValue = calculateHeuristic(node.getBoardState(), node.getPlayerNumber(), heuristicChoice);
			//System.out.println("Maximum depth reached.  Heuristic value: "+heuristicValue);
			return heuristicValue; 
		}
		
		// now investigate the next node down to see if it is terminal
		int nextPlayer = getNextPlayerToken(node.getPlayerNumber());
		
		// iterate over all available moves
		while(nextMove != null){
			
			// for each move
			Board b = new Board(node.getBoardState());// clone the board
			//System.out.println(node.getBoardState().toString());
			performMove(b, nextMove);// apply the move to it
			Node nextNode = new Node(b, !node.getIsMaxNode(), node, nextMove, nextPlayer);// create the resulting node
			nextMove = generateMove(node.getPlayerNumber(), node.getBoardState(), nextMove);// update nextMove to reflect the move that will be applied next
			
			// evaluate the node recursively
			int childHeuristic = evaluateNode(nextNode, remainingPly-1, heuristicChoice);
			//System.out.println("Heuristic: "+childHeuristic+" at ply level "+remainingPly+"\n");
			
			if(node.getIsMaxNode()){
				
				
				// if a value greater than the current alpha has been found, choose it and set the best move found to the move that produced it
				if(childHeuristic > node.getAlphaOrBeta()){
					node.setAlphaOrBeta(childHeuristic);
					node.setBestNextMove(nextNode.getCreatedFrom());
				}
				// pruning: if the node is a max node, its parent is a min node and it will only ever be chosen if it is lower than the min node's beta value
				if(node.getParent() != null){
					if(node.getAlphaOrBeta() > node.getParent().getAlphaOrBeta()){
						//System.out.println("Beta pruned node with value "+node.getAlphaOrBeta()+" by parent with value "+node.getParent().getAlphaOrBeta()+" at ply level "+remainingPly);
						break;
					}
				}
			} else {
				
				// if a value less than the current beta has been found, choose it and set the best move found to the move that produced it
				if(childHeuristic < node.getAlphaOrBeta()){
					node.setAlphaOrBeta(childHeuristic);
					node.setBestNextMove(nextNode.getCreatedFrom());
				}
				// pruning: if the node is a min node, its parent is a max node and it will only ever be chosen if it is higher than the max node's alpha value
				// so stop evaluating the node and break
				if(node.getParent() != null){
					if(node.getAlphaOrBeta() < node.getParent().getAlphaOrBeta()){
						//System.out.println("Alpha pruned node with value "+node.getAlphaOrBeta()+" by parent with value "+node.getParent().getAlphaOrBeta());
						break;
					}
				}
				
			}
			
		}
		return node.getAlphaOrBeta();
		//return evaluateNode(next);
	}
	
	
	private boolean executeHumanTurn(Scanner scanner, int currentPlayerToken){
		
		// if no legal moves are available, end the game.
		ArrayList<Move> legalMoves = generateMoves(currentPlayerToken, gameBoard);
		if(legalMoves.size() == 0){
			return false;
		}
		
		boolean validMove = false;
		Move move = legalMoves.get(0);// initialization here to the first legal move, but it MUST be overwritten successfully before the following while loop can exit
		
		while(!validMove){
			
			System.out.println("Please enter your next move, as 5 digits.\n"
					+ "Digit 1: x coordinate of the initial space\n"
					+ "Digit 2: y coordinate of the initial space\n"
					+ "Digit 3: x coordinate of the final space\n"
					+ "Digit 4: y coordinate of the final space\n"
					+ "Digit 5: number of tokens to move.");
			
			boolean moveInputCompleted = false;
			int[] inputs = new int[5];
			int index = 0;
			
			while(!moveInputCompleted){
				if(scanner.hasNextInt()){
					inputs[index] = scanner.nextInt();
					// once inputs are full, compare with the list of legal moves...this might be more time intensive than running the move through a validation algorithm; could be optimized if runtime is an issue
					if(index==4){
						moveInputCompleted = true;
					} else {
						index++;
					}
				} else {
					// clear the bad input and discard it
					scanner.next();
					System.out.println("Invalid entry.  Please enter an integer.");
				}
			}
			move = new Move(inputs[0], inputs[1], inputs[2], inputs[3], inputs[4]);
			for(Move m:legalMoves){
				// if the move is in the legal moves list, proceed and process it
				if (m.equals(move)){
					validMove = true;
				}
			}
			if(!validMove){
				System.out.println("Invalid move.  Please try again.\n");
				System.out.println(gameBoard.toString());
			}
		}
		
		secondLastMove[currentPlayerToken - 1] = lastMove[currentPlayerToken - 1];
		lastMove[currentPlayerToken - 1] = move;
		playerScores[currentPlayerToken-1] += performMove(gameBoard, move);
		
		return true;
	}
	
	public ArrayList<Move> generateMoves(int currentPlayerToken, Board boardState){
		
		ArrayList<Move> moves = new ArrayList<Move>();
		//System.out.println(boardState.toString());
		
		//iterate over the board and select each piece which belongs to the current player
		for(int y=0; y<boardState.BOARD_HEIGHT; y++){
			
			for(int x=0; x<boardState.BOARD_WIDTH; x++){
				
				if(boardState.getSquare(x, y).getController() == currentPlayerToken){
					
					// any number of tokens from the stack may be moved (1 to the total number of tokens)
					for(int selectedTokens = 1; selectedTokens <= boardState.getSquare(x, y).getSize(); selectedTokens++){
						
						// legal moves are a number of spaces either horizontally or vertically up to the number of pieces removed
						for(int squaresMoved = 1; squaresMoved <= selectedTokens; squaresMoved++){
							
							if(boardState.isValidCoord(x-squaresMoved, y)){
								Move m = new Move(x, y, x-squaresMoved, y, selectedTokens, 4*selectedTokens);
								if(!m.equals(secondLastMove[currentPlayerToken - 1])){// to avoid deadlock, it is illegal to repeat the second last move
									moves.add(m);
								}
								
								//System.out.println("Adding move: "+m.toString());
							}
							if(boardState.isValidCoord(x, y-squaresMoved)){
								Move m = new Move(x, y, x, y-squaresMoved, selectedTokens, 4*selectedTokens+1);
								if(!m.equals(secondLastMove)){
									moves.add(m);
								}
								//System.out.println("Adding move: "+m.toString());
							}
							if(boardState.isValidCoord(x+squaresMoved, y)){
								Move m = new Move(x, y, x+squaresMoved, y, selectedTokens, 4*selectedTokens+2);
								if(!m.equals(secondLastMove)){
									moves.add(m);
								}
								//System.out.println("Adding move: "+m.toString());
							}
							if(boardState.isValidCoord(x, y+squaresMoved)){
								Move m = new Move(x, y, x, y+squaresMoved, selectedTokens, 4*selectedTokens+3);
								if(!m.equals(secondLastMove)){
									moves.add(m);
								}
								//System.out.println("Adding move: "+m.toString());
							}
						}
						
					}
					
				}
			}
		}
		
		return moves;
	}
	
	// method that will generate moves one at a time, based on the last move that was generated
	// necessary to reduce overhead of generating all possible moves when only the first few are searched before a cut is made.
	public Move generateMove(int currentPlayerToken, Board boardState, Move previous){
		
		int stepsToSkip = previous.getGenerationIndex() % 4;
		int initialDistanceMoved;
		int initialX = previous.getInitialX();
		
		if(previous.getInitialX() != previous.getFinalX()){
			initialDistanceMoved = Math.abs(previous.getInitialX() - previous.getFinalX());
		} else {
			initialDistanceMoved = Math.abs(previous.getInitialY() - previous.getFinalY());
		}
		
		//iterate over the board and select each piece which belongs to the current player
		for(int y=previous.getInitialY(); y<boardState.BOARD_HEIGHT; y++){
			
			for(int x=initialX; x<boardState.BOARD_WIDTH; x++){
				
				if(boardState.getSquare(x, y).getController() == currentPlayerToken){
					
					// any number of tokens from the stack may be moved (1 to the total number of tokens)
					for(int selectedTokens = previous.getTokensToMove(); selectedTokens <= boardState.getSquare(x, y).getSize(); selectedTokens++){
						
						// legal moves are a number of spaces either horizontally or vertically up to the number of pieces removed
						for(int squaresMoved = initialDistanceMoved; squaresMoved <= selectedTokens; squaresMoved++){
							
							// statement 1 generates stepsToSkip == 0
							if(boardState.isValidCoord(x-squaresMoved, y) && stepsToSkip < 0){ // always skip this step the first time through, as stepsToSkip should always be 0, 1, 2, or 3
								Move m = new Move(x, y, x-squaresMoved, y, selectedTokens, 0);
								if(!m.equals(secondLastMove[currentPlayerToken - 1])){// to avoid deadlock, it is illegal to repeat the second last move
									return m;
								}
								
								//System.out.println("Adding move: "+m.toString());
							} 
							// statement 2, generates stepsToSkip == 1
							if(boardState.isValidCoord(x, y-squaresMoved) && stepsToSkip < 1){ // perform this step the first time through only if the previous move was generated by the first statement 
								Move m = new Move(x, y, x, y-squaresMoved, selectedTokens, 1);
								if(!m.equals(secondLastMove)){
									return m;
								}
								//System.out.println("Adding move: "+m.toString());
							} 
							// statement 3, generates stepsToSkip == 2
							if(boardState.isValidCoord(x+squaresMoved, y) && stepsToSkip < 2){
								Move m = new Move(x, y, x+squaresMoved, y, selectedTokens, 2);
								if(!m.equals(secondLastMove)){
									return m;
								}
								//System.out.println("Adding move: "+m.toString());
							} 
							// statement 4, generates stepsToSkip == 3
							if(boardState.isValidCoord(x, y+squaresMoved) && stepsToSkip < 3){
								Move m = new Move(x, y, x, y+squaresMoved, selectedTokens, 3);
								if(!m.equals(secondLastMove)){
									return m;
								}
								//System.out.println("Adding move: "+m.toString());
							}
							stepsToSkip = -1; // once the first iteration has been completed, we want to perform all statements in the following loop iterations
						}
						
					}
					
				}
			}
			initialX = 0;// need to reset the loop counter here so that it does not start at previous.initialX once the y coordinate is incremented.
		}
		return null; // if all of the possibilities have been and no valid move has been found, return null.  Need to check for null in the calling function.
	}
	
	private ArrayList<Board> performAllMoves(Board boardState, ArrayList<Move> moves){
		
		ArrayList<Board> resultingStates = new ArrayList<Board>();
		
		for(Move move: moves){
			// copy the board, perform the move, and then add the board to the array
			Board b = new Board(boardState);
			performMove(b, move);
			resultingStates.add(b);
		}
		
		return resultingStates;
	}
	
	public int performMove(Board boardState, Move move){
		// remove pieces from the first square
		//System.out.println("Evaluating move "+move.toString());
		ArrayList<Integer> piecesToMove = boardState.getSquare(move.getInitialX(), move.getInitialY()).removePieces(move.getTokensToMove());
		// add pieces to the second square and capture
		return boardState.getSquare(move.getFinalX(), move.getFinalY()).addPieces(piecesToMove);
	}
	
	private int calculateHeuristic(Board board, int currentPlayerToken, int heuristicToUse){
		
		if(heuristicToUse == 1){
			return calculateHeuristic1(board, currentPlayerToken);
		} else if(heuristicToUse == 2){
			return calculateHeuristic2(board, currentPlayerToken);
		} else if(heuristicToUse == 3){
			return calculateHeuristic3(board, currentPlayerToken);
		} else {
			System.out.println("Error: invalid heuristic number");
			return -1;
		}
		
	}
	
	// heuristic 1: count the total number of "controlled" pieces, give 5 bonus points per enemy token removed
	public int calculateHeuristic1(Board board, int currentPlayerToken){
		
		int heuristicValue = 0;
		int currentPlayerTokensFound = 0;
		int opposingPlayerTokensFound = 0;
		int nextPlayerToken = getNextPlayerToken(currentPlayerToken);
		
		for(int y=0; y<board.BOARD_HEIGHT; y++){
			for(int x=0; x<board.BOARD_WIDTH; x++){
				if(board.isValidCoord(x, y)){
					if(board.getSquare(x, y).getController() == currentPlayerToken){
						heuristicValue += board.getSquare(x, y).getSize();
					}
					currentPlayerTokensFound += board.getSquare(x, y).getPiecesByPlayer(currentPlayerToken);
					opposingPlayerTokensFound += board.getSquare(x, y).getPiecesByPlayer(nextPlayerToken);
				}
			}
				
		}
		heuristicValue += ((18 - opposingPlayerTokensFound) * 5);
		return heuristicValue;
	}
	
	// heuristic 2: like heuristic 1, but with extra emphasis on large stacks, which are very strong and mobile
	public int calculateHeuristic2(Board board, int currentPlayerToken){
		
		int heuristicValue = 0;
		int opposingPlayerTokensFound = 0;
		int nextPlayerToken = getNextPlayerToken(currentPlayerToken);
		
		for(int y=0; y<board.BOARD_HEIGHT; y++){
			for(int x=0; x<board.BOARD_WIDTH; x++){
				if(board.isValidCoord(x, y)){
					if(board.getSquare(x, y).getController() == currentPlayerToken){
						heuristicValue += board.getSquare(x, y).getSize() * board.getSquare(x, y).getSize();
					}
					opposingPlayerTokensFound += board.getSquare(x, y).getPiecesByPlayer(nextPlayerToken);
				}
			}
				
		}
		heuristicValue += ((18 - opposingPlayerTokensFound) * 5);
		return heuristicValue;
		
	}
	
	// like heuristic 1, but takes into account the "active" enemy pieces remaining
	public int calculateHeuristic3(Board board, int currentPlayerToken){
		
		int heuristicValue = 0;
		
		for(int y=0; y<board.BOARD_HEIGHT; y++){
			for(int x=0; x<board.BOARD_WIDTH; x++){
				if(board.isValidCoord(x, y)){
					if(board.getSquare(x, y).getController() == currentPlayerToken){
						heuristicValue += board.getSquare(x, y).getSize();
					} else {
						heuristicValue -= board.getSquare(x, y).getSize();
					}
				}
			}
				
		}
		return heuristicValue;
	}
	
	public int getNextPlayerToken(int playerToken){
		int nextPlayer = playerToken + 1;
		if(nextPlayer > playerScores.length){ // playerScores.length represents the number of players in the game
			nextPlayer = 1;
		}
		return nextPlayer;
	}
	
	
	public Board getBoard(){
		return gameBoard;
	}
	
	public PrintWriter getLogger(){
		return logger;
	}
	
	public int scanInt(Scanner scanner, String prompt){
		
		boolean done = false;
		
		System.out.println(prompt+" Or enter 'q' to quit.");
		
		while(!done){
			if(scanner.hasNextInt()){
				return scanner.nextInt();				
			} else {
				String userInput = scanner.next();
				if(userInput.equals("q")){
					done = true;
					//System.out.println("Quitting.");
				} else {
					System.out.println("Invalid entry.  Please enter a valid integer.");
				}
			}
		}
		return -1;
		
	}
	
	public static void main(String[] args){
		
		Scanner scanner = new Scanner(System.in);
		
		Game game = new Game();
		
		int gameType = game.scanInt(scanner, "Please enter '1' for Player vs. Player, '2' for Player vs. Computer, or '3' for Computer vs. Computer.");
		
		if(gameType == 1){
			
			game.runPvP(scanner, 2);
			
		} else if(gameType == 2){
			
			int searchDepth = game.scanInt(scanner, "Please enter a depth of search value (as an integer) for the computer opponent. \n"
					+ "Must be one or greater.  Recommended no larger than 4.");
			
			if(searchDepth < 10 && searchDepth > 0){ // hard cap of 10
				
				int heuristicChoice = game.scanInt(scanner, "Please enter which heuristic the computer should use (integer).\n"
						+ "Must be between 1 and "+game.MAX_HEURISTIC_CHOICE+" inclusive.");
				
				if(heuristicChoice >=1 && heuristicChoice <= game.MAX_HEURISTIC_CHOICE){
					game.runPvC(scanner, 2, searchDepth, heuristicChoice);
				} else {
					System.out.println("No valid heuristic choice detected. Quitting.");
				}
				
				
			} else {
				System.out.println("No valid search depth detected.  Quitting.");
			}
			
		} else if(gameType == 3){
			
			int searchDepth1 = game.scanInt(scanner, "Please enter a depth of search value (as an integer) for computer 1. \n"
					+ "Must be one or greater.  Recommended no larger than 4.");
			
			if(searchDepth1 < 10 && searchDepth1 > 0){ // hard cap of 10
				
				int searchDepth2 = game.scanInt(scanner, "Please enter a depth of search value (as an integer) for computer 2. \n"
						+ "Must be one or greater.  Recommended no larger than 4.");
				
				if(searchDepth2 < 10 && searchDepth2 > 0){ // hard cap of 10
					
					int heuristicChoice1 = game.scanInt(scanner, "Please enter which heuristic computer 1 should use (integer).\n"
							+ "Must be between 1 and "+game.MAX_HEURISTIC_CHOICE+" inclusive.");
					
					if(heuristicChoice1 >=1 && heuristicChoice1 <= game.MAX_HEURISTIC_CHOICE){
						
						int heuristicChoice2 = game.scanInt(scanner, "Please enter which heuristic computer 2 should use (integer).\n"
								+ "Must be between 1 and "+game.MAX_HEURISTIC_CHOICE+" inclusive.");
						
						if(heuristicChoice2 >=1 && heuristicChoice2 <= game.MAX_HEURISTIC_CHOICE){
							
							game.runCvC(2, searchDepth1, searchDepth2, heuristicChoice1, heuristicChoice2);
							
						} else {
							System.out.println("No valid heuristic choice detected. Quitting.");
						}
					} else {
						System.out.println("No valid heuristic choice detected. Quitting.");
					}
					
					
				} else {
					System.out.println("No valid search depth detected.  Quitting.");
				}
				
				
			} else {
				System.out.println("No valid search depth detected.  Quitting.");
			}
			
		} else {
			System.out.println("No valid game selection detected.  Quitting.");
		}
		
		game.getLogger().close();
		scanner.close();
		
		
	}
}
