package pb.client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import pb.board.Board;
import pb.net.ControlMessage;
import pb.net.SocketPusher;
import pb.net.ControlMessage.Type;
import pb.parse.BoardBuilder;
import pb.proto.HelloMessage;
import pb.proto.Message;
import pb.render.RenderManager;
import pb.render.Viewport;

/**
 * The code that runs inside the client's board thread.
 *
 * The client has two threads (a {@link SocketPusher} and a
 * {@link ClientFetcher}) for talking to the server, and a board thread
 * (running an instance of this class) that handles simulation and rendering.
 * 
 * The client uses the Swing toolkit for its UI, and the Swing event dispatch
 * thread runs the code inside {@link ClientUiFrame}. The board thread sends
 * messages to the Swing thread. The messages are wrapped as {@link Runnable}
 * instances calling {@link ClientUi} methods, and are sent using
 * {@link SwingUtilities#invokeLater(Runnable)}. The Swing thread sends messages
 * to the board thread by pushing {@link UserCommandMessage} and
 * {@link UserInputMessage} instances to the board thread's request queue.
 * 
 * Right before running a simulation step, the board thread checks the request
 * queue and passes the messages to {@link Board#onMessage(Message)}. This
 * handles balls coming from the server, as well as user input.
 * 
 * During a {@link Board#simulate(double)} call, balls that must be sent to the
 * server are converted into {@link Message}s which are collected in a list.
 * After the simulation step, the messages are collected via
 * {@link Board#getAndClearOutgoingMessages()} and pushed onto the send queue.
 */
public class ClientController implements Runnable {
	/** Receives messages from the server and from the Swing thread. */
	private final BlockingQueue<Message> requestQueue;
	/** Used to send messages to the Swing thread. */
	private final ClientUi ui;
	/**
	 * Used to send messages to the server.
	 * 
	 * This is null if the client is not connected to a server.
	 */
	private BlockingQueue<Message> sendQueue;
	/**
	 * The socket that is connected to the server.
	 * 
	 * This is null if the client is not connected to a server.
	 */
	private Socket clientToServerSocket;
	/**
	 * The board simulated by this client.
	 * 
	 * This starts out as null, and changes every time a
	 * {@link UserCommandMessage.Type#LOAD} message is received from the Swing
	 * thread.
	 */
	private Board board;
	/**
	 * Implements the board simulation and rendering loop.
	 * 
	 * This starts out as null, and is set when a
	 * {@link UserCommandMessage.Type#SETUP_RENDERING} message is received from
	 * the Swing thread.
	 */
	private SimulationController simulationController;
	/** Used to implement the restart command. */
	private File lastLoadedBoardFile;
	
	/**
	 * Sets up a controller for a client.
	 */
	public ClientController(BlockingQueue<Message> requestQueue, ClientUi ui) {
		assert requestQueue != null;
		assert ui != null;
		
		this.ui = ui;
		this.requestQueue = requestQueue;
		this.sendQueue = null;
		this.clientToServerSocket = null;		
		this.board = null;
		this.simulationController = null;
		this.lastLoadedBoardFile = null;
	}
	
	@Override
	public void run() {        
        try {
	        while (true) {
	        	while (requestQueue.peek() != null) {
	        		// NOTE: NoSuchElementException should never be thrown,
	        		//       because we used peek() before, and we're this
	        		//		 queue's consumer
	        		Message message = requestQueue.remove();
	        		
	        		if (message instanceof UserCommandMessage &&
	        				((UserCommandMessage)message).getType() ==
	        				UserCommandMessage.Type.EXIT) {
	        			return;
	        		}
	        		
	        		handleMessage(message);
	        	}
	        	
	        	if (simulationController != null) {
	        		assert board != null;
	        		simulationController.simulateAndRender();	        		
		        	List<Message> messages =
		        			board.getAndClearOutgoingMessages();
		        	for (Message message : messages) {
		        		sendQueue.put(message);
		        	}
	        	} else {
	        		// NOTE: until we can simulate the board, we might as well
	        		//       sleep and wait for a message
	        		Message message = requestQueue.take();
	        		if (message instanceof UserCommandMessage &&
	        				((UserCommandMessage)message).getType() ==
	        				UserCommandMessage.Type.EXIT) {
	        			return;
	        		}	        		
	        		handleMessage(message);
	        	}
	        }
        }
        catch (InterruptedException e) {
        	e.printStackTrace();
        }
	}
	
	private void handleMessage(Message message) throws InterruptedException {
		if (message instanceof ControlMessage) {
			// Clean up when the server disconnects.
			if (((ControlMessage)message).getType() == Type.CLOSED)
				disconnect();
		} else if (message instanceof UserCommandMessage) {
			handleUserCommand((UserCommandMessage)message);
		}
		if (board != null)
			board.onMessage(message);
	}
	
