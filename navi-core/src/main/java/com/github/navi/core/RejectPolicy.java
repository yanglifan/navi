package com.github.navi.core;

/**
 * @author Yang Lifan
 */
public interface RejectPolicy {
	<T> void reject(T candidate, MatchResult rejectResult);
}
