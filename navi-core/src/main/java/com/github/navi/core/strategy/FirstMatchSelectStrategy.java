package com.github.navi.core.strategy;

import com.github.navi.core.MatchResult;
import com.github.navi.core.SelectStrategy;

/**
 * @author Yang Lifan
 */
public class FirstMatchSelectStrategy<T> implements SelectStrategy<T> {

	@Override
	public void addMatchResult(MatchResult matchResult) {
		// Not need to implement
	}

	@Override
	public T addCandidate(T candidate) {
		return candidate;
	}

	@Override
	public T getResult() {
		return null;
	}
}
