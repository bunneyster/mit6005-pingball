package pb.proto;

import static org.junit.Assert.*;

import org.junit.Test;

import physics.Circle;
import physics.Vect;

/**
 *  test for: 
 *  - construction
 *  - serialization
 */

public class PortalBallMessageTest {
    @Test
    public void testConstruction() {
        PortalBallMessage message = new PortalBallMessage("fromBoardName",
        		"fromPortalName", "toBoardName", "toPortalName", "ballName",
        		new Circle(new Vect(2.0, 3.0), 4.0), new Vect(5.0, 6.0));
        assertEquals("fromBoardName", message.getFromBoard());
        assertEquals("fromPortalName", message.getFromPortal());
        assertEquals("toBoardName", message.getToBoard());
        assertEquals("toPortalName", message.getToPortal());
        assertEquals("ballName", message.getBallName());
        assertEquals(2.0, message.getShape().getCenter().x(), 0.00001);
        assertEquals(3.0, message.getShape().getCenter().y(), 0.00001);
        assertEquals(4.0, message.getShape().getRadius(), 0.00001);
        assertEquals(5.0, message.getVelocity().x(), 0.00001);
        assertEquals(6.0, message.getVelocity().y(), 0.00001);
    }
    
    @Test
    public void testSerialization() {
    	PortalBallMessage message = new PortalBallMessage("fromBoardName2",
    			"fromPortalName2", "toBoardName2", "toPortalName2",
    			"anotherBallName", new Circle(new Vect(-2.5, 3.2), 12.34),
    			new Vect(-13.1, 14.2));
        String messageLine = message.toLine();
        
        Message message2 = Message.fromLine(messageLine);
        assertTrue(message2 instanceof PortalBallMessage);
        PortalBallMessage ballMessage2 = (PortalBallMessage)message2;
        assertEquals("fromBoardName2", ballMessage2.getFromBoard());
        assertEquals("fromPortalName2", ballMessage2.getFromPortal());
        assertEquals("toBoardName2", ballMessage2.getToBoard());
        assertEquals("toPortalName2", ballMessage2.getToPortal());
        assertEquals("anotherBallName", ballMessage2.getBallName());
        assertEquals(-2.5, ballMessage2.getShape().getCenter().x(), 0.00001);
        assertEquals(3.2, ballMessage2.getShape().getCenter().y(), 0.00001);
        assertEquals(12.34, ballMessage2.getShape().getRadius(), 0.00001);
        assertEquals(-13.1, ballMessage2.getVelocity().x(), 0.00001);
        assertEquals(14.2, ballMessage2.getVelocity().y(), 0.00001);
    }
}