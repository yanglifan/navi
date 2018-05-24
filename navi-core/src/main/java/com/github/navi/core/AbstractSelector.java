package com.github.navi.core;

import com.github.navi.core.exception.InvalidMatcherException;
import com.github.navi.core.exception.SelectStrategyCreationException;
import com.github.navi.core.strategy.ScoreSelectStrategy;
import com.github.navi.core.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.navi.core.utils.AnnotationUtils.annotatedBy;

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
		List<MatcherDefinition<?>> matcherDefinitions = new ArrayList<>();

		readMatcherDefinitions(candidate, matcherDefinitions);

		for (MatcherDefinition<?> matcherDefinition : matcherDefinitions) {
			MatchResult matchResult = doMatch(request, matcherDefinition);

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

	private void readMatcherDefinitions(Object candidate,
			List<MatcherDefinition<?>> matcherDefinitions) {
		List<Annotation> annotations = getTopLevelMatcherAnnotation(candidate);

		for (Annotation annotation : annotations) {
			readDefinitionFromMatcher(annotation, matcherDefinitions);
			readDefinitionFromMatcherContainer(annotation, matcherDefinitions);
			readDefinitionFromCompositeMatcher(annotation, matcherDefinitions);
		}
	}

	private void readDefinitionFromMatcherContainer(Annotation annotation,
			List<MatcherDefinition<?>> matcherDefinitions) {
		if (!annotatedBy(annotation, MatcherContainer.class)) {
			return;
		}

		readMatcherDefinitions(annotation, matcherDefinitions);
	}

	private List<Annotation> getTopLevelMatcherAnnotation(Object candidate) {
		Annotation[] annotationArray;
		if (candidate instanceof Annotation) {
			annotationArray = getAnnotationsFromAnnotation((Annotation) candidate);
		} else {
			annotationArray = candidate.getClass().getAnnotations();
		}

		return Arrays.stream(annotationArray)
				.filter(isMatcher())
				.collect(Collectors.toList());
	}

	private Annotation[] getAnnotationsFromAnnotation(Annotation annotation) {
		if (annotatedBy(annotation, MatcherContainer.class)) {
			try {
				Method valueMethod = annotation.getClass().getMethod("value");
				return (Annotation[]) valueMethod.invoke(annotation);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				throw new InvalidMatcherException(annotation + " is not a matcher container");
			}
		} else {
			return annotation.annotationType().getAnnotations();
		}
	}

	private Predicate<Annotation> isMatcher() {
		return a -> annotatedBy(a, MatcherType.class) || annotatedBy(a, CompositeMatcherType.class)
				|| annotatedBy(a, MatcherContainer.class);
	}

	private void readDefinitionFromCompositeMatcher(Annotation annotation,
			List<MatcherDefinition<?>> matcherDefinitions) {
		if (notCompositeMatcher(annotation)) {
			return;
		}

		readMatcherDefinitions(annotation, matcherDefinitions);

		merge(matcherDefinitions, aliasedAttributes(annotation));
	}

	private boolean notCompositeMatcher(Annotation annotation) {
		return !annotatedBy(annotation, CompositeMatcherType.class);
	}

	private void merge(List<MatcherDefinition<?>> matcherDefinitions,
			Map<Class<? extends Annotation>, Map<String, String[]>> allAliasedAttributes) {
		for (MatcherDefinition<?> matcherDefinition : matcherDefinitions) {
			Annotation matcher = matcherDefinition.getMatcher();
			Class<? extends Annotation> matcherType = AnnotationUtils.toClass(matcher);
			Map<String, String[]> aliasedAttributes = allAliasedAttributes.get(matcherType);
			if (aliasedAttributes != null && !aliasedAttributes.isEmpty()) {
				matcherDefinition.getAliasedAttributes().putAll(aliasedAttributes);
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

	private void readDefinitionFromMatcher(Annotation annotation,
			List<MatcherDefinition<?>> matcherDefinitions) {
		MatcherType matcher = annotation.annotationType().getAnnotation(MatcherType.class);

		if (matcher == null) {
			return;
		}

		matcherDefinitions.add(new MatcherDefinition<>(annotation));
	}

	@SuppressWarnings("unchecked")
	private MatchResult doMatch(Object request, MatcherDefinition matcherDefinition) {
		MatcherType matcherType =
				matcherDefinition.getMatcher().annotationType().getAnnotation(MatcherType.class);

		MatcherProcessor matcherProcessor = getMatcherProcessor(matcherType.processor());

		if (matcherProcessor == null) {
			throw new NullPointerException("Cannot find the matcher processor");
		}

		return matcherProcessor.process(request, matcherDefinition);
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
