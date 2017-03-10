package test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

import assignment2.Board;
import assignment2.Square;
import assignment2.Move;
import assignment2.Game;

public class Assignment2Test {

	private Game game;
	private Board board;
	private Square square;
	private Move move;
	
	@org.junit.Before
	public void setUp() throws Exception{
		
		game = new Game();
		board = game.getBoard();
		square = new Square(1);
		move = new Move(1,2,5,6,3);
	}
	
	@Test
	public void squareToString() throws Exception{
		
		square.addPiece(3);
		assertEquals("13", square.toString());
		
	}
		
	@Test
	public void squareAddPiecesOverFive() throws Exception{
		
		square.addPiece(4);
		square.addPiece(2);
		
		ArrayList<Integer> pieces = new ArrayList<Integer>();
		pieces.add(3);
		pieces.add(4);
		pieces.add(6);
		pieces.add(5);
		
		int captured = square.addPieces(pieces);
		
		// expect that the pieces should be the following (bottom to top): 23465, and 2 pieces were captured
		assertEquals("23465", square.toString());
		assertEquals(2, captured);
	}
	
	@Test
	public void squareAddPiecesNotOverFive() throws Exception{
		
		ArrayList<Integer> pieces = new ArrayList<Integer>();
		pieces.add(3);
		pieces.add(4);
		pieces.add(6);
		pieces.add(5);
		square.addPieces(pieces);
		assertEquals("13465", square.toString());

	}
	
	@Test
	public void squareRemovePieces() throws Exception{
		
		ArrayList<Integer> pieces = new ArrayList<Integer>();
		pieces.add(3);
		pieces.add(4);
		pieces.add(6);
		pieces.add(5);
		square.addPieces(pieces);
		square.removePieces(3);
		assertEquals("13", square.toString());
	}
	
	
	@Test
	public void squareGetController() throws Exception{
		square.addPiece(2);
		assertEquals(2, square.getController());
	}
	
	@Test
	public void squareEquals() throws Exception{
		
		square.addPiece(3);
		square.addPiece(1);
		square.addPiece(2);
		
		Square s = new Square(1);
		
		s.addPiece(3);
		s.addPiece(1);
		s.addPiece(2);
		
		assertEquals(true, square.equals(s));
		
	}
	
	@Test
	public void squareCopy() throws Exception{
		
		square.addPiece(3);
		square.addPiece(1);
		square.addPiece(2);
		
		Square s = new Square(square);
		
		assertEquals(true, square.equals(s));
		
	}
	
	@Test
	public void squareClone() throws Exception{
		
		square.addPiece(3);
		square.addPiece(1);
		square.addPiece(2);
		
		Square s = new Square(square);
		s.addPiece(3);
		
		//System.out.println(square.toString());
		//System.out.println(s.toString());
		
		assertEquals(false, square.equals(s));
		
	}
	
	@Test
	public void moveEquals() throws Exception{
		
		Move m = new Move(1,2,5,6,3);
		assertEquals(true, move.equals(m));
		
	}
	
	@Test
	public void boardCopy() throws Exception{
		
		Board b = new Board(board);
		assertEquals(board.toString(),b.toString());
		
	}
	
	// non-exhaustive
	@Test
	public void boardIsValidCoord() throws Exception{
		
		assertEquals(false, board.isValidCoord(1, 0));
		assertEquals(false, board.isValidCoord(7, 0));
		assertEquals(false, board.isValidCoord(7, 7));
		assertEquals(true, board.isValidCoord(5, 5));
		assertEquals(false, board.isValidCoord(-1, 0));
		assertEquals(false, board.isValidCoord(1, 22));
	}
	
	@Test
	public void boardInitialization() throws Exception{
		
		//System.out.println(board.toString());
		
		// expect 18 player token 1s in the string, and the 2 column/row delimiters
		int onesFound = board.toString().length() - board.toString().replaceAll("1",  "").length();
		assertEquals(18+2, onesFound);
		
		// expect 18 player token 2s in the string, and the 2 column/row delimiters
		int twosFound = board.toString().length() - board.toString().replaceAll("2",  "").length();
		assertEquals(18+2, twosFound);
		
	}
	
	// ensures that the performMove method does alter the board which is passed as a parameter
	@Test
	public void gamePerformMoveWithTwoTokens() throws Exception{
		
		String initial = board.getSquare(1, 1).toString();
		String finalString = board.getSquare(1, 2).toString();
		game.performMove(board, new Move(1,1,1,2,1));
		assertEquals(finalString+initial, board.getSquare(1, 2).toString());
	}
	
	@Test
	public void gamePerformMoveToBlankSquare() throws Exception{
		
		String initial = board.getSquare(1, 2).toString();
		game.performMove(board, new Move(1,2,0,2,1));
		assertEquals(initial, board.getSquare(0, 2).toString());
	}
	
	@Test
	public void gameGenerateMovoes() throws Exception{
		// TODO
	}
	
	
	@org.junit.After
	public void tearDown() throws Exception {
		
	}
	
}
