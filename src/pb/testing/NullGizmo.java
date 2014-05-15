package pb.testing;

import pb.board.Gizmo;

/** A gizmo that counts how many times its action has been triggered. */
public class NullGizmo extends Gizmo {
	private int m_actionCount = 0;
	
	public NullGizmo() {
		super("null");
	}
	public NullGizmo(String name) {
		super(name);
	}
	
	@Override
	public void advanceTime(double timeStep) { }
	
	@Override
	protected void doAction() { ++m_actionCount; }
	
	public void callTrigger() { trigger(); }
	
	public int actionCount() { return m_actionCount; }
}