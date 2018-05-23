package com.github.navi.core;

import com.github.navi.core.exception.SelectStrategyCreationException;
import com.github.navi.core.strategy.ScoreSelectStrategy;
import com.github.navi.core.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yang Lifan
 */
public abstract class AbstractSelector implements Selector {
	protected Class<? extends SelectStrategy> defaultSelectStrategyClass;

	public AbstractSelector() {
		this(ScoreSelectStrategy.class);
	}

	public AbstractSelector(Class<? extends SelectStrategy> defaultSelectStrategyClass) {
		this.defaultSelectStrategyClass = defaultSelectStrategyClass;
	}

	public <T> T select(Object request, Class<T> candidateType) {
		return this.select(request, candidateType, createSelectStrategy());
	}

	@Override
	public <T> T select(Object request, Class<T> candidateType, SelectStrategy<T> selectStrategy) {
		Iterable<T> candidates = findCandidatesByType(candidateType);

		for (T candidate : candidates) {
			T result = doMatch(request, candidate, selectStrategy);

			if (result != null) {
				return result;
			}
		}

		return selectStrategy.getResult();
	}

	private <T> T doMatch(Object request, T candidate, SelectStrategy<T> selectStrategy) {
		List<MatcherDescription<?>> matcherDescriptions = getMatcherDescriptions(candidate);

		for (MatcherDescription<?> matcherDescription : matcherDescriptions) {
			MatchResult matchResult = doMatch(request, matcherDescription);

			if (matchResult == null) {
				continue;
			}

			if (matchResult == MatchResult.REJECT) {
				return null;
			}

			selectStrategy.addMatchResult(matchResult);
		}

		return selectStrategy.addCandidate(candidate);
	}

	private List<MatcherDescription<?>> getMatcherDescriptions(Object candidate) {
		Annotation[] annotations = getAnnotations(candidate);

		List<MatcherDescription<?>> matcherDescriptions = new ArrayList<>();

		for (Annotation annotationOnCandidate : annotations) {
			processMatcherType(annotationOnCandidate, matcherDescriptions);
			processCompositeMatcherType(annotationOnCandidate, matcherDescriptions);
		}

		return matcherDescriptions;
	}

	private Annotation[] getAnnotations(Object candidate) {
		if (candidate instanceof Annotation) {
			return ((Annotation) candidate).annotationType().getAnnotations();
		} else {
			return candidate.getClass().getAnnotations();
		}
	}

	private void processCompositeMatcherType(Annotation annotation,
			List<MatcherDescription<?>> matcherDescriptions) {
		if (notCompositeMatcher(annotation)) {
			return;
		}

		Annotation[] subAnnotations = annotation.annotationType().getAnnotations();
		for (Annotation subAnnotation : subAnnotations) {
			processMatcherType(subAnnotation, matcherDescriptions);
		}

		merge(matcherDescriptions, aliasedAttributes(annotation));
	}

	private boolean notCompositeMatcher(Annotation annotationOnCandidate) {
		CompositeMatcherType compositeMatcherType =
				annotationOnCandidate.annotationType().getAnnotation(CompositeMatcherType.class);
		return compositeMatcherType == null;
	}

	private void merge(List<MatcherDescription<?>> matcherDescriptions,
			Map<Class<? extends Annotation>, Map<String, String[]>> allAliasedAttributes) {
		for (MatcherDescription<?> matcherDescription : matcherDescriptions) {
			Annotation matcher = matcherDescription.getMatcher();
			Class<? extends Annotation> matcherType = AnnotationUtils.toClass(matcher);
			Map<String, String[]> aliasedAttributes = allAliasedAttributes.get(matcherType);
			if (aliasedAttributes != null && !aliasedAttributes.isEmpty()) {
				matcherDescription.getAliasedAttributes().putAll(aliasedAttributes);
			}
		}
	}

	private Map<Class<? extends Annotation>, Map<String, String[]>> aliasedAttributes(
			Annotation compositeMatcher) {
		Map<Class<? extends Annotation>, Map<String, String[]>> aliasAttributes = new HashMap<>();
		Method[] methods = compositeMatcher.annotationType().getMethods();
		for (Method aliasAttrMethod : methods) {
			AliasAttribute aliasAttribute = aliasAttrMethod.getAnnotation(AliasAttribute.class);
			if (aliasAttribute == null) {
				continue;
			}

			String[] aliasedValues = getAliasedValue(compositeMatcher, aliasAttrMethod);

			Map<String, String[]> aliasedAttributes =
					createAliasedAttributesIfAbsent(aliasAttributes, aliasAttribute);
			aliasedAttributes.put(aliasAttribute.attributeFor(), aliasedValues);
		}
		return aliasAttributes;
	}

	private String[] getAliasedValue(Annotation compositeMatcher, Method aliasedPropMethod) {
		try {
			Object value = aliasedPropMethod.invoke(compositeMatcher);
			if (value instanceof String) {
				return new String[]{(String) value};
			}

			if (value instanceof String[]) {
				return (String[]) value;
			}

			throw new RuntimeException("Unsupported type");
		} catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, String[]> createAliasedAttributesIfAbsent(Map<Class<? extends Annotation>,
			Map<String, String[]>> aliasAttributes, AliasAttribute aliasAttribute) {
		Class<? extends Annotation> aliasedAnnotation = aliasAttribute.annotationFor();
		return aliasAttributes.computeIfAbsent(aliasedAnnotation, (a) -> new HashMap<>());
	}

	private void processMatcherType(Annotation annotation,
			List<MatcherDescription<?>> matcherDescriptions) {
		MatcherType matcher = annotation.annotationType().getAnnotation(MatcherType.class);

		if (matcher == null) {
			return;
		}

		matcherDescriptions.add(new MatcherDescription<>(annotation));
	}

	@SuppressWarnings("unchecked")
	private MatchResult doMatch(Object request, MatcherDescription matcherDescription) {
		MatcherType matcherType =
				matcherDescription.getMatcher().annotationType().getAnnotation(MatcherType.class);

		MatcherProcessor matcherProcessor = getMatcherProcessor(matcherType.processor());

		if (matcherProcessor == null) {
			throw new NullPointerException("Cannot find the matcher processor");
		}

		return matcherProcessor.process(request, matcherDescription);
	}

	protected abstract MatcherProcessor getMatcherProcessor(
			Class<? extends MatcherProcessor> processorClass);

	protected abstract <T> Iterable<T> findCandidatesByType(Class<T> beanClass);

	@SuppressWarnings("unchecked")
	protected <T> SelectStrategy<T> createSelectStrategy() {
		try {
			return defaultSelectStrategyClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SelectStrategyCreationException(e);
		}
	}
}
