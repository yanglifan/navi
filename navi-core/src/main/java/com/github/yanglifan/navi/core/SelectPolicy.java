package com.github.yanglifan.navi.core;

/**
 * @author Yang Lifan
 */
public interface SelectPolicy<T> {
	void addMatchResult(MatchResult matchResult);

	T addCandidate(T candidate);

	T getResult();
}
