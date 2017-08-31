package org.metagarfus.webdispatcher.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PropertyFile {
	private Map<String, String> properties;
	private boolean valid;

	public PropertyFile(InputStreamReader reader) {
		properties = parseProperties(reader);
	}

	public PropertyFile(File file) {
		properties = parseProperties(file);
	}

	public PropertyFile(String filename) {
		this(new File(filename));
	}

	public PropertyFile(String root, String filename) {
		this(new File(root, filename));
	}

	public PropertyFile(File root, String filename) {
		this(new File(root, filename));
	}

	private final Map<String, String> parseProperties(InputStreamReader reader) {
		BufferedReader source;
		String line, keyValue[];
		Map<String, String> result;
		result = new HashMap<String, String>();
		source = new BufferedReader(reader);
		try {
			while ((line = source.readLine()) != null) {
				keyValue = line.split(":", 2);
				if (keyValue.length != 2)
					continue;
				result.put(keyValue[0].trim(), keyValue[1].trim());
			}
			valid = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private final Map<String, String> parseProperties(File file) {
		FileReader reader;
		Map<String, String> result;
		try {
			reader = new FileReader(file);
			result = parseProperties(reader);
			reader.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<String, String>();
		}
	}

	public String getString(String property, String defaultValue) {
		String value;
		value = properties.get(property);
		if (value == null)
			return defaultValue;
		return value;
	}

	public boolean getBoolean(String property, boolean defaultValue) {
		String value;
		value = properties.get(property);
		if (value == null)
			return defaultValue;
		value = value.toLowerCase();
		return value.equals("yes") || value.equals("true") || value.equals("1");
	}

	public int getInteger(String property, int defaultValue) {
		String value;
		value = properties.get(property);
		if (value == null)
			return defaultValue;
		 try {
			 return Integer.parseInt(value);
		 } catch (Exception e) {
			 return defaultValue;
		 }
	}

	public UUID getUUID(String property, UUID defaultValue) {
		String value;
		value = properties.get(property);
		if (value == null)
			return defaultValue;
		try {
			return UUID.fromString(value);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isValid() {
		return valid;
	}
}
