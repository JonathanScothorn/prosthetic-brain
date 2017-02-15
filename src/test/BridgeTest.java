package test;

import org.junit.Test;
import static org.junit.Assert.*;
import assignment1.Bridge;
import assignment1.BridgeNode;

public class BridgeTest {
	
	private Bridge bridge;
	private BridgeNode node;
	
	@org.junit.Before
	public void setUp() throws Exception {
		
		bridge = new Bridge();
		node = new BridgeNode(new boolean[]{true, true, true, true, true, true}, true, 0);
	}
	
	@Test
	public void compareBridgeNode(){
		
		//assertEquals(true, bridge.compareBridgeNode(new BridgeNode(new boolean[]{true, true}, true), new BridgeNode(new boolean[]{true, true}, true)));
		
	}
	
	
	@Test
	public void setPerson() throws Exception{
		BridgeNode node2 = node.copy();
		node2.setPerson(2);
		System.out.println(node.toString());
		System.out.println(node2.toString());
	}
	
	@org.junit.After
	public void tearDown() throws Exception {
		
	}

}
