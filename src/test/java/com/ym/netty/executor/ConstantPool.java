package com.jd.biz.service.util.executor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yangmeng44
 * @Date 2017/7/21
 */
public abstract class ConstantPool<T extends Constant<T>> {

    private final Map<String, T> constants = new HashMap<String, T>();

    private int nextId = 1;

    /**
     * Shortcut of {@link #valueOf(String) valueOf(firstNameComponent.getName() + "#" + secondNameComponent)}.
     */
    public T valueOf(Class<?> firstNameComponent, String secondNameComponent) {
        if (firstNameComponent == null) {
            throw new NullPointerException("firstNameComponent");
        }
        if (secondNameComponent == null) {
            throw new NullPointerException("secondNameComponent");
        }

        return valueOf(firstNameComponent.getName() + '#' + secondNameComponent);
    }

    /**
     * Returns the {@link Constant} which is assigned to the specified {@code name}.
     * If there's no such {@link Constant}, a new one will be created and returned.
     * Once created, the subsequent calls with the same {@code name} will always return the previously created one
     * (i.e. singleton.)
     *
     * @param name the name of the {@link Constant}
     */
    public T valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }

        synchronized (constants) {
            T c = constants.get(name);
            if (c == null) {
                c = newConstant(nextId, name);
                constants.put(name, c);
                nextId ++;
            }

            return c;
        }
    }

    protected abstract T newConstant(int id, String name);
}
