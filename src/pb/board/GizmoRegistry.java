package pb.board;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Indexes elements on a board by name.
 *
 * Instances are not thread-safe and should be contained to the same thread as
 * the {@link Board}s that own them.
 */
class GizmoRegistry implements Iterable<Gizmo> {
	/** Gizmos whose names are unique in the registry. */
	private final Map<String, Gizmo> uniques;
	/** Gizmos whose names are shared by at least one other gizmo. */
	private final Map<String, Set<Gizmo>> dupes;
	/** Number of gizmos in the registry. */
	private int m_size;
	/** Counts {@link MobileGizmo}s in the registry. */
	private int m_mobileCount;
	/** Counts {@link SolidGizmo}s in the registry. */
	private int m_solidCount;
	
	// Rep invariant:
	//   uniques and dupes are non-null
	//   the sets of uniques' and dupes' keys are disjoint
	//   the gizmos in uniques and dupes have names matching the keys
	//   each set in dupes has at least two gizmos
	//   size equals the total number of gizmos in uniques and dupes
	//   mobile equals the number of MobileGizmo instances in uniques and dupes
	//   solid equals the number of SolidGizmo instances in uniques and dupes
	// AF:
	//   a registry of the union of gizmos stored in uniques and dupes
	
	/**
	 * Creates an empty gizmo registry.
	 */
	public GizmoRegistry() {
		uniques = new HashMap<String, Gizmo>();
		dupes = new HashMap<String, Set<Gizmo>>();
		m_size = 0;
		m_mobileCount = m_solidCount = 0;
		assert checkRep();
	}
	
	/**
	 * Adds an element to the registry.
	 * 
	 * @param gizmo the element to be added
	 */
	public void add(Gizmo gizmo) {
		assert gizmo != null;
		assert !contains(gizmo);
		
		String name = gizmo.name();
		Set<Gizmo> dupeSet = dupes.get(name);
		if (dupeSet != null) {
			dupeSet.add(gizmo);
		} else {		
			Gizmo newDupe = uniques.remove(name);
			if (newDupe != null) {
				dupeSet = new HashSet<Gizmo>();
				dupeSet.add(gizmo);
				dupeSet.add(newDupe);
				dupes.put(name, dupeSet);
			} else {
				uniques.put(name, gizmo);
			}
		}
		m_size += 1;
		if (gizmo instanceof SolidGizmo) {
			m_solidCount += 1;
			if (gizmo instanceof MobileGizmo)
				m_mobileCount += 1;
		}
		
		assert contains(gizmo);
		assert checkRep();
	}
	
	/**
	 * Removes an element from the registry.
	 * 
	 * @param gizmo the element to be removed; the element must have been
	 *   previously added by a call to {@link #add(Gizmo)}
	 */	
	public void remove(Gizmo gizmo) {
		assert contains(gizmo);
		
		String name = gizmo.name();
		if (uniques.remove(name) == null) {
			Set<Gizmo> nameDupes = dupes.get(name);
			nameDupes.remove(gizmo);
			if (nameDupes.size() == 1) {
				dupes.remove(name);
				Iterator<Gizmo> iterator = nameDupes.iterator();
				uniques.put(name, iterator.next());
			}
		}
		m_size -= 1;
		if (gizmo instanceof SolidGizmo) {
			m_solidCount -= 1;
			if (gizmo instanceof MobileGizmo)
				m_mobileCount -= 1;
		}
		

		assert !contains(gizmo);
		assert checkRep();
	}
	
	/**
	 * Returns an element with the given name.
	 * 
	 * @param name the name to look for
	 * @return an element whose name matches the given name, or null if the
	 *   registry contains no element with the given name; if multiple elements
	 *   are registered with the same name, any of them may be returned
	 */
	public Gizmo findByName(String name) {
		Gizmo unique = uniques.get(name);
		if (unique != null)
			return unique;
		
		Set<Gizmo> nameDupes = dupes.get(name);
		if (nameDupes == null)
			return null;
		
		Iterator<Gizmo> iterator = nameDupes.iterator();
		return iterator.next();
	}
	
