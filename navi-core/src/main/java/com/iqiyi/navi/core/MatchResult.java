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

package com.iqiyi.navi.core;

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
