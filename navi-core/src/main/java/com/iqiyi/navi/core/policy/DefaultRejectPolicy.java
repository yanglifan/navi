/*
 * Copyright 2018 Yang Lifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iqiyi.navi.core.policy;

import com.iqiyi.navi.core.MatchResult;
import com.iqiyi.navi.core.RejectPolicy;
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
