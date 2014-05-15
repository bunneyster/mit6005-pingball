package pb.render;

import java.awt.Color;
import java.awt.Graphics2D;

import pb.board.Gizmo;
import pb.gizmos.Ball;
import physics.Circle;
import physics.Vect;

/**
 * Renders any {@link Ball}.
 * 
 * A single {@link BallRenderer} instance can handle all the balls on the board.
 */
class BallRenderer extends Renderer {
	/**
	 * Creates a render for balls.
	 * 
	 * The renderer is not tied to any {@link Ball} instance. It can render any
	 * ball on the board.
	 * 
	 * @param viewport rendering parameters
	 */
	public BallRenderer(Viewport viewport) {
		super(viewport);
	}
	
	@Override
	public void render(Gizmo gizmo, Graphics2D context) {
		assert gizmo instanceof Ball;
		Ball ball = (Ball)gizmo;
		Circle circle = ball.getShape();
		Vect center = circle.getCenter();
		double r = circle.getRadius();
		
		context.setColor(Color.BLUE);
		context.fillOval(
				viewport().x(center.x() - r),
				viewport().y(center.y() - r),
				viewport().dx(2 * r), viewport().dy(2 * r));
	}
}