package pb.render;

import java.awt.Color;
import java.awt.Graphics2D;

import pb.board.Gizmo;
import pb.gizmos.Ball;
import pb.gizmos.Portal;
import physics.Circle;
import physics.Vect;

/**
 * Renders any {@link Ball}.
 * 
 * A single {@link PortalRenderer} instance can handle all the portals on the
 * board.
 */
class PortalRenderer extends Renderer {
	public void render(Gizmo gizmo, Graphics2D context) {
		assert gizmo instanceof Portal;
		Portal portal = (Portal)gizmo;
		Circle circle = portal.getShape();
		Vect center = circle.getCenter();
		double r = circle.getRadius();
		
		context.setColor(Color.YELLOW);
		context.fillOval(
				viewport().x(center.x() - r),
				viewport().y(center.y() - r),
				viewport().dx(2 * r), viewport().dy(2 * r));
	}
	
	/**
	 * Creates a renderer for objects whose shapes are circles.
	 * 
	 * @param viewport board rendering parameters
	 */
	public PortalRenderer(Viewport viewport) {
		super(viewport);
	}
}