package pb.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import pb.board.Gizmo;
import pb.gizmos.BumperBase;
import pb.gizmos.CircleBumper;
import pb.gizmos.SquareBumper;
import pb.gizmos.TriangleBumper;
import physics.Circle;
import physics.LineSegment;

/** 
 * Renders a {@link BumperBase} instance.
 * 
 * Bumpers do not move on the board. Therefore, each bumper has its own renderer
 * that caches its position and renders it very quickly.
 */
class BumperRenderer extends Renderer {
	/** The bumper rendered by this renderer. */
	private final BumperBase bumper;
	/** The color used to render this renderer's bumper. */
	private final Color color;
	/** The rendered shape of this renderer's bumper. */
	private final Shape renderShape;
	
	public BumperRenderer(Viewport viewport, BumperBase bumper) {
		super(viewport);
		this.bumper = bumper;
		this.renderShape = BumperRenderer.toRenderShape(bumper.getShape(),
				viewport);
		
		Color defaultColor;
		if (bumper instanceof CircleBumper)
			defaultColor = Color.GREEN;
		else if (bumper instanceof SquareBumper)
			defaultColor = Color.RED;
		else if (bumper instanceof TriangleBumper)
			defaultColor = Color.BLUE;
		else
			throw new UnsupportedOperationException("Unrecognized bumper type");
		
		String styleColorString = bumper.getStyle().value("color", null);
		if (styleColorString != null)
			this.color = Color.decode("0x" + styleColorString);
		else
			this.color = defaultColor;
	}
	
	@Override
	public void render(Gizmo gizmo, Graphics2D context) {
		assert this.bumper == gizmo;
		
		context.setColor(color);
		context.fill(renderShape);
	}
	
	// TODO: move this to a generic class
	static java.awt.Shape toRenderShape(pb.board.Shape shape,
			Viewport viewport) {
		LineSegment[] sides = shape.copySides();
		
		Path2D.Double path = new Path2D.Double();
		if (sides.length != 0) {
			path.moveTo(viewport.x(sides[0].p1().x()),
					viewport.y(sides[0].p1().y()));
			for (int i = 0; i < sides.length; ++i) {
				path.lineTo(viewport.x(sides[i].p2().x()),
						viewport.y(sides[i].p2().y()));
			}
		}
		
		Circle[] circles = shape.copyCorners();
		for (Circle circle : circles) {
			double r = circle.getRadius();
			if (r == 0)
				continue;
			double x = circle.getCenter().x();
			double y = circle.getCenter().y();
			Ellipse2D.Double circlePath = new Ellipse2D.Double(
					viewport.x(x - r), viewport.y(y - r),
					viewport.dx(2 * r), viewport.dy(2 * r));
			path.append(circlePath, false);
		}
		
		return path;
	}	
}