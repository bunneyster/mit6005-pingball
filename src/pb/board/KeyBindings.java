package pb.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pb.client.UserInputMessage;

/**
 * Stores the key bindings for a board.
 * 
 * Instances of this class are not thread-safe and should be contained to the
 * same threads as the owning {@link Board} instances.
 */
public class KeyBindings {
	/** The bindings for a key press. */
	private final Map<String, List<Gizmo>> press;
	/** The bindings for a key release. */
	private final Map<String, List<Gizmo>> release;
	
	// Rep invariant:
	//   press and release are non-null
	//   the keys and gizmo lists in both maps are non-null
	//   all the gizmos in the lists are non-null
	
	/** Creates an empty bindings table. */
	public KeyBindings() {
		press = new HashMap<String, List<Gizmo>>();
		release = new HashMap<String, List<Gizmo>>();
		assert checkRep();
	}
	
	/**
	 * Sets up a key binding.
	 * 
	 * @param keyName the name of the key to be pressed or released
	 * @param press if true, the target element's action will be triggered when 
	 *   the key is pressed; if false, the action will be triggered when the key
	 *   is released
	 * @param listener the board element whose action will be triggered 
	 */
	public void addListener(String keyName, boolean press, Gizmo listener) {
		assert keyName != null;
		assert listener != null;
		
		Map<String, List<Gizmo>> bindings = getBindings(press);
		List<Gizmo> listeners = bindings.get(keyName);
		if (listeners == null) {
			listeners = new ArrayList<Gizmo>();
			bindings.put(keyName, listeners);
		}
		listeners.add(listener);
		assert checkRep();		
	}
	
	/**
	 * Copies the list of all targets for a key binding.
	 * 
	 * This method is inefficient, and is only intended for testing.
	 * 
	 * @param keyName the name of the key to be pressed or released
	 * @param press if true, obtains the elements whose actions will be
	 * 	 triggered when the key is pressed; if false, obtains the elements
	 *   whose actions be triggered when the key is released
	 * @return a copy of the list of listeners for the specified key press /
	 *   release
	 */
	public Gizmo[] copyListeners(String keyName, boolean press) {
		assert keyName != null;
		
		Map<String, List<Gizmo>> bindings = getBindings(press);
		List<Gizmo> listeners = bindings.get(keyName);
		if (listeners == null)
			return EMPTY_LISTENERS_ARRAY;
		return listeners.toArray(EMPTY_LISTENERS_ARRAY);
	}
	/** Empty array of {@link Gizmo}s, for working around the type system. */
	private static final Gizmo[] EMPTY_LISTENERS_ARRAY = new Gizmo[0];
	
	/**
	 * Triggers board element actions according to these key bindings.
	 * 
	 * This method should only be called by the {@link Board} that owns these
	 * key bindings.
	 * 
	 * @param message specifies a key that the user has pressed / released
	 */
	void dispatch(UserInputMessage message) {
		assert message.getType() == UserInputMessage.Type.PRESS ||
			   message.getType() == UserInputMessage.Type.RELEASE;
		
		boolean press = message.getType() == UserInputMessage.Type.PRESS;
		Map<String, List<Gizmo>> bindings = getBindings(press);
		List<Gizmo> listeners = bindings.get(message.getKeyName());
		if (listeners == null)
			return;
		for (Gizmo listener : listeners)
			listener.doAction();
	}
	
	/**
	 * Returns the bindings for key presses / releases.
	 * 
	 * @param press true to request the key press bindings, false to request the
	 *   key release bindings
	 * @return the referenced bindings
	 */
	private Map<String, List<Gizmo>> getBindings(boolean press) {
		return press ? this.press : this.release;
	}
	
	/** True if this instance's representation invariant holds. */
	private boolean checkRep() {
		if (press == null)
			return false;
		if (!checkRepBindings(press))
			return false;
		if (release == null)
			return false;
		if (!checkRepBindings(release))
			return false;
		return true;
	}
	/** True if the representation invariant for a  key bindings map holds. */
	private boolean checkRepBindings(Map<String, List<Gizmo>> bindings) {
		for (Entry<String, List<Gizmo>> entry : bindings.entrySet()) {
			if (entry.getKey() == null)
				return false;
			List<Gizmo> listeners = entry.getValue();
			if (listeners == null)
				return false;
			for (Gizmo listener : listeners) {
				if (listener == null)
					return false;
			}
		}
		return true;
	}
}
