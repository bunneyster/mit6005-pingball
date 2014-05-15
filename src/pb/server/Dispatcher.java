package pb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pb.net.SocketPusher;
import pb.proto.Message;


/**
 * Manages the server's threads.
 *
 * The server has one thread that accepts connections on the listening socket,
 * and spawns two new threads when a new client connection is established. For
 * each client, there is a {@link ServerFetcher} thread that reads lines from
 * the socket and converts them into {@link Request} instances that end up in
 * a global request queue. For each client, there is also a {@link SocketPusher}
 * thread that takes messages from the client's send queue and writes them as
 * lines to the client's socket.
 * 
 * The server has a thread that receives input from the console, and turns
 * each command into a {@link ServerConsoleMessage} that gets pushed in the
 * global request queue. Last, the server also has a thread running a
 * {@link ServerController} instance, which is covered in the next paragraph.
 * 
 * The server's data model is an instance of {@link BoardLinks}, which has the
 * logic for responding to incoming messages. The model acts on commands from
 * the console, clients coming and going, and balls traveling between boards.
 * The model must be contained to its own thread, running a
 * {@link ServerController}. The server controller takes {@link Request}
 * instances from the global request queue, translates them into
 * {@link BoardLinks} method calls, and pushes response messages into the
 * appropriate client send queues.
 * 
 * Tests for edge cases in the specification should use the model directly, and
 * should go in {@link BoardLinksTest}. {@link ServerControllerTest} should
 * verify that each message type is turned into the proper {@link BoardLinks}
 * method call. {@link DispatcherTest} should verify that the dispatcher code
 * handles multiple clients talking to the server in series or parallel.
 */
public class Dispatcher {
	/** Interface to the code running on the server thread. */
	private final ListenController listenController;
	/** The thread listening for incoming connections. */
	private final Thread listenThread;
	/** The thread listening for input from the server console. */
	private final Thread consoleThread;
	
	// Rep invariant:
	//   listenController, listenThread and consoleThread are not null
	// Thread safety argument:
	//   all member variables are immutable references (final)
	//   listenController is used in a thread-safe manner, according to its spec
	//   listenThread and consoleThread are only accessed in serve(), which is
	//		 documented as not being thread-safe

	/**
	 * Sets up a dispatcher.
	 * 
	 * @param requestQueue queue that accepts requests for the board links
	 *   thread
	 * @param port the port that the server will listen to
	 * @param consoleStream the console's input stream
	 * @throws IOException
	 */
	public Dispatcher(BlockingQueue<Request> requestQueue, int port,
			InputStream consoleStream) throws IOException {
		assert requestQueue != null;

		listenController = new ListenController(requestQueue, port);
		listenThread = new Thread(listenController, "Connection Listener");
		ConsoleController consoleController = new ConsoleController(
				requestQueue, consoleStream);
		consoleThread = new Thread(consoleController, "Console Reader");
	}
	
	/**
	 * Sets up the server to listen for connections.
	 * 
	 * This method should be called at most once. It returns when the server is
	 * no longer listening for connections, either because
	 * {@link #stopServing()} was called, or because 
	 */
	public void serve() {
		listenThread.start();
		consoleThread.start();
		try {
			listenThread.join();
			consoleThread.join();
		} catch(InterruptedException e) {
			// Someone else wants us to exit.
		}
	}
	
	/**
	 * Shuts down the server.
	 * 
	 * This method can be called on any thread.
	 */
	public void stopServing() {
		listenController.stopListening();
	}
	
	/** The code that runs on the thread listening for connections. */
	private static class ListenController implements Runnable {
		/** Accepts requests for the board links thread. */
		private final BlockingQueue<Request> requestQueue;
		/** The socket that accepts incoming client connections. */
	    private final ServerSocket acceptSocket;
	    /** Lock that must be acquired to mutate {@link #listening}. */
	    private final Object listeningChangeLock;
	    /** False when the thread should stop */
	    private boolean listening;
	    