	private void handleUserCommand(UserCommandMessage command)
			throws InterruptedException {
		if (command.getType() == UserCommandMessage.Type.PAUSE) {
			board.setPaused(true);
			return;
		}
		if (command.getType() == UserCommandMessage.Type.RESUME) {
			board.setPaused(false);
			return;
		}		
		if (command.getType() == UserCommandMessage.Type.RESTART) {
			if (lastLoadedBoardFile != null)
				loadBoard(lastLoadedBoardFile);
			return;
		}		
		if (command.getType() == UserCommandMessage.Type.DISCONNECT) {
			disconnect();
			return;
		}
		if (command.getType() == UserCommandMessage.Type.LOAD) {
			loadBoard(command.getBoardFile());
			return;
		}
		if (command.getType() == UserCommandMessage.Type.CONNECT) {
			disconnect();
			
			try { 
				connect(command.getHost(), command.getPort());
				if (isConnected())
					handshake();
			} catch (IOException e) {
				e.printStackTrace();
				disconnect();
			}
		}
		if (command.getType() == UserCommandMessage.Type.SETUP_RENDERING) {
			if (board != null) {
				RenderManager renderManager = new RenderManager(board,
						command.getRendererBufferStrategy());
				board.setRenderManager(renderManager);
				simulationController = new SimulationController(board);
			}
		}
	}
	
	private void loadBoard(File boardFile) throws InterruptedException {
		Board board;
		try {
			board = BoardBuilder.buildBoard(boardFile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// NOTE: the disconnect() call is here so that the user doesn't get
		//       disconnected from the server when trying to load a new
		//		 board, but the loading fails due to e.g. a parse error
		disconnect();
		setBoard(board);		
		this.lastLoadedBoardFile = boardFile;
	}
	
	private void setBoard(Board board) {
		assert board != null;
		// NOTE: the protocol doesn't support switching boards on the fly, so
		//       the user must disconnect and reconnect
		assert !isConnected();
		
		this.board = board;
		board.setHasServer(isConnected());
		
		final Viewport boardViewport = board.getViewport();
		final String boardName = board.getConstants().name();
		final ClientUi clientUi = this.ui;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				clientUi.setBoardViewport(boardViewport);
				clientUi.setBoardName(boardName);
			}
		});
	}
	
	/**
	 * Sets up a socket to the server and connects it to message queues.
	 * 
	 * @param host the hostname of the server
	 * @param port the network port where the server is listening
	 * @throws IOException if an error occurs while connecting to the server; if
	 *   this is thrown, the controller state is guaranteed to be completely
	 *   rolled back
	 */
	private void connect(String host, int port)
			throws IOException, InterruptedException {
		assert host != null;
		assert 1 <= port && port <= 65535;
		assert clientToServerSocket == null;
		assert sendQueue == null;
		
		sendQueue = new LinkedBlockingQueue<Message>();
		clientToServerSocket = new Socket();

		ClientFetcher fetcher;
		try {
			clientToServerSocket.connect(
					new InetSocketAddress(InetAddress.getByName(host), port));
			clientToServerSocket.setKeepAlive(true);
			clientToServerSocket.setTcpNoDelay(true);

			fetcher = new ClientFetcher(requestQueue,
					clientToServerSocket, sendQueue);
		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
			throw e;
		}
		Thread fetcherThread = new Thread(fetcher, "Client Fetcher");
		fetcherThread.start();

		SocketPusher pusher = new SocketPusher(sendQueue, clientToServerSocket);
		Thread pusherThread = new Thread(pusher, "Client Pusher");
		pusherThread.start();
				
		if (board == null)
			return;
		
		board.setHasServer(true);
		final ClientUi clientUi = this.ui;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				clientUi.setHasServer(true);
			}
		});
	}
	
	/**
	 * Returns true if the game client is connected to a server.
	 * @return true if the game client is connected to a server
	 */
	private boolean isConnected() {
		return sendQueue != null;
	}
	
	/**
	 * Says hi to the server.
	 * 
	 * @throws InterruptedException
	 */
	private void handshake() throws InterruptedException {
		assert sendQueue != null;
		assert requestQueue != null;
		
		sendQueue.put(new HelloMessage(board.getConstants().name()));
	}
	
	/**
	 * Disconnect from the server.
	 * @throws InterruptedException
	 */
	private void disconnect() throws InterruptedException {
		if (sendQueue == null)
			return;
		
		assert clientToServerSocket != null;
		try {
			clientToServerSocket.close();
		} catch (IOException e) {
			// We're closing down the connection, not much we can do here.
			e.printStackTrace();
		}
		clientToServerSocket = null;
		
		try {
			sendQueue.put(new ControlMessage(Type.DISCONNECT));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendQueue = null;
		
		board.setHasServer(false);
		final ClientUi clientUi = this.ui;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				clientUi.setHasServer(false);
			}
		});
	}
}
