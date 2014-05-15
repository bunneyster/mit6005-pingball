package pb.server;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import pb.net.ControlMessage;
import pb.net.ControlMessage.Type;
import pb.proto.PortalBallMessage;
import pb.proto.WallBallMessage;
import pb.proto.HelloMessage;
import pb.proto.Message;
import pb.proto.WelcomeMessage;
import pb.server.BoardLinks.TargetedMessage;

/** The code that runs on the board links thread. */ 
public class ServerController implements Runnable {
	/** The data structure that implements all the hard stuff. */
	private final BoardLinks boardLinks;
	/** Queues up commands received by client threads. */
	private final BlockingQueue<Request> requestQueue;
	/** Send queues for the connected clients with named boards. */
	private final HashMap<String, BlockingQueue<Message>> clientQueues;
	
	/**
	 * Sets up the thread that manages the game's state.
	 * 
	 * @param board an initialized board; the GameThread takes ownership of the
	 * 	   board after this call, and it should not be referenced anywhere else
	 * @param inDebugMode if true, users don't get disconnected when they get
	 *     the BOOM message
	 */
	public ServerController(BoardLinks boardLinks) {
		assert boardLinks != null;
		this.boardLinks = boardLinks;
		this.requestQueue = new ArrayBlockingQueue<Request>(2);
		this.clientQueues = new HashMap<String, BlockingQueue<Message>>();
	}
	
	/**
	 * The queue that receives client commands.
	 * 
	 * The queue should be passed to client threads so they can enqueue
	 * commands. Only the GameThread should get things out of the queue.
	 * 
	 * @return queue that receives client commands
	 */
	public BlockingQueue<Request> getRequestQueue() {
		return requestQueue;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Request request = requestQueue.take();
				if (request == Request.EXIT)
					return;
				handleRequest(request); 
			}
		} catch (InterruptedException e) {
			// Done serving requests?
			e.printStackTrace();
		}
	}

    /**
     * Acts on a client request.
     * 
     * @param request the request wrapping the client message
     */
    private void handleRequest(Request request) throws InterruptedException {
    	Message message = request.getMessage();
    	if (message instanceof HelloMessage) {
    		HelloMessage hello = (HelloMessage)message;
    		BlockingQueue<Message> clientQueue = request.getClientQueue();
    		if (hello.getProtocolVersion() == Message.PROTOCOL_VERSION) {
    			String boardName = hello.getBoardName();
    			if (!clientQueues.containsKey(boardName)) {
    				clientQueues.put(boardName, clientQueue);
    				Message welcome = new WelcomeMessage();
    				clientQueue.put(welcome);

    				List<BoardLinks.TargetedMessage> reactions = 
    						boardLinks.connected(boardName);
    				for(BoardLinks.TargetedMessage reaction : reactions)
    					dispatch(reaction);
    				return;
    			}
    		}
    		// NOTE: the specs say that the result of two clients joining with
    		//       the same board name is undefined; we choose to disconnect
    		//       the second client
    		Message disconnect = new ControlMessage(Type.DISCONNECT);
    		clientQueue.put(disconnect);
    		return;
    	}
    	if (message instanceof ControlMessage) {
    		ControlMessage controlMessage = (ControlMessage)message;
    		if (controlMessage.getType() == Type.CLOSED) {
    			// The client disconnected.
    			String boardName = request.getBoardName();
    			if (boardName == null ||
    					!clientQueues.containsKey(boardName))  {
    				// The client disconnected before sending HelloMessage.
    				// We don't need to do anything to update the server state.
    				return;
    			}
 				clientQueues.remove(boardName);
    			
    			List<BoardLinks.TargetedMessage> reactions =
    					boardLinks.disconnected(boardName); 
    			for(BoardLinks.TargetedMessage reaction : reactions)
    				dispatch(reaction);    			
    			return;
    		}
    	}
    	if (message instanceof WallBallMessage) {
    		WallBallMessage ballMessage = (WallBallMessage)message;
    		BoardLinks.TargetedMessage response = boardLinks.wallTeleport(
    				request.getBoardName(), ballMessage);
    		dispatch(response);
    		return;
    	}
    	if (message instanceof PortalBallMessage) {
    		PortalBallMessage ballMessage = (PortalBallMessage)message;
    		BoardLinks.TargetedMessage response = boardLinks.portalTeleport(
    				ballMessage);
    		dispatch(response);
    		return;
    	}
    	if (message instanceof ServerConsoleMessage) {
    		ServerConsoleMessage consoleMessage = (ServerConsoleMessage)message;
    		String[] tokens = consoleMessage.getLine().trim().split("\\s+");
    		if (tokens.length != 3)
    			return;
    		
			List<BoardLinks.TargetedMessage> reactions; 
    		if (tokens[0].equals("h"))
    			reactions = boardLinks.horizontalJoin(tokens[1], tokens[2]);
    		else if (tokens[0].equals("v"))
    			reactions = boardLinks.verticalJoin(tokens[1], tokens[2]);
    		else
    			return;
			for(BoardLinks.TargetedMessage reaction : reactions)
				dispatch(reaction);
			return;
    	}
    	
    	assert false;
        throw new UnsupportedOperationException();
    }
    
    /**
     * Sends a message to a client.
     * 
     * @param targetedMessage the (client board name, message) pair
     * @throws InterruptedException
     */
    private void dispatch(TargetedMessage targetedMessage)
    		throws InterruptedException {
    	String boardName = targetedMessage.getBoardName();
    	Message message = targetedMessage.getMessage();
		BlockingQueue<Message> clientQueue = clientQueues.get(boardName);
		assert clientQueue != null;
		clientQueue.put(message);
    }
}