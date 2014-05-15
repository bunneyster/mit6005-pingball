package pb.cli;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import pb.client.ClientController;
import pb.client.ClientUi;
import pb.client.ClientUiFrame;
import pb.client.UserCommandMessage;
import pb.proto.Message;

public class PingballClient {
    /**
     * Loads a board file and begins playing it.
     * 
     * Usage: PingballClient [--host HOST] [--port PORT] [FILE]
     * 
     * HOST is an optional hostname or IP address of the server 
     * to connect to. If no HOST is provided, then the client 
     * starts in single-machine play mode, as described above.
     * 
     * PORT is an optional integer in the range 0 to 65535 inclusive, 
     * specifying the port where the server is listening for incoming
     * connections. The default port is 10987.
     * 
     * FILE is an optional argument specifying a file pathname of the Pingball
     * board that this client should run.
     * 
     * @param args
     */
	public static void main(String[] args) {
        int port = 10987;  // default port
        String host = null;
        File boardFile = null;
        
        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while (arguments.size() > 1) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > 65535) {
                            throw new IllegalArgumentException(
                            		"port " + port + " out of range");
                        }
                    } else if (flag.equals("--host")) {
                        host = arguments.remove();
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException(
                    		"missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException(
                    		"unable to parse number for " + flag);
                }
            }
            
            if (arguments.size() == 1) {
            	boardFile = new File(arguments.remove());
                if (!boardFile.isFile()) {
                    throw new IllegalArgumentException(
                    		"file not found: \"" + boardFile + "\"");
                }            
            }
            else {
            	boardFile = null;
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: PingballClient [--host PORT] " +
            		"[--port PORT] [--term] [path/to/board_file.pb]");
            return;
        }
        runClient(boardFile, host, port);
	}
	
	/**
	 * Starts a PingballClient running the specified board and connected to the
	 * specified server.
	 * 
	 * @param boardFile the client's board
	 * @param host the hostname of the server
	 * @param port the network port where the server is listening
	 */
	public static void runClient(final File boardFile, final String host,
			final int port) {
		final BlockingQueue<Message> controllerQueue =
				new LinkedBlockingQueue<Message>();
		
		if (boardFile != null) {
			// Load a board file, if it was specified on the command line.
			controllerQueue.add(new UserCommandMessage(boardFile));
		}
		if (host != null) {
			// Connect to a server, if it was specified on the command line.
			controllerQueue.add(new UserCommandMessage(host, port));
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ClientUi ui = new ClientUiFrame(controllerQueue);
				
				ClientController controller = new ClientController(
						controllerQueue, ui);
				Thread controllerThread = new Thread(controller);
				controllerThread.setName("Board");
				controllerThread.start();
			}
		});
	}
}