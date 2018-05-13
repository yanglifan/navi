package com.github.guide.core;

/**
 * @author Yang Lifan
 */
public class ScoreSelectStrategy<T> implements SelectStrategy<T> {

    private ScoreMatchResult currentScoreMatchResult;

    @Override
    public void addMatchResult(MatchResult<T> matchResult) {
        ScoreMatchResult scoreMatchResult = new ScoreMatchResult(matchResult);
        setMatchScore(matchResult, scoreMatchResult);
        changeCurrentResult(scoreMatchResult);
    }

    @Override
    public T getWinner() {
        if (currentScoreMatchResult == null) {
            return null;
        }

        return currentScoreMatchResult.matchResult.getCandidate();
    }

    private void changeCurrentResult(ScoreMatchResult scoreMatchResult) {
        if (isFirstValidMatchResult(scoreMatchResult)) {
            currentScoreMatchResult = scoreMatchResult;
        }

        if (isScoreHigherThanCurrentOne(scoreMatchResult)) {
            currentScoreMatchResult = scoreMatchResult;
        }
    }

    private boolean isScoreHigherThanCurrentOne(ScoreMatchResult scoreMatchResult) {
        return currentScoreMatchResult != null && scoreMatchResult.score >= currentScoreMatchResult.score;
    }

    private boolean isFirstValidMatchResult(ScoreMatchResult scoreMatchResult) {
        return currentScoreMatchResult == null && scoreMatchResult.score > -1;
    }

    private void setMatchScore(MatchResult matchResult, ScoreMatchResult scoreMatchResult) {
        switch (matchResult.getResultType()) {
            case ACCEPT:
                scoreMatchResult.score++;
                break;
            case NEUTRAL:
                // Not change score
                break;
            case REJECT:
                scoreMatchResult.score = -1;
                break;
        }
    }

    private class ScoreMatchResult {
        private int score;
        private MatchResult<T> matchResult;

        private ScoreMatchResult(MatchResult<T> matchResult) {
            this.matchResult = matchResult;
        }
    }
}
