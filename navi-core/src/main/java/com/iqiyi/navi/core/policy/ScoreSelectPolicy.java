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
import com.iqiyi.navi.core.SelectPolicy;

/**
 * @author Yang Lifan
 */
public class ScoreSelectPolicy<T> implements SelectPolicy<T> {

	private ScoreMatchResult currentWinner;
	private ScoreMatchResult currentResult;

	@Override
	public void addMatchResult(MatchResult matchResult) {
		currentResult = new ScoreMatchResult(matchResult);

		switch (currentResult.matchResult.getType()) {
			case ACCEPT:
				currentResult.score++;
				break;
			case NEUTRAL:
				// Not change score
				break;
			case REJECT:
				currentResult.score = -1;
				break;
		}
	}

	@Override
	public T addCandidateAndGetResult(T candidate) {
		if (currentResult == null) {
			return null;
		}

		currentResult.candidate = candidate;

		if (isFirstValidMatchResult()) {
			currentWinner = currentResult;
		}

		if (isScoreHigherThanCurrentOne()) {
			currentWinner = currentResult;
		}

		return null;
	}

	@Override
	public T getResult() {
		if (currentWinner == null) {
			return null;
		}

		return currentWinner.candidate;
	}

	private boolean isScoreHigherThanCurrentOne() {
		return currentWinner != null && currentResult.score >= currentWinner.score;
	}

	private boolean isFirstValidMatchResult() {
		return currentWinner == null && currentResult.score > -1;
	}

	private class ScoreMatchResult {
		private int score;
		private T candidate;
		private MatchResult matchResult;

		private ScoreMatchResult(MatchResult matchResult) {
			this.matchResult = matchResult;
		}
	}
}
