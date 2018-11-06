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

import com.iqiyi.navi.core.alias.AliasFor;
import com.iqiyi.navi.core.matcher.EqualMatcher;
import com.iqiyi.navi.core.matcher.VersionMatcher;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yang Lifan
 */
public class AliasTest {
	private SimpleSelector selector;

	@Before
	public void setUp() {
		selector = new SimpleSelector();
	}

	@Test
	public void alias_all() {
		// Given
		selector.registerCandidate(Handler.class, new AliasAllHandler());
		Map<String, String> correctReq = new HashMap<>();
		correctReq.put("name", "hulk");
		correctReq.put("clientVersion", "1.5.0");

		Map<String, String> wrongReq = new HashMap<>();
		wrongReq.put("name", "stark");
		wrongReq.put("clientVersion", "1.5.0");

		// When
		Handler correctHandler = selector.select(correctReq, Handler.class);
		Handler wrongHandler = selector.select(wrongReq, Handler.class);

		// Then
		assertThat(correctHandler).isInstanceOf(AliasAllHandler.class);
		assertThat(wrongHandler).isNull();
	}

	@Test
	public void alias_part() {
		// Given
		selector.registerCandidate(Handler.class, new AliasPartHandler());
		Map<String, String> correctReq = new HashMap<>();
		correctReq.put("name", "hulk");
		correctReq.put("clientVersion", "0.1.0");

		Map<String, String> wrongReq = new HashMap<>();
		wrongReq.put("name", "stark");
		wrongReq.put("clientVersion", "0.1.0");

		// When
		Handler correctHandler = selector.select(correctReq, Handler.class);
		Handler wrongHandler = selector.select(wrongReq, Handler.class);

		// Then
		assertThat(correctHandler).isInstanceOf(AliasPartHandler.class);
		assertThat(wrongHandler).isNull();
	}

	@Test
	public void labeled_alias() {
		// Given
		Map<String, String> request = new HashMap<>();
		request.put("name", "stark");
		request.put("department", "avengers");

		selector.registerCandidate(Handler.class, new LabeledAliasHandler());

		// When
		Handler handler = selector.select(request, Handler.class);

		// Then
		assertThat(handler).isInstanceOf(LabeledAliasHandler.class);
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@EqualMatcher(property = "name")
	@VersionMatcher(property = "clientVersion")
	@CompositeMatcherType
	@interface AliasAll {
		@AliasFor(annotationFor = EqualMatcher.class, attributeFor = "value")
		String name();

		@AliasFor(annotationFor = VersionMatcher.class, attributeFor = "range")
		String clientVersionRange();
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@EqualMatcher(property = "name", value = "hulk")
	@VersionMatcher(property = "clientVersion")
	@CompositeMatcherType
	@interface AliasPart {
		@AliasFor(annotationFor = VersionMatcher.class, attributeFor = "range")
		String clientVersionRange();
	}

	@SuppressWarnings("unused")
	@Retention(RetentionPolicy.RUNTIME)
	@EqualMatcher(property = "name", aliasLabel = "name")
	@EqualMatcher(property = "department", aliasLabel = "department")
	@CompositeMatcherType
	@interface LabeledAlias {
		@AliasFor(annotationFor = EqualMatcher.class, attributeFor = "value", label = "name")
		String name();

		@AliasFor(annotationFor = EqualMatcher.class, attributeFor = "value", label = "department")
		String department();
	}

	@AliasAll(name = "hulk", clientVersionRange = "[1.0.0,2.0.0)")
	private class AliasAllHandler implements Handler {
	}

	@AliasPart(clientVersionRange = "[0.1.0,0.2.0)")
	private class AliasPartHandler implements Handler {
	}

	@LabeledAlias(name = "stark", department = "avengers")
	private class LabeledAliasHandler implements Handler {
	}

}
