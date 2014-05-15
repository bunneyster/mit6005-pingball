package pb.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import pb.board.Gizmo;
import pb.gizmos.Wall;
import physics.Circle;

/**
 * Renders a wall.
 *
 * Walls require some special treatment, because their coordinates for physics
 * purposes are different from their coordinates for drawing purposes.
 * Therefore, each wall gets its own renderer.
 */
class WallRenderer extends Renderer {
	/** The wall rendered by this renderer. */
	private final Wall wall;
	/** The font used to render the wall's label. */
	private final Font font;
	/** The X coordinate of the rendered wall's origin, in render space. */
	private final int x;
	/** The Y coordinate of the rendered wall's origin, in render space. */
	private final int y;
	/** The wall's width, in pixels. */
	private final int w;
	/** The wall's height, in pixels. */
	private final int h;
	/** True if the wall is horizontal. */
	private final boolean isHorizontal;
	
	@Override
	public void render(Gizmo gizmo, Graphics2D context) {
		assert gizmo == wall;
		
		context.setColor(Color.LIGHT_GRAY);
		context.fillRect(x, y, w, h);
		
		String label = wall.getNeighborName();
		if (label == null)
			return;

		context.setColor(Color.BLACK);
		context.setFont(font);
		FontMetrics fontMetrics = context.getFontMetrics();
		Rectangle2D labelBounds = fontMetrics.getStringBounds(label, context);
		double labelWidth = labelBounds.getWidth();
		double labelHeight = labelBounds.getHeight();
		
		if (isHorizontal) {
			double labelBaseline = fontMetrics.getLeading() +
					fontMetrics.getAscent();
			context.drawString(label, (int)(x + (w - labelWidth) / 2),
					(int)(y + (h - labelHeight) / 2 + labelBaseline));
		} else {
			AffineTransform originalTransform = context.getTransform();
			
			double labelDescent = fontMetrics.getDescent();
			AffineTransform transform = new AffineTransform();
			int xStart = (int)(x + (w - labelHeight) / 2 + labelDescent);
			int yStart = (int)(y + (h - labelWidth) / 2);
			transform.translate(xStart, yStart);
			transform.quadrantRotate(1);
			context.setTransform(transform);			
			FontRenderContext fontContext = new FontRenderContext(null,
					false, true);
			GlyphVector glyphVector = font.createGlyphVector(
					fontContext, label);
			context.drawGlyphVector(glyphVector, 0, 0);
			context.setTransform(originalTransform);			
		}
	}

	public WallRenderer(Wall wall, Viewport viewport) {
		super(viewport);
		this.wall = wall;
		
		Circle[] corners = wall.getShape().copyCorners(); 
		int x1 = (int)corners[0].getCenter().x();
		int y1 = (int)corners[0].getCenter().y();
		int x2 = (int)corners[1].getCenter().x();
		int y2 = (int)corners[1].getCenter().y();
		
		if (x1 == 0) { x1 = -1; }
		if (x2 == 0) { x2 = -1; }
		if (y1 == 0) { y1 = -1; }
		if (y2 == 0) { y2 = -1; }
		
		this.x = viewport.x(x1);
		this.y = viewport.y(y1);
		this.w = viewport.dx(x2 - x1 + 1);
		this.h = viewport.dx(y2 - y1 + 1);
		this.isHorizontal = wall.getIsHorizontal();
		
		Font labelFont = new Font(Font.MONOSPACED, Font.BOLD,
				(int)(viewport.yScale() * 0.6));
		this.font = labelFont;
	}
}