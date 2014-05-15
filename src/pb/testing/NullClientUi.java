package pb.testing;

import static org.junit.Assert.fail;

import javax.swing.SwingUtilities;

import pb.client.ClientUi;
import pb.render.Viewport;

/** Stub {@link ClientUi} implementation. */
public class NullClientUi implements ClientUi {
	private Viewport boardViewport;
	private Object boardViewportLock;

	private String boardName;
	private Object boardNameLock;
	
	private boolean hasServer;
	private Object lock;
	
	public NullClientUi() {
		boardViewportLock = new Object();
		boardViewport = null;

		boardNameLock = new Object();
		boardName = null;
		
		lock = new Object();
		hasServer = false;
	}
	public Viewport getBoardViewport() {
		synchronized (boardViewportLock) {
			return boardViewport;
		}
	}
	public void waitForBoadViewportChange(Viewport oldValue)
			throws InterruptedException {
		synchronized (boardViewportLock) {
			while (boardViewport == oldValue)
				boardViewportLock.wait();
		}
	}
	public String getBoardName() {
		synchronized (lock) {
			return boardName;
		}
	}
	public void waitForBoadNameChange(String oldValue)
			throws InterruptedException {
		synchronized (boardNameLock) {
			while (boardName == oldValue)
				boardNameLock.wait();
		}
	}
	public boolean getHasServer() {
		synchronized (lock) {
			return hasServer;
		}
	}		
	public void waitForHasServerChange(boolean oldValue)
			throws InterruptedException {
		synchronized (lock) {
			while (hasServer == oldValue)
				lock.wait();
		}
	}
	@Override
	public void setBoardViewport(Viewport boardViewport) {
		if (!SwingUtilities.isEventDispatchThread())
			fail("ClientUi method called outside the Swing event thread");
		synchronized (boardViewportLock) {
			this.boardViewport = boardViewport;
			boardViewportLock.notifyAll();
		}
	}
	@Override
	public void setBoardName(String boardName) {
		if (!SwingUtilities.isEventDispatchThread())
			fail("ClientUi method called outside the Swing event thread");
		synchronized (boardNameLock) {
			this.boardName = boardName;
			boardNameLock.notifyAll();
		}
	}
	@Override
	public void setHasServer(boolean hasServer) {
		if (!SwingUtilities.isEventDispatchThread())
			fail("ClientUi method called outside the Swing event thread");
		synchronized (lock) {
			this.hasServer = hasServer;
			lock.notifyAll();
		}
	}		
}