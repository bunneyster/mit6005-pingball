package pb.board;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import pb.testing.NullGizmo;

public class GizmoRegistryTest {
	private GizmoRegistry registry;
	private NullGizmo foo1, foo2, foo3, bar, baz1, baz2, baz3, meh;
	
	@Before
	public void setUp() throws Exception {
		registry = new GizmoRegistry();		
		foo1 = new NullGizmo("foo");
		foo2 = new NullGizmo("foo");
		foo3 = new NullGizmo("foo");
		bar = new NullGizmo("bar");
		baz1 = new NullGizmo("baz");
		baz2 = new NullGizmo("baz");
		baz3 = new NullGizmo("baz");
		meh = new NullGizmo("meh");
	}

	@Test
	public void testEmpty() {
		assertEquals(0, registry.size());
		assertEquals(null, registry.findByName("foo"));
		assertEquals(null, registry.findByName("bar"));
		assertEquals(false, registry.contains(foo1));
		assertEquals(false, registry.contains(foo2));
		assertEquals(false, registry.contains(foo3));
		assertEquals(false, registry.contains(bar));
		
		Gizmo[] gizmos = new Gizmo[] { };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());
	}

	@Test
	public void testAddOneFoo() {
		registry.add(foo1);
		
		assertEquals(1, registry.size());
		assertEquals(foo1, registry.findByName("foo"));
		assertEquals(null, registry.findByName("bar"));
		assertEquals(true, registry.contains(foo1));
		assertEquals(false, registry.contains(foo2));
		assertEquals(false, registry.contains(foo3));
		assertEquals(false, registry.contains(bar));

		Gizmo[] gizmos = new Gizmo[] { foo1 };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());
	}

	@Test
	public void testAddTwoFoos() {
		registry.add(foo1);
		registry.add(foo2);
		
		assertEquals(2, registry.size());
		assertEquals("foo", registry.findByName("foo").name());
		assertEquals(null, registry.findByName("bar"));
		assertEquals(true, registry.contains(foo1));
		assertEquals(true, registry.contains(foo2));
		assertEquals(false, registry.contains(foo3));
		assertEquals(false, registry.contains(bar));

		Gizmo[] gizmos = new Gizmo[] { foo1, foo2 };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());
	}

	@Test
	public void testAddThreeFoos() {
		registry.add(foo1);
		registry.add(foo2);
		registry.add(foo3);
		
		assertEquals(3, registry.size());
		assertEquals("foo", registry.findByName("foo").name());
		assertEquals(null, registry.findByName("bar"));
		assertEquals(true, registry.contains(foo1));
		assertEquals(true, registry.contains(foo2));
		assertEquals(true, registry.contains(foo3));
		assertEquals(false, registry.contains(bar));
		
		Gizmo[] gizmos = new Gizmo[] { foo1, foo2, foo3 };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());
	}

	@Test
	public void testAddThreeFoosAndOneBar() {
		registry.add(foo1);
		registry.add(foo2);
		registry.add(foo3);
		registry.add(bar);
		
		assertEquals(4, registry.size());
		assertEquals("foo", registry.findByName("foo").name());
		assertEquals(bar, registry.findByName("bar"));
		assertEquals(true, registry.contains(foo1));
		assertEquals(true, registry.contains(foo2));
		assertEquals(true, registry.contains(foo3));
		assertEquals(true, registry.contains(bar));
		
		Gizmo[] gizmos = new Gizmo[] { foo1, foo2, foo3, bar };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());		
	}
	
	@Test
	public void testAddOneFooAndRemoveIt() {
		registry.add(foo1);
		registry.remove(foo1);
		
		assertEquals(0, registry.size());
		assertEquals(null, registry.findByName("foo"));
		assertEquals(null, registry.findByName("bar"));
		assertEquals(false, registry.contains(foo1));
		assertEquals(false, registry.contains(foo2));
		assertEquals(false, registry.contains(foo3));
		assertEquals(false, registry.contains(bar));
		
		Gizmo[] gizmos = new Gizmo[] { };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());
	}	
	
	@Test
	public void testAddTwoFoosAndRemoveOne() {
		registry.add(foo1);
		registry.add(foo2);
		registry.remove(foo1);
		
		assertEquals(1, registry.size());
		assertEquals(foo2, registry.findByName("foo"));
		assertEquals(null, registry.findByName("bar"));
		assertEquals(false, registry.contains(foo1));
		assertEquals(true, registry.contains(foo2));
		assertEquals(false, registry.contains(foo3));
		assertEquals(false, registry.contains(bar));
		
		Gizmo[] gizmos = new Gizmo[] { foo2 };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());
	}

	@Test
	public void testAddThreeFoosAndRemoveOne() {
		registry.add(foo1);
		registry.add(foo2);
		registry.add(foo3);
		registry.remove(foo1);
		
		assertEquals(2, registry.size());
		assertEquals("foo", registry.findByName("foo").name());
		assertEquals(null, registry.findByName("bar"));
		assertEquals(false, registry.contains(foo1));
		assertEquals(true, registry.contains(foo2));
		assertEquals(true, registry.contains(foo3));
		assertEquals(false, registry.contains(bar));
		
		Gizmo[] gizmos = new Gizmo[] { foo2, foo3 };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());
	}
	
	@Test
	public void testLargeSet() {
		registry.add(foo1);
		registry.add(baz1);
		registry.add(foo2);
		registry.add(baz2);
		registry.add(foo3);
		registry.add(baz3);
		registry.add(bar);
		registry.add(meh);
		
		assertEquals(8, registry.size());
		assertEquals("foo", registry.findByName("foo").name());
		assertEquals(bar, registry.findByName("bar"));
		assertEquals("baz", registry.findByName("baz").name());
		assertEquals(meh, registry.findByName("meh"));
		assertEquals(true, registry.contains(foo1));
		assertEquals(true, registry.contains(foo2));
		assertEquals(true, registry.contains(foo3));
		assertEquals(true, registry.contains(bar));
		assertEquals(true, registry.contains(baz1));
		assertEquals(true, registry.contains(baz2));
		assertEquals(true, registry.contains(baz3));
		assertEquals(true, registry.contains(meh));
		
		Gizmo[] gizmos =
				new Gizmo[] { foo1, foo2, foo3, bar, baz1, baz2, baz3, meh };
		assertArraysHaveSameElements(gizmos, registry.copyToArray());
		assertIteratorGivesElements(gizmos, registry.iterator());		
	}
	
	private static <T> void assertIteratorGivesElements(T[] expected,
			Iterator<T> actual) {
		ArrayList<T> list = new ArrayList<T>();
		while (actual.hasNext())
			list.add(actual.next());
		
		try {
			actual.next();
			fail("#next() does not throw NoSuchElementException()");
		} catch(NoSuchElementException e) {
			// The test passed.
		}
		HashSet<T> actualSet = new HashSet<T>(list);
		
		// Make sure the iterator did not yield the same element twice.
		assertEquals(actualSet.size(), list.size());
		
		HashSet<T> expectedSet = new HashSet<T>(Arrays.asList(expected));
		assertEquals(expectedSet, actualSet);
	}

	private static <T> void assertArraysHaveSameElements(T[] expected,
			T[] actual) {
		HashSet<T> expectedSet = new HashSet<T>(Arrays.asList(expected));
		HashSet<T> actualSet = new HashSet<T>(Arrays.asList(actual));
		assertEquals(expectedSet, actualSet);
	}
}
