package com.github.navi.spring;

import com.github.navi.core.AbstractSelector;
import com.github.navi.core.MatcherProcessor;
import com.github.navi.core.SelectStrategy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBasedSelector extends AbstractSelector implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	protected MatcherProcessor getMatcherProcessor(Class<? extends MatcherProcessor> processorClass) {
		return applicationContext.getBean(processorClass);
	}

	@Override
	protected <T> Iterable<T> findCandidatesByType(Class<T> beanClass) {
		return applicationContext.getBeansOfType(beanClass).values();
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T> SelectStrategy<T> createSelectStrategy() {
		SelectStrategy<T> selectStrategy = applicationContext.getBean(this.defaultSelectStrategyClass);
		// TODO Check SelectStrategy bean is prototype.
		return selectStrategy == null ? super.createSelectStrategy() : selectStrategy;
	}
}
