package pb.client;

import pb.board.Board;
import pb.render.RenderManager;

public class SimulationController {
	/** The board being simulated. */
	private final Board board;
	
	private double oldBoardTime;
	private double oldRealTimeMs;
	
	public SimulationController(Board board) {
		assert board != null;
		assert board.getRenderer() != null;
		
		this.board = board;
		this.oldBoardTime = board.getTime();
		this.oldRealTimeMs = System.currentTimeMillis();
	}
	
	public void simulateAndRender() throws InterruptedException {
    	board.simulate(0.05);
    	RenderManager renderer = board.getRenderer();
    	renderer.renderFrame();
		
    	double newBoardTime = board.getTime();
    	double newRealTimeMs = System.currentTimeMillis();
    	double realTimeDeltaMs = newRealTimeMs - oldRealTimeMs;
    	double boardTimeDeltaMs = (newBoardTime - oldBoardTime) * 1000;
    	
    	oldBoardTime = newBoardTime;
    	oldRealTimeMs = newRealTimeMs;
    	if (boardTimeDeltaMs > realTimeDeltaMs) {
    		Thread.sleep((long)(boardTimeDeltaMs - realTimeDeltaMs));
    	} else {
    		// We're lagging, but there's not much we can do right now.
    	}    	
	}
}
