package com.inexas.util;

import static com.inexas.util.ReflectionU.*;
import static org.junit.Assert.assertEquals;
import java.lang.reflect.Method;
import org.junit.Test;
import com.inexas.exception.InexasRuntimeException;

// todo Return values
public class TestReflectionU {
	public static class C {
		int i;
		Integer j;
		C k;

		@SuppressWarnings({ "hiding" })
		public void test(int i, Integer j, C k) {
			this.i = i;
			this.j = j;
			this.k = k;
		}

		@SuppressWarnings({ "unused", "hiding" })
		public void test(int i, int j, C k) {
			//
		}

		@SuppressWarnings({ "unused", "hiding" })
		public void test(C k) {
			//
		}

		@SuppressWarnings({ "unused", "hiding" })
		public void test(int i, C k) {
			//
		}

		@SuppressWarnings({ "unused", "hiding" })
		public void test(int i, SubC k) {
			//
		}
	}

	public static class SubC extends C {

		@Override
		@SuppressWarnings({ "hiding" })
		public void test(int i, int j, C k) {
			//
		}

		@SuppressWarnings({ "hiding", "unused" })
		public void test(int i, int j, SubC k) {
			//
		}
	}

	private final static Method intInteger, intInt, subIntInt, cAlone, intC, intSubC;
	static {
		try {
			intInt = C.class.getDeclaredMethod("test", int.class, int.class, C.class);
			intInteger = C.class.getDeclaredMethod("test", int.class, Integer.class, C.class);
			subIntInt = SubC.class.getDeclaredMethod("test", int.class, int.class, C.class);
			cAlone = C.class.getDeclaredMethod("test", C.class);
			intC = C.class.getDeclaredMethod("test", int.class, C.class);
			intSubC = C.class.getDeclaredMethod("test", int.class, SubC.class);
		} catch(final Exception e) {
			throw new InexasRuntimeException("Error setting up tests", e);
		}
	}

	@Test
	public void testToMethodName() {
		assertEquals("com.inexas.util.TestReflectionU.C.test("
				+ "int, "
				+ "Integer, "
				+ "com.inexas.util.TestReflectionU.C)",
				toMethodName(C.class, "test", int.class, Integer.class, C.class));
	}

	@Test(expected = NoSuchMethodException.class)
	public void testIncorrectNumberOfParameters() throws Exception {
		ReflectionU.getMethod(C.class, "test", int.class, Integer.class);
	}

	@Test(expected = NoSuchMethodException.class)
	public void testIncorrectParameters() throws Exception {
		ReflectionU.getMethod(C.class, "test", int.class, Integer.class, Integer.class);
	}

	@Test(expected = NoSuchMethodException.class)
	public void testInvalidMethodName() throws Exception {
		ReflectionU.getMethod(C.class, "noneSuch", int.class, Integer.class, C.class);
	}

	@Test
	public void testGetMethod1() throws Exception {
		assertEquals(
				intInteger,
				getMethod(C.class, "test", Integer.class, Integer.class, C.class));
		assertEquals(
				intInt,
				getMethod(C.class, "test", int.class, int.class, C.class));
		assertEquals(
				intInteger,
				getMethod(C.class, "test", int.class, Integer.class, C.class));
	}

	@Test
	public void testGetMethod2() throws Exception {
		assertEquals(
				subIntInt,
				getMethod(SubC.class, "test", int.class, int.class, C.class));
		assertEquals(
				intInteger,
				getMethod(SubC.class, "test", int.class, Integer.class, C.class));
		assertEquals(
				subIntInt,
				getMethod(SubC.class, "test", Integer.class, int.class, C.class));
	}

	@Test
	public void testGetMethod3() throws Exception {
		assertEquals(
				cAlone,
				getMethod(SubC.class, "test", SubC.class));
		assertEquals(
				cAlone,
				getMethod(SubC.class, "test", C.class));
		assertEquals(
				intC,
				getMethod(SubC.class, "test", int.class, C.class));
		assertEquals(
				intSubC,
				getMethod(SubC.class, "test", int.class, SubC.class));
	}

	@Test
	public void testCallMethod() throws Exception {
		final C c = new C();
		intInteger.invoke(c, new Integer(1), new Integer(2), c);
		assertEquals(1, c.i);
		assertEquals(new Integer(2), c.j);
		assertEquals(c, c.k);

		final SubC subC = new SubC();
		intInteger.invoke(subC, new Integer(3), new Integer(4), subC);
		assertEquals(subC, subC.k);
		assertEquals(3, subC.i);
		assertEquals(new Integer(4), subC.j);
		assertEquals(subC, subC.k);
	}

	@Test(expected = NoSuchMethodException.class)
	public void testFail1() throws Exception {
		C.class.getMethod("test", Integer.class, int.class, C.class);
	}

	@Test(expected = NoSuchMethodException.class)
	public void testFail2() throws Exception {
		C.class.getMethod("test", int.class, Integer.class, SubC.class);
	}
}
