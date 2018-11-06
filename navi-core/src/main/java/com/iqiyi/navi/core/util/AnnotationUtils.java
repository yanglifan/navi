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

package com.iqiyi.navi.core.util;

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
