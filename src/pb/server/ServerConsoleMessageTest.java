package pb.server;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *  test for: 
 *  - construction
 */

public class ServerConsoleMessageTest {
    @Test
    public void testConstruction() {
        ServerConsoleMessage message = new ServerConsoleMessage(
        		"h board1 board2");
        assertEquals("h board1 board2", message.getLine());
    }
}