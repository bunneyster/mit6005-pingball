package pb.client;

import java.awt.Frame;

import pb.render.Viewport;

/**
 * The interface between the client UI and the controller.
 *
 * The UI is implemented by {@link ClientUiFrame}, which extends {@link Frame}
 * and inherits a lot of public methods from it. The rest of the game code
 * accesses the {@link ClientUiFrame} via this interface, to avoid directly
 * calling random {@link Frame} methods.
 * 
 * All method calls must be performed on the Swing event dispatch thread.
 */
public interface ClientUi {
	/**
	 * Informs the UI when a new board is loaded.
	 * 
	 * @param boardViewport the new board's rendering settings
	 */
	public void setBoardViewport(Viewport boardViewport);
	
	/**
	 * Informs the UI when the current board's name is changed.
	 * 
	 * @param boardName the new board's name
	 */
	public void setBoardName(String boardName);

	/**
	 * Informs the UI when the server connectivity status has changed
	 * 
	 * @param hasServer true if this client is connected to a server, false if
	 *   it is operating standalone
	 */
	public void setHasServer(boolean hasServer);
}