package pb.testing;

import pb.board.Gizmo;

/** A gizmo that can be directly triggered, for testing. */
public class Triggerable extends Gizmo {
	public Triggerable() {
		super("triggerable");
	}
	
	@Override
	public void advanceTime(double timeStep) { }

	@Override
	protected void doAction() { }
	
	@Override
	public void trigger() { super.trigger(); }
}