package com.github.navi.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Yang Lifan
 */
public class MatcherProcessorAutoRegister implements BeanDefinitionRegistryPostProcessor {

	// TODO @Nonnull?
	// TODO Log
	@Override
	public void postProcessBeanDefinitionRegistry(@Nonnull BeanDefinitionRegistry beanDefinitionRegistry)
			throws BeansException {
		List<Class> matcherProcessorClasses = MatcherProcessorClassFinder.findAll();
		for (Class processorClass : matcherProcessorClasses) {
			BeanDefinition beanDefinition =
					BeanDefinitionBuilder.genericBeanDefinition(processorClass).getBeanDefinition();
			String beanName = buildBeanNameForMatcherProcessor(processorClass);
			beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
		}
	}

	@Override
	public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory configurableListableBeanFactory)
			throws BeansException {
		// Need not to implement
	}

	private String buildBeanNameForMatcherProcessor(Class clazz) {
		String classFullName = clazz.getName();
		String packageName = clazz.getPackage().getName();
		String className = classFullName.replace(packageName + ".", "");
		className = className.replace("$", "");
		String firstLetter = className.substring(0, 1);
		String other = className.substring(1, className.length());
		return firstLetter.toLowerCase() + other;
	}
}
