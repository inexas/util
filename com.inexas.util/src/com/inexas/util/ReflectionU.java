package com.inexas.util;

import java.lang.reflect.Method;
import java.util.*;

public class ReflectionU {
	private static class Candidate implements Comparable<Candidate> {
		private final Method method;
		private final int weight;

		public Candidate(Method method, int weight) {
			this.method = method;
			this.weight = weight;
		}

		@Override
		public int compareTo(Candidate rhs) {
			return weight - rhs.weight;
		}
	}

	/**
	 * Create a String version of a method name. This is useful for messages in
	 * thrown exceptions for example.
	 *
	 * @param clazz
	 *            The class in which the method should appear.
	 * @param methodName
	 *            The name of the method in question.
	 * @param parameterTypes
	 *            The list of parameter types.
	 * @return The fully qualified name of the method.
	 */
	public static String toMethodName(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		final TextBuilder sb = new TextBuilder(clazz.getCanonicalName());

		sb.append('.');
		sb.append(methodName);
		sb.append('(');
		if(parameterTypes != null) {
			boolean delimit = false;
			for(final Class<?> parameterType : parameterTypes) {
				if(delimit) {
					sb.append(", ");
				} else {
					delimit = true;
				}

				final String type;
				final String fullName = parameterType.getCanonicalName();
				final String simpleName = parameterType.getSimpleName();
				// "java.lang." = 10 characters...
				if(fullName.startsWith("java.lang.") && simpleName.length() + 10 == fullName.length()) {
					type = simpleName;
				} else {
					type = fullName;
				}
				sb.append(type);
			}
		}
		sb.append(')');

		return sb.toString();
	}

	/**
	 * This method improves upon the Class.getMethod sibling by being more
	 * flexible with the types. If you look for myMethod(Long) it will find
	 * myMethod(long) for example.
	 *
	 * The key differences are:
	 * <ul>
	 * <li>Primitive data types and their object equivalents will be both
	 * considered OK. The matching data type is preferred</li>
	 * <li>isAssignable used to check for object equivalence: HashMap will match
	 * Map</li>
	 * <li>Non-public methods are also returned</li>
	 * </ul>
	 * A weighting system is used to return the best match. This is not an exact
	 * science.
	 *
	 * What's not handled (yet)
	 * <ul>
	 * <li>Type conversion: int -&gt; long</li>
	 * <li>Constructors</li>
	 * <li>Arrays</li>
	 * <li>Collection classes</li>
	 * </ul>
	 *
	 * @param clazz
	 *            the class in which to search for the method
	 * @param methodName
	 *            a String specifying the simple name of the desired method
	 * @param parameterTypes
	 *            an array of Class objects that identify the method's formal
	 *            parameter types, in declared order. If parameterTypes is null,
	 *            it is treated as if it were an empty array.
	 * @return The retrieved Method.
	 * @throws NoSuchMethodException
	 *             if a matching method is not found or if the name is
	 *             "&lt;init&gt;"or "&lt;clinit&gt;".
	 * @throws SecurityException
	 *             the caller's class loader is not the same as or an ancestor
	 *             of the class loader for the current class and invocation of
	 *             s.checkPackageAccess() denies access to the package of this
	 *             class
	 * @throws NullPointerException
	 *             clazz or methodName is null
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
			throws NoSuchMethodException, SecurityException, NullPointerException {
		final List<Candidate> result;

		if(clazz == null) {
			throw new NullPointerException("The parameter clazz is null");
		}
		if(methodName == null) {
			throw new NullPointerException("The parameter methodName is null");
		}

		if("<init>".equals(methodName) || "<clinit>".equals(methodName)) {
			throw new NoSuchMethodException("Constructors not yet supported");
		}

		result = new ArrayList<>();
		search(clazz, methodName, parameterTypes, 0, result);

		if(result.isEmpty()) {
			throw new NoSuchMethodException(toMethodName(clazz, methodName, parameterTypes) + " not found");
		}
		Collections.sort(result);

		return result.get(0).method;
	}

	private final static Map<Class<?>, Class<?>> primitiveToObjectMap = new HashMap<>();
	static {
		primitiveToObjectMap.put(boolean.class, Boolean.class);
		primitiveToObjectMap.put(char.class, Character.class);
		primitiveToObjectMap.put(byte.class, Byte.class);
		primitiveToObjectMap.put(short.class, Short.class);
		primitiveToObjectMap.put(int.class, Integer.class);
		primitiveToObjectMap.put(long.class, Long.class);
		primitiveToObjectMap.put(float.class, Float.class);
		primitiveToObjectMap.put(double.class, Double.class);
	}

	/**
	 * Recursively search for a method
	 */
	private static void search(
			Class<?> clazz,
			String methodName,
			Class<?>[] preferredParameterTypes,
			int currentWeight,
			List<Candidate> result) {
		boolean exactMatch = false;
		for(final Method method : clazz.getDeclaredMethods()) {

			// Method name must match...
			if(method.getName().equals(methodName)) {

				// Parameter count must match...
				final Class<?>[] actualParameterTypes = method.getParameterTypes();
				final int count = preferredParameterTypes == null ? 0 : preferredParameterTypes.length;
				if(actualParameterTypes.length == count) {

					// Each parameter must match...
					int totalWeight = currentWeight;
					for(int i = 0; i < count; i++) {
						final Class<?> actualType = actualParameterTypes[i];
						@SuppressWarnings("null")
						final Class<?> preferredType = preferredParameterTypes[i];

						final int weight;
						if(actualType == preferredType) {
							weight = 0;
						} else if(actualType.isPrimitive()) {
							if(primitiveToObjectMap.get(actualType) == preferredType) {
								// Primitive and matching object type
								weight = 10;
							} else {
								// Only one type is primitive
								weight = -1;
							}
						} else if(preferredType.isPrimitive()) {
							if(primitiveToObjectMap.get(preferredType) == actualType) {
								// Primitive and matching object type
								weight = 10;
							} else {
								// Only one type is primitive
								weight = -1;
							}
						} else {
							// Assignable object type?
							weight = actualType.isAssignableFrom(preferredType) ? 10 : -1;
						}

						if(weight < 0) {
							totalWeight = -1;
							break;
						}
						totalWeight += weight;
					}

					if(totalWeight == currentWeight) {
						exactMatch = true;
						result.clear();
						final Candidate candidate = new Candidate(method, totalWeight);
						result.add(candidate);
						break;
					}
					if(totalWeight > 0) {
						final Candidate candidate = new Candidate(method, totalWeight);
						result.add(candidate);
					}
				}
			}
		}

		if(!exactMatch) {
			final Class<?> superClass = clazz.getSuperclass();
			if(superClass != null) {
				search(superClass, methodName, preferredParameterTypes, currentWeight + 1, result);
			}
		}
	}

