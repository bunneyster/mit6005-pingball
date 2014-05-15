package pb.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import pb.board.Gizmo;
import pb.gizmos.Flipper;

/** 
 * Renders a {@link Flipper}.
 * 
 * Flippers do not move on the board. Therefore, each flipper has its own
 * renderer that caches its position and renders it quickly.
 */
class FlipperRenderer extends Renderer {
	/** The flipper rendered by this renderer. */
	private final Flipper flipper;
	
	public FlipperRenderer(Viewport viewport, Flipper flipper) {
		super(viewport);
		this.flipper = flipper;		
	}
		
	@Override
	public void render(Gizmo gizmo, Graphics2D context) {
		assert this.flipper == gizmo;

		pb.board.Shape shape = flipper.getShape();
		Shape renderShape = BumperRenderer.toRenderShape(shape, viewport());
		
		context.setColor(Color.YELLOW);
		context.fill(renderShape);
	}
}