package com.github.navi.core.strategy;

import com.github.navi.core.MatchResult;
import com.github.navi.core.RejectStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yang Lifan
 */
@Slf4j
public class DefaultRejectStrategy implements RejectStrategy {
	@Override
	public void reject(MatchResult rejectResult) {
		if (rejectResult.getRejectException() != null) {
			Exception e = rejectResult.getRejectException();
			log.warn(e.getMessage(), e);
		}
	}
}
