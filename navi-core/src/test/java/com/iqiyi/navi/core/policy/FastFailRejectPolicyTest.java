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

import com.iqiyi.navi.core.BaseTest;
import com.iqiyi.navi.core.Handler;
import com.iqiyi.navi.core.exception.MatchRejectException;
import com.iqiyi.navi.core.matcher.EqualMatcher;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FastFailRejectPolicyTest extends BaseTest {
	@SuppressWarnings("all")
	@Test
	public void reject_by_no_such_method() {
		// Given
		Object request = new Object();
		registerHandler(new FailHandler());
		selector.setRejectPolicy(new FastFailRejectPolicy());

		// When
		Exception ex = null;
		try {
			selector.select(request, Handler.class);
		} catch (Exception e) {
			ex = e;
		}

		// Then
		assertThat(ex).isInstanceOf(MatchRejectException.class);
		assertThat(ex.getCause()).isInstanceOf(NoSuchMethodException.class);
	}

	@EqualMatcher(property = "name", value = "duncan")
	private class FailHandler implements Handler {
	}
}