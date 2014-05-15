package pb.testing;

import java.awt.Graphics2D;

import pb.board.Gizmo;
import pb.render.Viewport;
import pb.render.Renderer;

/** No-op renderer used for testing. */
public class NullRenderer extends Renderer {
	public NullRenderer(Viewport viewport) {
		super(viewport);
	}
	
	@Override
	public void render(Gizmo gizmo, Graphics2D context) { }
}