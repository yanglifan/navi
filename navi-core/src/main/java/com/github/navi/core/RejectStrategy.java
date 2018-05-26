package com.github.navi.core;

/**
 * @author Yang Lifan
 */
public interface RejectStrategy {
	<T> void reject(T candidate, MatchResult rejectResult);
}
