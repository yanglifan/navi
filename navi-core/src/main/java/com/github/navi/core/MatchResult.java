package com.github.navi.core;

/**
 * @author Yang Lifan
 */
public class MatchResult<T> {
    private ResultType resultType;


    private MatchResult(ResultType resultType) {
        this.resultType = resultType;
    }

    public static <S> MatchResult<S> accept() {
        return new MatchResult<>(ResultType.ACCEPT);
    }

    public static <S> MatchResult<S> reject() {
        return new MatchResult<>(ResultType.REJECT);
    }

    public enum ResultType {
        ACCEPT, NEUTRAL, REJECT
    }

    public ResultType getResultType() {
        return resultType;
    }
}
