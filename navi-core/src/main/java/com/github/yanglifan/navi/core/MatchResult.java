package com.github.yanglifan.navi.core;

/**
 * @author Yang Lifan
 */
public class MatchResult {
	private MatchType type;
	private Exception rejectException;

	public static MatchResult accept() {
		return create(MatchType.ACCEPT);
	}

	public static MatchResult neutral() {
		return create(MatchType.NEUTRAL);
	}

	public static MatchResult reject() {
		return create(MatchType.REJECT);
	}

	public static MatchResult reject(Exception ex) {
		MatchResult matchResult = create(MatchType.REJECT);
		matchResult.rejectException = ex;
		return matchResult;
	}

	private static MatchResult create(MatchType matchType) {
		MatchResult matchResult = new MatchResult();
		matchResult.type = matchType;
		return matchResult;
	}

	public MatchType getType() {
		return type;
	}

	public Exception getRejectException() {
		return rejectException;
	}

	public enum MatchType {
		ACCEPT, NEUTRAL, REJECT
	}
}
