package com.github.navi.core.alias;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Simplify, remove it
 *
 * @author Yang Lifan
 */
public class AliasAttributes {
	private Map<String, String[]> values = new HashMap<>();

	public Map<String, String[]> getValues() {
		return values;
	}

	void addAttribute(String key, String[] value) {
		this.values.put(key, value);
	}
}
