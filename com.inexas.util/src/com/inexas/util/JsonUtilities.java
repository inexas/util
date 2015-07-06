package com.inexas.util;

import java.io.File;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class JsonUtilities {
	private final JSONObject json;
	private final String source;

	public JsonUtilities(File file) throws JsonException {
		source = file.getAbsolutePath();
		try {
			final JSONParser parser = new JSONParser();
			final String jsonText = FileU.read(file, false);
			json = (JSONObject)parser.parse(jsonText);
		} catch(final ParseException e) {
			throw new JsonException(source + ": " + "Error parsing JSON", e);
		}
	}

	public JsonUtilities(JSONObject json, String source) {
		this.json = json;
		this.source = source;
	}

	public String getString(String key) throws JsonException {
		final Object object = json.get(key);
		if(object == null) {
			throw new JsonException(
					source + ": " +
							"Error parsing JSON, missing key: " + source + '/' + key);
		} else if(object.getClass() != String.class) {
			throw new JsonException(
					source + ": " +
							"Error parsing JSON, invalid type, expected number: " + source + '/' + key);
		}
		return (String)object;
	}

	public String getString(String key, String defaultValue) throws JsonException {
		final String result;
		final Object object = json.get(key);
		if(object == null) {
			result = defaultValue;
		} else if(object.getClass() != String.class) {
			throw new JsonException(
					source + ": " +
							"Error parsing JSON, invalid type, expected string: " + source + '/' + key);
		} else {
			result = (String)object;
		}
		return result;
	}

	public int getInt(String key) throws JsonException {
		final Object object = json.get(key);
		if(object == null) {
			throw new JsonException(
					source + ": " +
							"Error parsing JSON, missing key: " + source + '/' + key);
		} else if(object.getClass() != Long.class) {
			throw new JsonException(
					source + ": " +
							"Error parsing JSON, invalid type, expected number: " + source + '/' + key);
		}
		return ((Long)object).intValue();
	}

	public int getInt(String key, int defaultValue) throws JsonException {
		final int result;

		final Object object = json.get(key);
		if(object == null) {
			result = defaultValue;
		} else if(object.getClass() != Long.class) {
			throw new JsonException(
					source + ": " +
							"Error parsing JSON, invalid type, expected number: " + source + '/' + key);
		} else {
			result = ((Long)object).intValue();
		}
		return result;
	}

	public Object getObject(String key) throws JsonException {
		final Object result = json.get(key);
		if(result == null) {
			throw new JsonException(
					source + ": " +
							"Error parsing JSON, missing key: " + source + '/' + key);
		}
		return result;
	}

	public static interface Visitor<T> {
		T visit(JsonUtilities jsonSource, T parent) throws JsonException;
	}

	/**
	 * Visitor pattern. Given a key visit all the children in a JSON file under
	 * that key. for example given the JSON
	 * "{ "children" [ { id: 1 }, { id: 2 } ] }" and the key "children" the
	 * visitor will be called twice
	 *
	 * @param key
	 *            the key of the child array
	 * @param visitor
	 *            the visitor to call
	 * @throws JsonException
	 *             thrown on any JSON syntax errors
	 * @returns the root element
	 */
	public <T> T visit(String key, Visitor<T> visitor) throws JsonException {
		return visit(key, (T)null, visitor);
	}

	// todo This idea doesn't really work. Move it to the classes as ctors
	private <T> T visit(String key, T parent, Visitor<T> visitor) throws JsonException {
		// Create the object...
		final T result = visitor.visit(this, parent);

		// Visit any children...
		final JSONArray array = (JSONArray)json.get(key);
		if(array != null) {
			@SuppressWarnings("unchecked")
			final Iterator<JSONObject> iterator = array.iterator();
			while(iterator.hasNext()) {
				final JsonUtilities childJson = new JsonUtilities(iterator.next(), source);
				childJson.visit(key, result, visitor);
			}
		}

		return result;
	}

	public List<JsonUtilities> getChildren(String key) {
		final List<JsonUtilities> result = new ArrayList<>();

		final JSONArray array = (JSONArray)json.get(key);
		if(array != null) {
			@SuppressWarnings("unchecked")
			final Iterator<JSONObject> iterator = array.iterator();
			while(iterator.hasNext()) {
				final JsonUtilities childJson = new JsonUtilities(iterator.next(), source);
				result.add(childJson);
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return json.toString();
	}

}