	/**
	 * Checks if the registry contains an element.
	 * 
	 * @param gizmo the element that will be searched for
	 * @return true if the registry contains the element, false otherwise
	 */
	public boolean contains(Gizmo gizmo) {
		assert gizmo != null;
		
		String name = gizmo.name();
		Gizmo unique = uniques.get(name);
		if (unique != null)
			return gizmo == unique;

		Set<Gizmo> nameDupes = dupes.get(name);
		if (nameDupes == null)
			return false;
		return nameDupes.contains(gizmo);
	}
	
	/**
	 * The number of elements in the registry.
	 * 
	 * @return the number of elements in the registry
	 */
	public int size() {
		return m_size;
	}
	
	/**
	 * Copies all the elements in the registry to an array.
	 * 
	 * The returned array is an independent copy of the data in this registry,
	 * so the registry's thread-safety restrictions do not apply.
	 * 
	 * @return a newly created array containing all the elements in the registry
	 */
	public Gizmo[] copyToArray() {
		Gizmo[] array = new Gizmo[m_size];
		
		uniques.values().toArray(array);
		int offset = uniques.size();
		for (Set<Gizmo> gizmos : dupes.values()) {
			for (Gizmo gizmo : gizmos) {
				array[offset] = gizmo;
				offset++;
			}
		}
		assert offset == array.length;
		return array;
	}

	/**
	 * Copies all the {@link MobileGizmo} elements in the registry to an array.
	 * 
	 * The returned array is an independent copy of the data in this registry,
	 * so the registry's thread-safety restrictions do not apply.
	 * 
	 * @return a newly created array containing all the {@link MobileGizmo}
	 *   elements in the registry
	 */
	public MobileGizmo[] copyMobilesToArray() {
		MobileGizmo[] array = new MobileGizmo[m_mobileCount];
		
		int offset = 0;
		for (Gizmo gizmo : uniques.values()) {
			if (gizmo instanceof MobileGizmo) {
				array[offset] = (MobileGizmo)gizmo;
				offset += 1;
			}
		}
		for (Set<Gizmo> gizmos : dupes.values()) {
			for (Gizmo gizmo : gizmos) {
				if (gizmo instanceof MobileGizmo) {
					array[offset] = (MobileGizmo)gizmo;
					offset++;
				}
			}
		}
		assert offset == array.length;
		return array;
	}
	
	/**
	 * Copies {@link SolidGizmo}s that are not {@link MobileGizmo}s to an array.
	 * 
	 * The returned array is an independent copy of the data in this registry,
	 * so the registry's thread-safety restrictions do not apply.
	 * 
	 * @return a newly created array containing all the {@link SolidGizmo}
	 *   elements in the registry that are not {@link MobileGizmo}s
	 */
	public SolidGizmo[] copyImmobilesToArray() {
		SolidGizmo[] array = new SolidGizmo[m_solidCount - m_mobileCount];
		
		int offset = 0;
		for (Gizmo gizmo : uniques.values()) {
			if (gizmo instanceof SolidGizmo) {
				if (!(gizmo instanceof MobileGizmo)) {
					array[offset] = (SolidGizmo)gizmo;
					offset += 1;
				}
			}
		}
		for (Set<Gizmo> gizmos : dupes.values()) {
			for (Gizmo gizmo : gizmos) {
				if (gizmo instanceof SolidGizmo) {
					if (!(gizmo instanceof MobileGizmo)) {
						array[offset] = (SolidGizmo)gizmo;
						offset += 1;
					}
				}
			}
		}
		assert offset == array.length;
		return array;
	}	
	
	@Override
	public Iterator<Gizmo> iterator() {
		return new RegistryIterator();
	}
	
	/** The iterators returned by {@link GizmoRegistry#iterator()}. */
	private class RegistryIterator implements Iterator<Gizmo> {
		/**
		 * Points to the next element in uniques.
		 * 
		 * This becomes null when uniques is exhausted.
		 */
		private Iterator<Gizmo> uniquesIterator;
		/**
		 * Points to the next set of elements in dupes.
		 * 
		 * This becomes null when dupes is exhausted.
		 */
		private Iterator<Set<Gizmo>> dupesIterator;
		/**
		 * Points to the next element in the current set of elements in dupes.
		 * 
		 * This becomes null when dupes is exhausted.
		 */
		private Iterator<Gizmo> dupeSetIterator;
		
