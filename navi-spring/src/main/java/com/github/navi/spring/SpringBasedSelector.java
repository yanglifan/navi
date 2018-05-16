package com.github.navi.spring;

import com.github.navi.core.AbstractSelector;
import com.github.navi.core.MatcherProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;

public class SpringBasedSelector extends AbstractSelector implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	protected MatcherProcessor<Annotation> getMatcherProcessor(
			Class<? extends MatcherProcessor> processorClass) {
		return applicationContext.getBean(processorClass);
	}

	@Override
	protected <T> Iterable<T> findCandidatesByType(Class<T> beanClass) {
		return applicationContext.getBeansOfType(beanClass).values();
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
