package com.github.guide.core;

public interface Selector {
    <T> T select(Object request, Class<T> targetClass);
}
