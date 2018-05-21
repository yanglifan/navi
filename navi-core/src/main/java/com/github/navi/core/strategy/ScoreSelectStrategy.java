package com.github.navi.core.strategy;

import com.github.navi.core.MatchResult;
import com.github.navi.core.SelectStrategy;

/**
 * @author Yang Lifan
 */
public class ScoreSelectStrategy<T> implements SelectStrategy<T> {

	private ScoreMatchResult currentWinner;
	private ScoreMatchResult currentResult;

	@Override
	public void addMatchResult(MatchResult matchResult) {
		currentResult = new ScoreMatchResult(matchResult);

		switch (currentResult.matchResult) {
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
	public T addCandidate(T candidate) {
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
