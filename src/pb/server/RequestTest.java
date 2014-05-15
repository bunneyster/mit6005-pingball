package pb.server;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import pb.board.Edge;
import pb.proto.WallBallMessage;
import pb.proto.HelloMessage;
import pb.proto.Message;
import physics.Circle;
import physics.Vect;

public class RequestTest {
	@Test
	public void testHelloConstruction() {
		HelloMessage message = new HelloMessage("board1");
		BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(1);
		Request helloRequest = new Request(message, queue);
			
		assertEquals("board1", helloRequest.getBoardName());
		assertEquals(message, helloRequest.getMessage());
		assertEquals(queue, helloRequest.getClientQueue());
	}

	@Test
	public void testNonHelloConstruction() {
		WallBallMessage message = new WallBallMessage(Edge.LEFT, "ballName",
				new Circle(new Vect(10, 20), 30), new Vect(0.2, 0.3));
		Request helloRequest = new Request("board1", message);
			
		assertEquals("board1", helloRequest.getBoardName());
		assertEquals(message, helloRequest.getMessage());
		assertEquals(null, helloRequest.getClientQueue());
	}
}