	public static class ReflectException extends Exception {
		private static final long serialVersionUID = -1819281382298181464L;

		public ReflectException(String message) {
			super(message);
		}

		public ReflectException(String message, Exception e) {
			super(message, e);
		}
	}

	public static <T> Class<T> getClass(String className, Class<?> type) throws ReflectException {
		try {
			if(className == null || className.trim().length() == 0) {
				throw new ReflectException("Missing class name");
			}

			@SuppressWarnings("unchecked")
			final Class<T> result = (Class<T>)Class.forName(className);
			if(!type.isAssignableFrom(result)) {
				throw new ReflectException("Class found but not of right type: " + className
						+ "/" + type.getName());
			}
			return result;

		} catch(final ClassNotFoundException e) {
			throw new ReflectException("Class not found: " + className, e);
		}
	}

	/**
	 * A convenience method to invoke a method give the object, method name and
	 * a list of parameters.
	 *
	 * Note that this won't work with null parameters.
	 *
	 * @param <T>
	 *            The type of the object returned by the invocation.
	 * @param object
	 *            The object on which to call the method.
	 * @param methodName
	 *            The name of the method to call.
	 * @param parameters
	 *            The parameters to pass.
	 * @return The value return by the invocation.
	 */
	public static <T> T invoke(Object object, String methodName, Object... parameters) {
		// Get the parameter types...
		final int count = parameters.length;
		final Class<?>[] parameterTypes = new Class<?>[count];
		for(int i = 0; i < count; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}

		// Get the method...
		try {
			final Class<?> clazz = object.getClass();
			final Method method = clazz.getMethod(methodName, parameterTypes);
			@SuppressWarnings("unchecked")
			final T result = (T)method.invoke(object, parameters);
			return result;
		} catch(final Exception e) {
			throw new RuntimeException("Error invoking method", e);
		}
	}
}
