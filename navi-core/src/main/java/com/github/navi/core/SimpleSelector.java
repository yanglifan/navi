package com.github.navi.core;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Yang Lifan
 */
public class SimpleSelector extends AbstractSelector {
	private Map<Class<?>, Set> candidatesByType = new HashMap<>();

	@SuppressWarnings("unchecked")
	public void registerCandidates(Class<?> candidateClass, Set candidates) {
		Collection existed = this.candidatesByType.get(candidateClass);
		if (existed == null) {
			this.candidatesByType.put(candidateClass, candidates);
		} else {
			existed.addAll(candidates);
		}
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
