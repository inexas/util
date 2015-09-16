package com.inexas.util;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.*;
import com.inexas.exception.ShouldNotBeCalledException;

public class FileU {
	public static enum Type {
		FILE,
		DIRECTORY,
		BOTH;
	}

	public final static String ROOT = getCurrentWorkingDirectory() + '/';
	public final static String DATA = ROOT + "data/";
	public final static String DATATEST = ROOT + "datatest/";

	public static char[] readToCharacterArray(File file) {
		final char[] result;

		try(final FileReader reader = new FileReader(file)) {
			final int length = (int)file.length();
			final char[] ca = new char[length];
			final int read = reader.read(ca);
			if(read < length) {
				// File contained unicode characters mapping two bytes to 1
				// character
				result = new char[read];
				System.arraycopy(ca, 0, result, 0, read);
			} else {
				result = ca;
			}
		} catch(final Exception e) {
			throw new RuntimeException("Error reading file: " +
					file.getAbsolutePath(), e);
		}

		return result;
	}

	public static String read(File file, boolean trim) {
		final StringBuilder result = new StringBuilder();

		final List<String> list = readIntoStringList(file, trim);
		final String ls = System.getProperty("line.separator");
		for(final String line : list) {
			result.append(line);
			result.append(ls);
		}

		return result.toString();
	}

	public static List<String> readIntoStringList(File file, boolean trim) {
		final List<String> result = new ArrayList<>();

		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				result.add(trim ? line.trim() : line);
			}

		} catch(final Exception e) {
			throw new RuntimeException(
					"Error reading file: " +
							file.getAbsolutePath(), e);
		}
		return result;
	}

	/**
	 * @param subPath
	 *            A relative sub-path, e.g my/data.
	 * @return A File generated from the user.dir plus a given relative
	 *         sub-path.
	 */
	public static File getHome(String subPath) {
		assert !subPath.startsWith("/") : "Should be relative path: " + subPath;
		final String path = System.getProperty("user.dir") + '/' + subPath;
		return new File(path);
	}

	public static File[] getChildren(File directory, Type type, String regex) {
		assert directory.isDirectory() : "Not a directory: " + directory.getPath();

		final List<File> result = new ArrayList<>();

		final File[] list = directory.listFiles();
		for(final File child : list) {
			if(child.getName().matches(regex)) {
				switch(type) {
				case BOTH:
					result.add(child);
					break;

				case DIRECTORY:
					if(child.isDirectory()) {
						result.add(child);
					}
					break;

				case FILE:
					if(child.isFile()) {
						result.add(child);
					}
					break;

				default:
					throw new ShouldNotBeCalledException();
				}
			}
		}

		return result.toArray(new File[result.size()]);
	}

	public static InputStream openResourceStream(String path) {
		final InputStream result = FileU.class.getResourceAsStream(path);
		return result;
	}

	/**
	 * Search classpath for entries matching a given pattern
	 *
	 * @param regexp
	 *            a regular expression to match, e.g. ".*\\.properties" or
	 *            "/com/inexas/util/.*\\properties"
	 * @param allClasspath
	 *            true search all classpath, false search only the project (the
	 *            first entry in the class path)
	 * @return a collection of Strings that can be used with
	 *         getResourceAsStream()
	 */

	public static Collection<String> getResources(String regexp, boolean allClasspath) {
		final ArrayList<String> result = new ArrayList<>();

		Pattern pattern;
		try {
			pattern = Pattern.compile(regexp);
		} catch(final Exception e) {
			throw new RuntimeException("Error parsing pattern", e);
		}
		final String classPath = System.getProperty("java.class.path", ".");
		final String[] classPathElements = classPath.split(":");
		if(allClasspath) {
			for(final String element : classPathElements) {
				result.addAll(getResources(element, pattern));
			}
		} else {
			result.addAll(getResources(classPathElements[0], pattern));
		}

		return result;
	}

	private static Collection<String> getResources(String element, Pattern pattern) {
		final ArrayList<String> result = new ArrayList<>();

		final File file = new File(element);
		if(file.isDirectory()) {
			result.addAll(getResourcesFromDirectory(element.length(), file, pattern));
		} else {
			result.addAll(getResourcesFromJarFile(file, pattern));
		}
		return result;
	}

	private static Collection<String> getResourcesFromJarFile(File file, Pattern pattern) {
		final ArrayList<String> result = new ArrayList<>();

		try(final ZipFile zipFile = new ZipFile(file)) {
			@SuppressWarnings("unchecked")
			final Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>)zipFile.entries();
			while(entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();
				final String fileName = entry.getName();
				if(pattern.matcher(fileName).matches()) {
					result.add(fileName);
				}
			}
		} catch(final Exception e) {
			throw new RuntimeException("Error reading JAR file", e);
		}

		return result;
	}

	private static Collection<String> getResourcesFromDirectory(
			int rootLength,
			File directory,
			Pattern pattern) {
		final ArrayList<String> result = new ArrayList<>();

		final File[] fileList = directory.listFiles();
		for(final File file : fileList) {
			if(file.isDirectory()) {
				result.addAll(getResourcesFromDirectory(rootLength, file, pattern));
			} else {
				try {
					final String fileName = file.getCanonicalPath().substring(rootLength);
					if(pattern.matcher(fileName).matches()) {
						result.add(fileName);
					}
				} catch(final IOException e) {
					throw new RuntimeException("Error reading directory", e);
				}
			}
		}
		return result;
	}

	private static String getCurrentWorkingDirectory() {
		try {
			final File currentWorkingDirectory = new File(".");
			return currentWorkingDirectory.getCanonicalPath();
		} catch(final IOException e) {
			throw new RuntimeException("Error getting current directory", e);
		}
	}

}
