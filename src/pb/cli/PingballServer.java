package pb.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import pb.server.BoardLinks;
import pb.server.Dispatcher;
import pb.server.ServerController;

public class PingballServer {
    /**
     * Joins the outer walls of clients together, so that a ball exiting one client's
     * playing area can enter another client.
     * 
     * Usage: PingballServer [--port PORT]
     * 
     * PORT is an optional integer in the range 0 to 65535 inclusive, 
     * specifying the port where the server should listen for incoming connections. 
     * The default port is 10987.
     * 
     * @param args
     */
	public static void main(String[] args) {
        int port = 10987;  // default port
        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while (!arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > 65535) {
                            throw new IllegalArgumentException(
                            		"port " + port + " out of range");
                        }
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException(
                    		"missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException(
                    		"unable to parse number for " + flag);
                }
            }
            runServer(port);
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: PingballServer [--port PORT]");
            return;
        } catch(IOException e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Starts a PingballServer at the specified port.
	 * 
	 * @param port The network port on which the server should listen.
	 * @throws IOException
	 */
	public static void runServer(int port) throws IOException {
		BoardLinks boardLinks = new BoardLinks();
		
		ServerController serverController = new ServerController(boardLinks);
		Thread serverThread = new Thread(serverController, "Board Links");
		serverThread.start();

		Dispatcher dispatcher = new Dispatcher(
				serverController.getRequestQueue(), port, System.in);
		dispatcher.serve();
	}
}