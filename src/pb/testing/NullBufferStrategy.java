package pb.testing;

import java.awt.BufferCapabilities;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

/** Placeholder {@link BufferStrategy} implementation used for testing. */
public class NullBufferStrategy extends BufferStrategy {
	@Override
	public BufferCapabilities getCapabilities() { return null; }
	@Override
	public Graphics getDrawGraphics() { return null; }
	@Override
	public boolean contentsLost() { return false; }
	@Override
	public boolean contentsRestored() { return false; }
	@Override
	public void show() { }
}