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

package com.iqiyi.navi.spring;

import com.iqiyi.navi.core.AbstractSelector;
import com.iqiyi.navi.core.MatcherProcessor;
import com.iqiyi.navi.core.SelectPolicy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBasedSelector extends AbstractSelector implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	public SpringBasedSelector() {
		super();
	}

	public SpringBasedSelector(Class<? extends SelectPolicy> defaultSelectPolicyClass) {
		super(defaultSelectPolicyClass);
	}

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

	@SuppressWarnings("unchecked")
	@Override
	protected <T> SelectPolicy<T> createSelectPolicy() {
		// TODO Check SelectPolicy bean is prototype.
		try {
			return applicationContext.getBean(this.defaultSelectPolicyClass);
		} catch (NoSuchBeanDefinitionException e) {
			return super.createSelectPolicy();
		}
	}
}
