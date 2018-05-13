package com.github.guide.spring;

import com.github.guide.core.AbstractSelector;
import com.github.guide.core.IndicatorProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBasedSelector extends AbstractSelector implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    protected IndicatorProcessor getIndicatorProcessor(
            Class<? extends IndicatorProcessor> processorClass) {

        return applicationContext.getBean(processorClass);
    }

    @Override
    protected <T> Iterable<T> findBeansByType(Class<T> beanClass) {
        return applicationContext.getBeansOfType(beanClass).values();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
