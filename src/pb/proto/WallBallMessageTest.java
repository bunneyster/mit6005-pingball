package pb.proto;

import static org.junit.Assert.*;

import org.junit.Test;

import pb.board.Edge;
import physics.Circle;
import physics.Vect;

/**
 *  test for: 
 *  - construction
 *  - serialization
 */

public class WallBallMessageTest {
    @Test
    public void testConstruction() {
        WallBallMessage message = new WallBallMessage(Edge.LEFT, "ballName",
        		new Circle(new Vect(2.0, 3.0), 4.0), new Vect(5.0, 6.0));
        assertEquals(Edge.LEFT, message.getEdge());
        assertEquals("ballName", message.getBallName());
        assertEquals(2.0, message.getShape().getCenter().x(), 0.00001);
        assertEquals(3.0, message.getShape().getCenter().y(), 0.00001);
        assertEquals(4.0, message.getShape().getRadius(), 0.00001);
        assertEquals(5.0, message.getVelocity().x(), 0.00001);
        assertEquals(6.0, message.getVelocity().y(), 0.00001);
    }
    
    @Test
    public void testSerialization() {
        WallBallMessage message = new WallBallMessage(Edge.RIGHT,
        		"anotherBallName", new Circle(new Vect(-2.5, 3.2), 12.34),
        		new Vect(-13.1, 14.2));
        String messageLine = message.toLine();
        
        Message message2 = Message.fromLine(messageLine);
        assertTrue(message2 instanceof WallBallMessage);
        WallBallMessage ballMessage2 = (WallBallMessage)message2;
        assertEquals(Edge.RIGHT, ballMessage2.getEdge());
        assertEquals("anotherBallName", ballMessage2.getBallName());
        assertEquals(-2.5, ballMessage2.getShape().getCenter().x(), 0.00001);
        assertEquals(3.2, ballMessage2.getShape().getCenter().y(), 0.00001);
        assertEquals(12.34, ballMessage2.getShape().getRadius(), 0.00001);
        assertEquals(-13.1, ballMessage2.getVelocity().x(), 0.00001);
        assertEquals(14.2, ballMessage2.getVelocity().y(), 0.00001);
    }
}