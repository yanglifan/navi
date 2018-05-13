package com.github.guide.core;

/**
 * @author Yang Lifan
 */
public class ProcessResult {
    private ResultType resultType;

    enum ResultType {
        ACCEPT, NEUTRUAL, REJECT
    }
}
