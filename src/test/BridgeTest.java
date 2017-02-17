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
	public void compare(){
		
		int value = 0;
		int[][] array = new int[3][3];
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				array[i][j]=value;
				value++;
			}
		}
		
		value = 0;
		int[][] array2 = new int[3][3];
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				array2[i][j]=value;
				value++;
			}
		}
		
		BoardNode n1 = new BoardNode(array,0,0);
		BoardNode n2 = new BoardNode(array2,0,0);
		
		System.out.println(n1.compare(n2));
		System.out.println(n1.toString());
		System.out.println(n2.toString());
		
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
