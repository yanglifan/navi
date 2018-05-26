package com.github.navi.core.policy;

import com.github.navi.core.MatchResult;
import com.github.navi.core.RejectPolicy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yang Lifan
 */
@Slf4j
public class DefaultRejectPolicy implements RejectPolicy {
	@Override
	public <T> void reject(T candidate, MatchResult rejectResult) {
		if (rejectResult.getRejectException() != null) {
			Exception e = rejectResult.getRejectException();
			log.warn("{} was rejected by the following exception:",
					candidate.getClass().getSimpleName(), e);
		}
	}
}
