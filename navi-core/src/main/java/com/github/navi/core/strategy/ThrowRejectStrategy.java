package com.github.navi.core.strategy;

import com.github.navi.core.MatchResult;
import com.github.navi.core.RejectStrategy;
import com.github.navi.core.exception.MatchRejectException;

/**
 * Fail fast
 *
 * @author Yang Lifan
 */
public class ThrowRejectStrategy implements RejectStrategy {
	@Override
	public void reject(MatchResult rejectResult) {
		if (rejectResult.getRejectException() != null) {
			throw new MatchRejectException(rejectResult.getRejectException());
		}
	}
}
