package com.github.yanglifan.navi.core.policy;

import com.github.yanglifan.navi.core.MatchResult;
import com.github.yanglifan.navi.core.RejectPolicy;
import com.github.yanglifan.navi.core.exception.MatchRejectException;

/**
 * Fail fast
 *
 * @author Yang Lifan
 */
public class FastFailRejectPolicy implements RejectPolicy {
	@Override
	public <T> void reject(T candidate, MatchResult rejectResult) {
		if (rejectResult.getRejectException() != null) {
			throw new MatchRejectException(rejectResult.getRejectException());
		}
	}
}
