package pb.testing;

import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.AssertionFailedError;

/**
 * Strategy that stores an image buffer for comparison.
 * 
 * After creating an instance, call {@link #getDrawGraphics()} to obtain a
 * {@link Graphics2D} context and pass it to the render 
 */
public class ImageDumper extends BufferStrategy {
	/** The size of the image buffer. */
	private final int xSize, ySize;
	/** The initial color of the image buffer. */
	private final Color background;
	/** True if getGraphics() / show() can be called multiple times. */
	private final boolean allowRepaint;
	/**
	 * The image buffer.
	 * 
	 * This starts out as null, and is set after {@link #getDrawGraphics()} is
	 * called.
	 */
	private BufferedImage image;
	/**
	 * Drawing context for the image buffer.
	 * 
	 * This starts out as null, is set after {@link #getDrawGraphics()} is
	 * called, and is reset to null after {@link #show()} is called. 
	 */	
	private Graphics2D context;
	/** The number of times show() was called. */
	private int showCount;
	
	/** The path where the reference test images are stored. */
	public static final String REFERENCE_PATH = "resources/renderTests/";
	
	public ImageDumper(int xSize, int ySize) {
		this(xSize, ySize, Color.WHITE, false);
	}

	public ImageDumper(int xSize, int ySize, Color background) {
		this(xSize, ySize, background, false);
	}

	public ImageDumper(int xSize, int ySize, boolean allowRepaint) {
		this(xSize, ySize, Color.WHITE, allowRepaint);
	}
	
	public ImageDumper(int xSize, int ySize, Color background,
			boolean allowRepaint) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.background = background;
		this.allowRepaint = allowRepaint;
		this.showCount = 0;
		this.image = null;
		this.context = null;		
	}
	
	/**
	 * Compares the buffered image against a reference image.
	 * 
	 * @param imageName the name of the reference image
	 * @throw AssertionFailedError if the image does not match the reference 
	 */
	public void checkAgainst(String imageName) {
		BufferedImage reference;
		try {
			reference = ImageDumper.load(imageName);
		} catch (IOException e) {
			saveActualImage(imageName);
			if (pathFor(imageName).exists()) {
				throw new AssertionFailedError(
						"Could not read reference image " + imageName);
			} else {
				throw new AssertionFailedError(
						"Missing reference image " + imageName);
			}
		}
		
		if (compareImages(reference, image))
			return;
		
		saveActualImage(imageName);
		throw new AssertionFailedError(
				"Image does not match reference " + pathFor(imageName));
	}
	
	/**
	 * Returns a graphics context for drawing to the image buffer.
	 * 
	 * Either this method or {@link #getDrawGraphics()} must be called exactly
	 * once. After either of the methods is called,
	 * {@link #checkAgainst(String)} can be called. 
	 * 
	 * @return a graphics context for drawing to the image buffer
	 */
	public Graphics2D getDrawGraphics2D() {
		if (image != null) {
			if (!allowRepaint) {
				throw new IllegalStateException(
						"getDrawGraphics() already called");
			}
		}
		else {
			image = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
		}
		context = image.createGraphics();
		context.setBackground(background);
		context.clearRect(0, 0, xSize, ySize);
		return context;
	}
	
	/**
	 * The number of times {@link #show()} has been called.
	 * 
	 * @return the number of times {@link #show()} has been called
	 */
	public int getShowCount() {
		return showCount;
	}
	
	/**
	 * Saves the image buffer as an actual (vs. expected) image.
	 * 
	 * This method is called when an image comparison fails. 
	 * 
	 * @param imageName the test image name
	 */
	private void saveActualImage(String imageName) {
		// NOTE: If the reference image does not exist, create it.
		if (pathFor(imageName).exists())
			imageName = imageName + "-actual";
		
		try {
			save(imageName);
		} catch (IOException e) {
			// In some environments, the source tree is set up as read-only.
		}
	}
	
	/**
	 * Saves the image buffer.
	 * 
	 * @param imageName the test image name
	 * @throws IOException
	 */
	private void save(String imageName) throws IOException {
		if (image == null)
			throw new IllegalStateException("getDrawGraphics() not called");
		
		ImageIO.write(image, "png", ImageDumper.pathFor(imageName));
	}
	
	/**
	 * Compares two image buffers.
	 * 
	 * @param reference the reference image
	 * @param actual the image produced by the code being tested
	 * @return true if the images match, false otherwise
	 */
	private static boolean compareImages(BufferedImage reference,
			BufferedImage actual) {
		int width = actual.getWidth();
		int height = actual.getHeight();
		if (reference.getWidth() != width)
			return false;
		if (reference.getHeight() != height)
			return false;
		
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (reference.getRGB(x, y) != actual.getRGB(x, y))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Loads a test image.
	 * 
	 * @param imageName the test image name
	 * @return the image
	 * @throws IOException
	 */
	private static BufferedImage load(String imageName) throws IOException {
		return ImageIO.read(pathFor(imageName));
	}
	
	/**
	 * The canonical path for a test image.
	 * 
	 * @param imageName the name of the test image
	 * @return the test image's canonical path
	 */
	private static File pathFor(String imageName) {
		return new File(REFERENCE_PATH + imageName + ".png");
	}

	@Override
	public BufferCapabilities getCapabilities() {
		return null;
	}
	
	@Override
	public Graphics getDrawGraphics() {
		return getDrawGraphics2D();
	}

	@Override
	public boolean contentsLost() {
		return false;
	}

	@Override
	public boolean contentsRestored() {
		return false;
	}

	@Override
	public void show() {
		if (context == null)
			throw new IllegalStateException("getDrawGraphics() not called");

		if (allowRepaint) {
			context = null;
		} else {
			if (image != null)
				throw new IllegalStateException("show() called twice");			
		}
		showCount += 1;
	}	
}
