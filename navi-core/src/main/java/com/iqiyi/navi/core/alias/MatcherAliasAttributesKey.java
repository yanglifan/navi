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

package com.iqiyi.navi.core.alias;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author Yang Lifan
 */
public class MatcherAliasAttributesKey {
	private Class<? extends Annotation> matcherType;
	private String aliasLabel;

	public MatcherAliasAttributesKey(Class<? extends Annotation> matcherType, String aliasLabel) {
		this.matcherType = matcherType;
		this.aliasLabel = aliasLabel;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MatcherAliasAttributesKey that = (MatcherAliasAttributesKey) o;
		return Objects.equals(matcherType, that.matcherType) &&
				Objects.equals(aliasLabel, that.aliasLabel);
	}

	@Override
	public int hashCode() {

		return Objects.hash(matcherType, aliasLabel);
	}
}
