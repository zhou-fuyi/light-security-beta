package com.light.security.core.config.core.builder;

import com.light.security.core.filter.ExceptionTranslationFilter;
import com.light.security.core.filter.FilterSecurityInterceptor;
import com.light.security.core.filter.SecurityContextPretreatmentFilter;
import com.light.security.core.filter.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FilterComparator
 * @Description 仅供内部使用的{@link Comparator}对安全{@link Filter}实例进行排序，以确保它们的顺序正确。
 * @Author ZhouJian
 * @Date 2019-12-02
 */
final class FilterComparator implements Comparator<Filter>, Serializable {

    private static final int STEP = 100;
    private Map<String, Integer> filterToOrder = new HashMap<>();

    FilterComparator(){
        int order = 100;
        put(SecurityContextPretreatmentFilter.class, order);
        order += STEP;
        put(UsernamePasswordAuthenticationFilter.class, order);
        order += STEP;
        put(ExceptionTranslationFilter.class, order);
        order += STEP;
        put(FilterSecurityInterceptor.class, order);
    }

    @Override
    public int compare(Filter lhs, Filter rhs) {
        Integer left = getOrder(lhs.getClass());
        Integer right = getOrder(rhs.getClass());
        return left - right;
    }

    /**
     * Determines if a particular {@link Filter} is registered to be sorted
     *
     * @param filter
     * @return
     */
    public boolean isRegistered(Class<? extends Filter> filter) {
        return getOrder(filter) != null;
    }

    /**
     * Registers a {@link Filter} to exist after a particular {@link Filter} that is
     * already registered.
     * @param filter the {@link Filter} to register
     * @param afterFilter the {@link Filter} that is already registered and that
     * {@code filter} should be placed after.
     */
    public void registerAfter(Class<? extends Filter> filter,
                              Class<? extends Filter> afterFilter) {
        Integer position = getOrder(afterFilter);
        if (position == null) {
            throw new IllegalArgumentException(
                    "Cannot register after unregistered Filter " + afterFilter);
        }

        put(filter, position + 1);
    }

    /**
     * Registers a {@link Filter} to exist at a particular {@link Filter} position
     * @param filter the {@link Filter} to register
     * @param atFilter the {@link Filter} that is already registered and that
     * {@code filter} should be placed at.
     */
    public void registerAt(Class<? extends Filter> filter,
                           Class<? extends Filter> atFilter) {
        Integer position = getOrder(atFilter);
        if (position == null) {
            throw new IllegalArgumentException(
                    "Cannot register after unregistered Filter " + atFilter);
        }

        put(filter, position);
    }

    /**
     * Registers a {@link Filter} to exist before a particular {@link Filter} that is
     * already registered.
     * @param filter the {@link Filter} to register
     * @param beforeFilter the {@link Filter} that is already registered and that
     * {@code filter} should be placed before.
     */
    public void registerBefore(Class<? extends Filter> filter,
                               Class<? extends Filter> beforeFilter) {
        Integer position = getOrder(beforeFilter);
        if (position == null) {
            throw new IllegalArgumentException(
                    "Cannot register after unregistered Filter " + beforeFilter);
        }

        put(filter, position - 1);
    }

    private void put(Class<? extends Filter> filter, int position) {
        String className = filter.getName();
        filterToOrder.put(className, position);
    }

    /**
     * Gets the order of a particular {@link Filter} class taking into consideration
     * superclasses.
     *
     * @param clazz the {@link Filter} class to determine the sort order
     * @return the sort order or null if not defined
     */
    private Integer getOrder(Class<?> clazz) {
        while (clazz != null) {
            Integer result = filterToOrder.get(clazz.getName());
            if (result != null) {
                return result;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
