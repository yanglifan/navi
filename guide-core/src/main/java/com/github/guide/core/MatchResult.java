package com.github.guide.core;

/**
 * @author Yang Lifan
 */
public class MatchResult<T> {
    private ResultType resultType = ResultType.NEUTRAL;
    private T candidate;


    public MatchResult(T candidate) {
        this.candidate = candidate;
    }

    enum ResultType {
        ACCEPT, NEUTRAL, REJECT
    }

    public T getCandidate() {
        return candidate;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }
}
