package com.github.navi.core;

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
