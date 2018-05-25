package com.github.navi.core;

public interface Selector {
	<T> T select(Class<T> candidateType, Object request);

	<T> T select(Class<T> candidateType, Object request, SelectStrategy<T> selectStrategy);
}