		public RegistryIterator() {
			if (uniques.isEmpty()) {
				uniquesIterator = null;
			} else {
				uniquesIterator = uniques.values().iterator();
				assert uniquesIterator.hasNext();
			}
			
			if (dupes.isEmpty()) {
				dupesIterator = null;
			} else {
				dupesIterator = dupes.values().iterator();
				assert dupesIterator.hasNext();
				dupeSetIterator = dupesIterator.next().iterator();
				// NOTE: each set in dupes has at least 2 elements
				assert dupeSetIterator.hasNext();
				if (!dupesIterator.hasNext())
					dupesIterator = null;
			}
			
			assert checkRep();
		}

		@Override
		public boolean hasNext() {
			return uniquesIterator != null || dupeSetIterator != null;
		}

		@Override
		public Gizmo next() {
			Gizmo nextGizmo;
			if (uniquesIterator != null) {
				nextGizmo = uniquesIterator.next();
				if (!uniquesIterator.hasNext())
					uniquesIterator = null;
			} else {
				if (dupeSetIterator != null) {
					nextGizmo = dupeSetIterator.next();
					if (!dupeSetIterator.hasNext()) {
						if (dupesIterator == null) {
							dupeSetIterator = null;
						} else {
							dupeSetIterator = dupesIterator.next().iterator();
							// NOTE: each set in dupes has at least 2 elements
							assert dupeSetIterator.hasNext();
							if (!dupesIterator.hasNext())
								dupesIterator = null;
						}
					}
				} else {
					throw new NoSuchElementException();
				}
			}
			
			assert checkRep();
			return nextGizmo;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"GizmoRegistry's iterator does not support remove()");
		}
		
		/**
		 * Checks this instance's representation invariant.
		 * 
		 * @return true if this instance's representation invariant holds
		 */
		private boolean checkRep() {
			if (uniquesIterator != null) {
				if (!uniquesIterator.hasNext())
					return false;
			}
			if (dupeSetIterator != null) {
				if (!dupeSetIterator.hasNext())
					return false;
			} else {
				if (dupesIterator != null)
					return false;
			}
			if (dupesIterator != null) {
				if (!dupesIterator.hasNext())
					return false;
			}
			return true;
		}
	}
	
	/**
	 * Checks this instance's representation invariant.
	 * 
	 * @return true if this instance's representation invariant holds
	 */
	private boolean checkRep() {
		if (uniques == null)
			return false;
		if (dupes == null)
			return false;
		
		int actualSize = 0;
		int actualSolids = 0;
		int actualMobiles = 0;
		for(Entry<String, Gizmo> entry : uniques.entrySet()) {
			String name = entry.getKey();
			if (name == null)
				return false;
			if (dupes.containsKey(name))
				return false;
			Gizmo gizmo = entry.getValue();
			if (!name.equals(gizmo.name()))
				return false;
			actualSize += 1;
			if (gizmo instanceof SolidGizmo)
				actualSolids += 1;
			if (gizmo instanceof MobileGizmo)
				actualMobiles += 1;
		}

		for (Entry<String, Set<Gizmo>> entry : dupes.entrySet()) {
			String name = entry.getKey();
			if (name == null)
				return false;
			if (uniques.containsKey(name))
				return false;
			Set<Gizmo> nameDupes = entry.getValue();
			if (nameDupes.size() < 2)
				return false;
			for (Gizmo gizmo : nameDupes) {
				if (!name.equals(gizmo.name()))
					return false;
				actualSize += 1;
				if (gizmo instanceof SolidGizmo)
					actualSolids += 1;
				if (gizmo instanceof MobileGizmo)
					actualMobiles += 1;
			}
		}
		if (m_size != actualSize)
			return false;
		if (m_solidCount != actualSolids)
			return false;
		if (m_mobileCount != actualMobiles)
			return false;
		
		return true;
	}
}
