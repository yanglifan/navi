package com.github.navi.core.policy;

import com.github.navi.core.MatchResult;
import com.github.navi.core.SelectPolicy;

/**
 * @author Yang Lifan
 */
public class FirstMatchSelectPolicy<T> implements SelectPolicy<T> {

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
