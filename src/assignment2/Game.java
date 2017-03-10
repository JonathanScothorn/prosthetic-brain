package assignment2;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {
	
	private Board board;
	private int[] playerScores;

	public Game(){
		
		board = new Board();
		
		System.out.println("Initial Board State: \n");
		System.out.println(board.toString());
		
	}
	
	public void runPvP(Scanner scanner, int players){
		
		boolean gameInProgress = true;
		playerScores = new int[players]; // defaults to zero filled
		
		// game starts with player 1
		int currentPlayer = 1;
		
		while(gameInProgress){
			
			// execute a player's turn
			gameInProgress = executeHumanTurn(scanner, currentPlayer);
			
			System.out.println(board.toString());
			
			// increment the player number, and roll over if the maximum number of players has been exceeded
			currentPlayer++;
			if(currentPlayer > players){
				currentPlayer = 1;
			}
			
		}
		
		//System.out.println(Arrays.toString(generateMoves(1).toArray()));
	}
	
	public void runPvC(Scanner scanner, int players, int ply){
		
		boolean gameInProgress = true;
		playerScores = new int[players];
		int currentPlayer = 1;
		
		while(gameInProgress){
			
			// player 1 will be the human player, player 2 the computer player
			if(currentPlayer == 1){
				gameInProgress = executeHumanTurn(scanner, currentPlayer);
			} else if(currentPlayer == 2){
				gameInProgress = executeComputerTurn(currentPlayer, ply);
			}
			
		}
		
	}
	
	private boolean executeComputerTurn(int currentPlayerToken, int ply){
		
		// if no legal moves are available, end the game.
		ArrayList<Move> legalMoves = generateMoves(currentPlayerToken);
		if(legalMoves.size() == 0){
			return false;
		}
		
		// now start minimaxing the moves, up to the maximum ply depth
		
		
		return true;
		
	}
	
	private boolean executeHumanTurn(Scanner scanner, int currentPlayerToken){
		
		// if no legal moves are available, end the game.
		ArrayList<Move> legalMoves = generateMoves(currentPlayerToken);
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
				System.out.println(board.toString());
			}
		}
		
		playerScores[currentPlayerToken-1] += performMove(board, move);
		
		return true;
	}
	
	private ArrayList<Move> generateMoves(int currentPlayerToken){
		
		ArrayList<Move> moves = new ArrayList<Move>();
		
		//iterate over the board and select each piece which belongs to the current player
		for(int y=0; y<board.BOARD_HEIGHT; y++){
			
			for(int x=0; x<board.BOARD_WIDTH; x++){
				
				if(board.getSquare(x, y).getController() == currentPlayerToken){
					
					// any number of tokens from the stack may be moved (1 to the total number of tokens)
					for(int selectedTokens = 1; selectedTokens <= board.getSquare(x, y).getSize(); selectedTokens++){
						
						// legal moves are a number of spaces either horizontally or vertically up to the number of pieces removed
						for(int squaresMoved = 1; squaresMoved <= selectedTokens; squaresMoved++){
							
							if(board.isValidCoord(x-squaresMoved, y)){
								Move m = new Move(x, y, x-squaresMoved, y, selectedTokens);
								moves.add(m);
								//System.out.println("Adding move: "+m.toString());
							}
							if(board.isValidCoord(x, y-squaresMoved)){
								Move m = new Move(x, y, x, y-squaresMoved, selectedTokens);
								moves.add(m);
								//System.out.println("Adding move: "+m.toString());
							}
							if(board.isValidCoord(x+1, y)){
								Move m = new Move(x, y, x+squaresMoved, y, selectedTokens);
								moves.add(m);
								//System.out.println("Adding move: "+m.toString());
							}
							if(board.isValidCoord(x, y+squaresMoved)){
								Move m = new Move(x, y, x, y+squaresMoved, selectedTokens);
								moves.add(m);
								//System.out.println("Adding move: "+m.toString());
							}
						}
						
					}
					
				}
			}
		}
		
		return moves;
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
		ArrayList<Integer> piecesToMove = boardState.getSquare(move.getInitialX(), move.getInitialY()).removePieces(move.getTokensToMove());
		// add pieces to the second square and capture
		return boardState.getSquare(move.getFinalX(), move.getFinalY()).addPieces(piecesToMove);
	}
	
	
	public Board getBoard(){
		return board;
	}
	
	public static void main(String[] args){
		
		Scanner scanner = new Scanner(System.in);
		String userInput;
		
		System.out.println("Please enter '1' for Player vs. Player, '2' for Player vs. Computer, or '3' for Computer vs. Computer.");
		userInput = scanner.next();
		
		Game game = new Game();
		
		if(userInput.equals("1")){
			game.runPvP(scanner, 2);
			
		} else if(userInput.equals("2")){
			
			System.out.println("Please enter a depth of search value (as an integer) for the computer opponent.");
			if(scanner.hasNextInt()){
				game.runPvC(scanner, 2, scanner.nextInt());
			} else {
				System.out.println("Invalid depth of search value.  Please restart the program.");
			}
			
		} else if(userInput.equals("3")){
			
		} else {
			System.out.println("Unrecognized input.  Please restart the program.");
		}
	}
}
