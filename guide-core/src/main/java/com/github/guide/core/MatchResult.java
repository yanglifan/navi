package com.github.guide.core;

/**
 * @author Yang Lifan
 */
public class MatchResult {
    private ResultType resultType;

    enum ResultType {
        ACCEPT, NEUTRUAL, REJECT
    }
}
