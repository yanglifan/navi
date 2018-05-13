package com.github.guide.spring;

import com.github.guide.core.Selector;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBasedSelector implements Selector, ApplicationContextAware {
    private ApplicationContext applicationContext;

    public <T> T select(Object request, Class<T> targetClass) {
        return null;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
