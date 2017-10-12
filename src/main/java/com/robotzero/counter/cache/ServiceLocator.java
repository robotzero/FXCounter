package com.robotzero.counter.cache;

interface ServiceLocator {
    Object get(Class clazz, Object reference);
}
