package pb.render;

import java.awt.Color;
import java.awt.Graphics2D;

import pb.board.Gizmo;
import pb.gizmos.Absorber;
import physics.Vect;

/**
 * Renders any {@link Absorber}.
 * 
 * A single {@link AbsorberRenderer} instance can handle all the absorbers on
 * the board.
 */
class AbsorberRenderer extends Renderer {
	/**
	 * Creates a renderer for absorbers.
	 * 
	 * The renderer is not tied to any {@link Absorber} instance. It can render
	 * any absorber on the board.
 	 *
	 * @param viewport rendering parameters
	 */
	public AbsorberRenderer(Viewport viewport) {
		super(viewport);
	}

	@Override
	public void render(Gizmo gizmo, Graphics2D context) {
		assert gizmo instanceof Absorber;
		Absorber absorber = (Absorber)gizmo;		
		Vect origin = absorber.getOrigin();
		
		context.setColor(Color.PINK);
		context.fillRect(viewport().x(origin.x()),
				viewport().y(origin.y()),
				viewport().dx(absorber.getWidth()),
				viewport().dy(absorber.getHeight()));
	}	
}