	    // Rep invariant:
	    //   requestQueue, socket and listeningChangeLock are not null
	    // Abstraction function:
	    //	 requestQueue is the queue that receives board links requests
	    //   socket is the socket listening for connections on the server port
	    // Thread-safety argument 1:
	    //   (outside tests, #stopListening is never called)
	    //	 assuming #stopListening is never called, all the fields are
	    //   initialized in the constructor and contained to the server
	    //   thread afterwards
	    // Thread-safety argument 2:
	    //   (in tests, when #stopListening can be called)
	    //   socket is final, so it always points to the same ServerSocket; the
	    //       server thread only calls ServerSocket#accept and any other
	    //       thread can only call ServerSocket#close; the docs for
	    //       ServerSocket#close specify the behavior in this case, implying
	    //		 that our use is thread-safe
	    //   listeningChangeLock is immutable, and is only used as a lock around
	    //        listening
	    //   accesses to listening are always synchronized by
	    //		  listeningChangeLock; no methods are called in the synchronized
	    //        sections, so no deadlock can occur
	    
	    /**
	     * Make a MinesweeperServer that listens for connections on port.
	     * 
	     * @param port port number, requires 0 <= port <= 65535
	     */
	    public ListenController(BlockingQueue<Request> requestQueue, int port)
	    		throws IOException {
	    	assert requestQueue != null;
	    	
	    	this.requestQueue = requestQueue;
	        acceptSocket = new ServerSocket(port);
	        listeningChangeLock = new Object();
	        listening = true;
	    }

		@Override
		public void run() {
	        while (true) {
	        	synchronized (listeningChangeLock) {
	        		if (!listening)
	        			break;
	        	}
	        	
	        	try {
	        		Socket serverToClientSocket = acceptSocket.accept();
	        		serverToClientSocket.setKeepAlive(true);
	        		serverToClientSocket.setTcpNoDelay(true);
	        		
	        		BlockingQueue<Message> clientQueue =
	        				new LinkedBlockingQueue<Message>();
	        		SocketPusher pusher = new SocketPusher(
	        				clientQueue, serverToClientSocket);
	        		Thread pusherThread = new Thread(pusher, "Server Pusher");
		            pusherThread.start();
		            
		            ServerFetcher clientFetcher = new ServerFetcher(
		            		requestQueue, serverToClientSocket, clientQueue);
		            Thread fetcherThread = new Thread(clientFetcher,
		            		"Server Fetcher");
		            fetcherThread.start();
	        	} catch (IOException e) {
	        		// This can happen if #stopListening() is called, or if
	        		// the OS errors in the system call, for some reason.
	        		continue;
	        	}
	        }
	        
	        try {
	        	acceptSocket.close();
	        } catch (IOException e) {
        		// Shutting down, nothing we can do about this.
	        }
		}
		
		/**
		 * Stops the thread running this controller's code.
		 * 
		 * This method is thread-safe and idempotent.
		 */
		public void stopListening() {
			synchronized (listeningChangeLock) {
				if (!listening)
					return;
				listening = false;
			}
			try {
				// Causes ServerSocket#accept to throw a SocketException in
				// the server thread.
				acceptSocket.close();
	        } catch (IOException e) {
        		// Shutting down, nothing we can do about this.
	        }
		}
	}
	
	/** The code that runs on the console thread. */
	private class ConsoleController implements Runnable {
		/** Accepts requests for the board links thread. */
		private final BlockingQueue<Request> requestQueue;
	    /** Wrapper around the console stream. */
	    private final BufferedReader consoleReader;

	    public ConsoleController(BlockingQueue<Request> requestQueue,
	    		InputStream consoleStream) {
	    	assert requestQueue != null;
	    	assert consoleStream != null;
	    	
	    	this.requestQueue = requestQueue;
	    	this.consoleReader = new BufferedReader(new InputStreamReader(
	    			consoleStream));
	    }
	    
	    @Override
		public void run() {
	    	try {
		    	while (true) {
		    		String line = consoleReader.readLine();
		    		if (line == null) {
		    			// The console stream was closed.
		    			return;
		    		}
		    		ServerConsoleMessage message = new ServerConsoleMessage(line);
		    		Request request = new Request(null, message);
		    		requestQueue.put(request);
		    	}
	    	} catch (IOException e) {
	    		// Something went wrong while reading from the console.
	    		// Not much we can do about it.
	    		e.printStackTrace();
	    	} catch (InterruptedException e) {
				// Someone else wants us to exit.	    		
	    	}
		}
	}
}