package pb.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import pb.proto.Message;
import pb.render.Viewport;

public class ClientUiFrame extends JFrame implements ClientUi {
	/** Required by serializable. */
	private static final long serialVersionUID = 1L;
	/** The board is painted on this. */
	private final Canvas boardCanvas;

	/** The client controller's receive queue. */
	private BlockingQueue<Message> controllerQueue;
	/** The currently loaded board. */
	private String boardName;
	/** True if this client is connected to a server. */
	private boolean hasServer;
	
	public ClientUiFrame(BlockingQueue<Message> controllerQueue) {
		super("Pingball Client");
		assert SwingUtilities.isEventDispatchThread();
		assert controllerQueue != null;
		
		this.boardName = "(no board)";
		this.hasServer = false;
		updateWindowTitle();
		
		setupMenu();
		
		Container pane = getContentPane();
		boardCanvas = new Canvas();
		boardCanvas.setPreferredSize(new Dimension(100, 100));
		pane.add(boardCanvas, BorderLayout.CENTER);
		
		setupListeners();
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		boardCanvas.setIgnoreRepaint(true);
		boardCanvas.createBufferStrategy(2);
		
		this.controllerQueue = controllerQueue;
	}
	
	@Override
	public void setBoardViewport(Viewport boardViewport) {
		assert SwingUtilities.isEventDispatchThread();
		assert boardViewport != null;
		boardCanvas.setPreferredSize(new Dimension(boardViewport.xPixels(),
				boardViewport.yPixels()));
		this.pack();
		controllerQueue.add(new UserCommandMessage(
				boardCanvas.getBufferStrategy()));
	}
	
	@Override
	public void setBoardName(String boardName) {
		this.boardName = boardName;
		updateWindowTitle();
	}

	@Override
	public void setHasServer(boolean hasServer) {
		this.hasServer = hasServer;		
		updateWindowTitle();
	}	

	/**
	 * Builds the menu bar at the top of the UI.
	 * 
	 * This must be called exactly once, by the constructor.
	 */
	private void setupMenu() {
		assert SwingUtilities.isEventDispatchThread();
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu gameMenu = new JMenu("Game");
		menuBar.add(gameMenu);
		JMenuItem pauseItem = new JMenuItem("Pause");
		pauseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onPauseMenuItem();
			}
		});
		gameMenu.add(pauseItem);
		JMenuItem resumeItem = new JMenuItem("Resume");
		resumeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onResumeMenuItem();
			}
		});
		gameMenu.add(resumeItem);
		JMenuItem loadBoardItem = new JMenuItem("Load board...");
		loadBoardItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onLoadMenuItem();
			}
		});
		gameMenu.add(loadBoardItem);
		JMenuItem restartItem = new JMenuItem("Restart");
		restartItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onRestartMenuItem();
			}
		});
		gameMenu.add(restartItem);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onExitMenuItem();
			}
		});
		gameMenu.add(exitItem);
		
		JMenu serverMenu = new JMenu("Server");
		menuBar.add(serverMenu);
		JMenuItem connectItem = new JMenuItem("Connect");
		connectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onConnectMenuItem();
			}
		});
		serverMenu.add(connectItem);
		JMenuItem disconnectItem = new JMenuItem("Disconnect");
		disconnectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDisconnectMenuItem();
			}
		});
		serverMenu.add(disconnectItem);		
		
		this.setJMenuBar(menuBar);
	}
	
	/**
	 * Wires up the event listeners for the window.
	 * 
	 * This must be called exactly once, by the constructor.
	 */
	private void setupListeners() {
		assert SwingUtilities.isEventDispatchThread();
		
		KeyListener keyListener = new KeyAdapter() {			
			@Override
			public void keyPressed(KeyEvent e) {
				onKeyEvent(e.getKeyCode(), true);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				onKeyEvent(e.getKeyCode(), false);
			}
		}; 
		this.addKeyListener(keyListener);
		boardCanvas.addKeyListener(keyListener);
		boardCanvas.requestFocus();
		this.addWindowListener(new WindowAdapter() {			
			@Override
			public void windowClosing(WindowEvent e) {
				controllerQueue.add(new UserCommandMessage(
						UserCommandMessage.Type.EXIT));
			}
		});		
	}
	
	/** Called when the user presses a key. */
	private void onKeyEvent(int keyCode, boolean pressed) {
		String keyName = KeyEvent.getKeyText(keyCode);
		UserInputMessage.Type type = pressed ? UserInputMessage.Type.PRESS :
				UserInputMessage.Type.RELEASE;
		controllerQueue.add(new UserInputMessage(type, keyName));
	}
	
	/** Called when the user selects Load from the menu. */
	private void onLoadMenuItem() {
		JFileChooser chooser = new JFileChooser();
		int answer = chooser.showOpenDialog(this);
		if (answer != JFileChooser.APPROVE_OPTION)
			return;
		
		File boardFile = chooser.getSelectedFile();
		controllerQueue.add(new UserCommandMessage(boardFile));
	}
	
	/** Called when the user selects Pause from the menu. */
	private void onPauseMenuItem() {
		controllerQueue.add(new UserCommandMessage(
				UserCommandMessage.Type.PAUSE));		
	}
	
	/** Called when the user selects Resume from the menu. */
	private void onResumeMenuItem() {
		controllerQueue.add(new UserCommandMessage(
				UserCommandMessage.Type.RESUME));		
	}
	
	private void onRestartMenuItem() {
		controllerQueue.add(new UserCommandMessage(
				UserCommandMessage.Type.RESTART));
	}
	
	/** Called when the user selects Exit from the menu. */
	private void onExitMenuItem() {
		controllerQueue.add(new UserCommandMessage(
				UserCommandMessage.Type.EXIT));
		this.dispose();
	}
	
	/** Called when the user selects Connect from the menu. */
	private void onConnectMenuItem() {
		String host = (String)JOptionPane.showInputDialog(this,
				"Please enter the server's hostname",
				"Connect to Pingball server",
				JOptionPane.PLAIN_MESSAGE,
				null, null, "localhost");
		if (host == null)
			return;
		
		String portString = (String)JOptionPane.showInputDialog(this,
				"Please enter the server's port",
				"Connect to Pingball server",
				JOptionPane.PLAIN_MESSAGE,
				null, null, "10987");
		if (portString == null)
			return;
		
		try {
			int port = Integer.parseInt(portString);
			controllerQueue.add(new UserCommandMessage(host, port));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Invalid port number.",
					"Connect to Pingball server",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/** Called when the user selects Disconnect from the menu. */
	private void onDisconnectMenuItem() {
		controllerQueue.add(new UserCommandMessage(
				UserCommandMessage.Type.DISCONNECT));
	}

	/** Updates the window's title when the client state changes. */
	private void updateWindowTitle() {
		String title = "Pingball Client";
		if (boardName == null)
			title = title + "- (no board)";
		else
			title = title + " - " + boardName;			

		if (hasServer)
			title = title + " - connected";
		else
			title = title + " - no server";
		
		this.setTitle(title);
	}
}