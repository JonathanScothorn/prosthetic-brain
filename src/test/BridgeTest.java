package test;

import org.junit.Test;
import static org.junit.Assert.*;

import assignment1.BoardNode;
import assignment1.Bridge;
import assignment1.BridgeNode;

public class BridgeTest {
	
	private Bridge bridge;
	private BridgeNode node;
	private BoardNode boardNode;
	
	@org.junit.Before
	public void setUp() throws Exception {
		
		bridge = new Bridge();
		int[][] board = new int[3][4];
		boardNode = new BoardNode(board, 0,0);
		//node = new BridgeNode(new boolean[]{true, true, true, true, true, true}, true, 0);
	}
	
	@Test
	public void printing(){
		System.out.println(boardNode.toString());
	}
	
	@Test
	public void setTile(){
		boardNode.setTile(1, 2, 5);
		System.out.println(boardNode.toString());
	}
	
	@Test
	public void compareBridgeNode(){
		
		//assertEquals(true, bridge.compareBridgeNode(new BridgeNode(new boolean[]{true, true}, true), new BridgeNode(new boolean[]{true, true}, true)));
		
	}
	
	
	@Test
	public void setPerson() throws Exception{
		BridgeNode node2 = node.createChild();
		node2.setPerson(2);
		System.out.println(node.toString());
		System.out.println(node2.toString());
	}
	
	@org.junit.After
	public void tearDown() throws Exception {
		
	}

}
