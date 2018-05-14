package com.github.navi.core;

/**
 * @author Yang Lifan
 */
public class ScoreSelectStrategy<T> implements SelectStrategy<T> {

    private ScoreMatchResult currentWinner;
    private ScoreMatchResult currentResult;

    @Override
    public void addMatchResult(MatchResult<T> matchResult) {
        currentResult = new ScoreMatchResult(matchResult);

        switch (currentResult.matchResult.getResultType()) {
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
    public void addCandidate(T candidate) {
        if (currentResult == null) {
            return;
        }

        currentResult.candidate = candidate;

        if (isFirstValidMatchResult()) {
            currentWinner = currentResult;
        }

        if (isScoreHigherThanCurrentOne()) {
            currentWinner = currentResult;
        }
    }

    @Override
    public T getWinner() {
        if (currentWinner == null) {
            return null;
        }

        return currentWinner.candidate;
    }

    private class ScoreMatchResult {
        private int score;
        private T candidate;
        private MatchResult<T> matchResult;

        private ScoreMatchResult(MatchResult<T> matchResult) {
            this.matchResult = matchResult;
        }
    }

    private boolean isScoreHigherThanCurrentOne() {
        return currentWinner != null && currentResult.score >= currentWinner.score;
    }

    private boolean isFirstValidMatchResult() {
        return currentWinner == null && currentResult.score > -1;
    }
}
