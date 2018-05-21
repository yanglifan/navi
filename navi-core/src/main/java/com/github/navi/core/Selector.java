package com.github.navi.core;

public interface Selector {
	<T> T select(Object request, Class<T> candidateType);

	<T> T select(Object request, Class<T> candidateType, SelectStrategy<T> selectStrategy);
}
