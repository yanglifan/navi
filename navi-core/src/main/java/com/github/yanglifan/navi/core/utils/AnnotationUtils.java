package com.github.yanglifan.navi.core.utils;

import java.lang.annotation.Annotation;

/**
 * @author Yang Lifan
 */
public abstract class AnnotationUtils {
	@SuppressWarnings("unchecked")
	public static Class<? extends Annotation> toClass(Annotation annotation) {
		Class[] interfaces = annotation.getClass().getInterfaces();
		for (Class interfaceClass : interfaces) {
			if (interfaceClass.isAnnotation()) {
				return (Class<? extends Annotation>) interfaceClass;
			}
		}
		throw new RuntimeException("Not annotation");
	}

	public static boolean annotatedBy(Annotation annotation,
			Class<? extends Annotation> annotationType) {
		return annotation.annotationType().isAnnotationPresent(annotationType);
	}
}
