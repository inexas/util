package com.inexas.util;

/**
 * This is a utility class that just holds a couple of objects
 * 
 * @author <a href=mailto:keith@whittingham.com>Keith Whittingham</a>
 * @copyright Keith Whittingham 2000.., All rights reservered.
 */
public class Pair<T1, T2> {
	private int hashCode;
	public final T1 object1;
	public final T2 object2;

	/**
	 * Creates a new Pair
	 * 
	 * @param object1
	 *            first object
	 * @param object2
	 *            second object
	 */
	public Pair(T1 object1, T2 object2) {
		this.object1 = object1;
		this.object2 = object2;
	}

	@Override
	public int hashCode() {
		if(hashCode == 0) {
			if(object1 == null) {
				hashCode = object2 == null ? super.hashCode() : object2.hashCode();
			} else {
				hashCode = object2 == null ?
						object1.hashCode() : object1.hashCode() + object2.hashCode();
			}
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object rhsObject) {
		// try for a cheap true...
		if(this == rhsObject) {
			return true;
		}
		// try for a cheap false...
		if(rhsObject == null) {
			return false;
		}
		try {
			// check we have the same types...
			@SuppressWarnings("unchecked")
			// todo work out how to get rid of this
			final Pair<T1, T2> rhs = (Pair<T1, T2>)rhsObject;
			// both lhs and rhs are the same types, check for an exact match...
			return equal(object1, rhs.object1) && equal(object2, rhs.object2);
		} catch(ClassCastException e) {
			// not the same types: false...
			return false;
		}
	}

	private boolean equal(Object lhs, Object rhs) {
		return lhs == null ? rhs == null : lhs.equals(rhs);
	}

	@Override
	public String toString() {
		return "Pair(" +
				(object1 == null ? "<null>" : object1.toString())
				+ ", "
				+ (object2 == null ? "<null>" : object2.toString() + ")");
	}
}
