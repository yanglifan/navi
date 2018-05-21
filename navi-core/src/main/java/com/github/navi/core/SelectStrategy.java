package com.github.navi.core;

/**
 * @author Yang Lifan
 */
public interface SelectStrategy<T> {
	void addMatchResult(MatchResult matchResult);

	T addCandidate(T candidate);

	T getResult();
}
