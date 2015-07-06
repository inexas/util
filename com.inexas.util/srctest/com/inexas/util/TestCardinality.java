package com.inexas.util;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestCardinality {

	@Test
	public void test() {
		assertTrue(Cardinality.ZERO == Cardinality.newInstance("0..0"));
		assertTrue(Cardinality.ZERO == Cardinality.newInstance(0, 0));

		assertTrue(Cardinality.ZERO_ONE == Cardinality.newInstance("0..1"));
		assertTrue(Cardinality.ZERO_ONE == Cardinality.newInstance(0, 1));

		assertTrue(Cardinality.ZERO_MANY == Cardinality.newInstance("*"));
		assertTrue(Cardinality.ZERO_MANY == Cardinality.newInstance("0..*"));
		assertTrue(Cardinality.ZERO_MANY == Cardinality.newInstance(0, Integer.MAX_VALUE));

		assertTrue(Cardinality.ONE_ONE == Cardinality.newInstance("1..1"));
		assertTrue(Cardinality.ONE_ONE == Cardinality.newInstance(1, 1));

		assertTrue(Cardinality.ONE_MANY == Cardinality.newInstance("1..*"));
		assertTrue(Cardinality.ONE_MANY == Cardinality.newInstance(1, Integer.MAX_VALUE));

		final Cardinality twoToFive = Cardinality.newInstance(2, 5);
		assertTrue(twoToFive.equals(Cardinality.newInstance("2..5")));
	}

}
