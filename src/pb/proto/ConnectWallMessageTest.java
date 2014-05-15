package pb.proto;

import static org.junit.Assert.*;

import org.junit.Test;

import pb.board.Edge;

/**
 *  test for: 
 *  - construction
 *  - serialization
 */

public class ConnectWallMessageTest {
    @Test
    public void testConstruction() {
        ConnectWallMessage message = new ConnectWallMessage("sampleNeighbor", Edge.LEFT);
        assertEquals("sampleNeighbor", message.getNeighborName());
        assertEquals(Edge.LEFT, message.getEdge());
    }
    
    @Test
    public void testSerialization() {
        ConnectWallMessage message = new ConnectWallMessage("sampleNeighbor", Edge.RIGHT);
        String messageLine = message.toLine();
        
        Message message2 = Message.fromLine(messageLine);
        assertTrue(message2 instanceof ConnectWallMessage);
        ConnectWallMessage ConnectWallMessage2 = (ConnectWallMessage)message2;
        assertEquals("sampleNeighbor", ConnectWallMessage2.getNeighborName());
        assertEquals(Edge.RIGHT, ConnectWallMessage2.getEdge());
    }
}