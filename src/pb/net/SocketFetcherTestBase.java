package pb.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.After;
import org.junit.Before;

import pb.proto.Message;

/** Setup for testing SocketFetcher subclasses. */
public abstract class SocketFetcherTestBase<T> {
	/** Creates the SocketFetcher instance that will be tested. */
	protected abstract SocketFetcher<T> createFetcher(
			BlockingQueue<T> recvQueue, Socket fetcherSocket,
			BlockingQueue<Message> pusherQueue) throws IOException;

	/** The SocketFetcher's queue. */
	protected BlockingQueue<T> recvQueue;
	/** The SocketFetcher's pusher queue. */
	protected BlockingQueue<Message> pusherQueue;
	/** The SocketFetcher's socket. */
	protected Socket fetcherSocket;
	/** The thread running the SocketFetcher code. */
	protected Thread fetcherThread;
	/** A socket connected to the SocketFetcher's socket. */
	protected Socket clientSocket;
	/** Wraps the socket connected to the SocketFetcher's socket. */
	protected PrintWriter clientWriter;

	private SocketFetcher<T> fetcher;
	private ServerSocket serverSocket;
	private Object lock;
	
	@Before
	public void setUp() throws Exception {
		lock = new Object();
		recvQueue = new ArrayBlockingQueue<T>(1);
		pusherQueue = new ArrayBlockingQueue<Message>(1);
		// The lock ensures that the write goes to serverThread.
		synchronized (lock) {
			serverSocket = new ServerSocket(0);
		}
		Thread serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// The lock ensures that the writes go to the main thread.
					synchronized (lock) {
						fetcherSocket = serverSocket.accept();
						fetcher = createFetcher(recvQueue, fetcherSocket,
								pusherQueue);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		serverThread.start();
		clientSocket = new Socket(InetAddress.getLocalHost(),
				serverSocket.getLocalPort());
		clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
		serverThread.join();
		
		// The lock ensures that the writes from serverThread are received.
		synchronized (lock) {
			fetcherThread = new Thread(fetcher, "Fetcher");
			fetcherThread.start();
		}
	}
	
	@After
	public void tearDown() throws Exception {
		if (!clientSocket.isClosed())
			clientSocket.close();
		fetcherThread.join();
		serverSocket.close();
	}
}
