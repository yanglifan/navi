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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Yang Lifan
 */
public class SimpleSelector extends AbstractSelector {
	private Map<Class<?>, Set> candidatesByType = new HashMap<>();

	public SimpleSelector() {
		super();
	}

	public SimpleSelector(Class<? extends SelectPolicy> defaultSelectPolicyClass) {
		super(defaultSelectPolicyClass);
	}

	@SuppressWarnings("unchecked")
	public void registerCandidates(Class<?> candidateType, Set candidates) {
		Collection existed = this.candidatesByType.get(candidateType);
		if (existed == null) {
			this.candidatesByType.put(candidateType, candidates);
		} else {
			existed.addAll(candidates);
		}
	}

	public void registerCandidate(Class<?> candidateType, Object candidate) {
		Set existed = this.candidatesByType.get(candidateType);
		if (existed == null) {
			existed = new HashSet();
			candidatesByType.put(candidateType, existed);
		}

		existed.add(candidate);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MatcherProcessor getMatcherProcessor(Class<? extends MatcherProcessor> processorClass) {
		try {
			return (MatcherProcessor<Annotation>)
					Class.forName(processorClass.getName()).newInstance();
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Iterable<T> findCandidatesByType(Class<T> beanClass) {
		return this.candidatesByType.get(beanClass);
	}
}
