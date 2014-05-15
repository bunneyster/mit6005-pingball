package pb.proto;

import static org.junit.Assert.*;

import org.junit.Test;

import pb.board.Edge;

/**
 *  test for: 
 *  - construction
 *  - serialization
 */

public class DisconnectWallMessageTest {
    @Test
    public void testConstruction() {
        DisconnectWallMessage message = new DisconnectWallMessage(Edge.LEFT);
        assertEquals(Edge.LEFT, message.getEdge());
    }
    
    @Test
    public void testSerialization() {
        DisconnectWallMessage message = new DisconnectWallMessage(Edge.RIGHT);
        String messageLine = message.toLine();
        
        Message message2 = Message.fromLine(messageLine);
        assertTrue(message2 instanceof DisconnectWallMessage);
        DisconnectWallMessage DisconnectWallMessage2 = (DisconnectWallMessage)message2;
        assertEquals(Edge.RIGHT, DisconnectWallMessage2.getEdge());
    }
}