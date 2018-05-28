package com.github.yanglifan.navi.core.policy;

import com.github.yanglifan.navi.core.MatchResult;
import com.github.yanglifan.navi.core.SelectPolicy;

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
