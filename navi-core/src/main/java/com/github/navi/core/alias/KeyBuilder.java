package com.github.navi.core.alias;

/**
 * @author Yang Lifan
 */
public abstract class KeyBuilder {
	public static String build(Class<?> matcherType, String label) {
		return matcherType.getSimpleName() + "." + label;
	}
}
