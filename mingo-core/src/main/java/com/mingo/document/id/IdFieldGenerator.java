/**
 * Copyright 2012-2013 The Mingo Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mingo.document.id;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mingo.document.annotation.GeneratedValue;
import com.mingo.document.id.generator.IdGenerator;
import com.mingo.document.id.generator.factory.IdGeneratorFactory;
import com.mingo.exceptions.IdGenerationException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.mingo.util.DocumentUtils.isId;
import static java.text.MessageFormat.format;


/**
 * Generates and sets new values for the fields that are annotated with @Id and @GeneratedValue.
 */
public class IdFieldGenerator {

    private IdGeneratorFactory idGeneratorFactory;

    private final static Table<Class<?>, Class<?>, Function> transformers = HashBasedTable.create();

    // messages
    private static final String INCOMPATIBLE_TYPES = "Incompatible types. Failed to set generated value: ''{0}'' (type: {1}) to the field: ''{2}'' (type: {3}) from {4}. Generator strategy: ''{5}''";
    private static final String GENERATOR_NOT_FOUND = "wasn't found any generators for strategy: ''{0}'' and type: ''{1}''";

    static {
        transformers.put(Long.class, String.class, Object::toString);
        transformers.put(Integer.class, String.class, Object::toString);
        transformers.put(Long.TYPE, String.class, Object::toString);
        transformers.put(Integer.TYPE, String.class, Object::toString);
    }

    public IdFieldGenerator(IdGeneratorFactory idGeneratorFactory) {
        this.idGeneratorFactory = idGeneratorFactory;
    }

    /**
     * Generates values for all pojo's ids fields including
     * inherited fields that annotated with {@link com.mingo.document.annotation.Id} and with {@link com.mingo.document.annotation.GeneratedValue}.
     * <p>
     * Generation depends on chosen strategy or a given id field type.
     * see predefined id generators strategies {@link com.mingo.document.id.generator.IdGeneratorStrategy}.
     * <p>
     * Generates values for nested fields that also have an id field, for instance if the pojo has collection of objects
     * of a type that has a field that annotated with @Id and @GeneratedValue then for each object form a collection will
     * be generated new id value.
     * It applies for {@code Collection}, {@code Array} and {@code Map}.
     * <p>
     * If an id field is final, synthetic or static then new value will not be generated.
     *
     * @param pojo the pojo that has identifier field for that new value should be generated
     * @throws IdGenerationException
     */
    public void generateId(Object pojo) throws IdGenerationException {
        if (pojo != null) {
            doWithFields(pojo, pojo.getClass(), generateId, skipSyntheticField, skipFinalField, skipStaticField);
        }
    }

    private void generateIdForArray(Object[] objects) {
        generateIdForList(Arrays.asList(objects));
    }

    private void generateIdForList(Collection<?> objects) {
        objects.forEach(this::generateId);
    }

    private void generateIdForMap(Map<?, ?> objects) {
        objects.forEach((key, val) -> {
            generateId(key);
            generateId(val);
        });
    }

    private void checkAndGenerateId(Object pojo, Field field) throws IdGenerationException {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            IdGenerator idGenerator = idGeneratorFactory.create(generatedValue.strategy(), field.getType());
            if (idGenerator == null) {
                throw new IdGenerationException(format(GENERATOR_NOT_FOUND, generatedValue.strategy(), field.getType()));
            }
            generateAndSet(pojo, field, idGenerator, generatedValue.strategy());
        }
    }

    private void generateAndSet(Object pojo, Field field, IdGenerator idGenerator, String strategy) {
        setValue(pojo, field, idGenerator.generate(), strategy);
    }

    private void setValue(Object pojo, Field field, Object val, String strategy) {
        try {
            if (!instanceOf(val.getClass(), field.getType())) {
                Function transformer = transformers.get(val.getClass(), field.getType());
                if (transformer == null) {
                    throw new IdGenerationException(format(INCOMPATIBLE_TYPES, val, val.getClass(), field.getName(), field.getType(), field.getDeclaringClass(), strategy));
                }
                val = transformer.apply(val);
            }
            field.set(pojo, val);
        } catch (IllegalAccessException e) {
            throw new IdGenerationException(e);
        }
    }

    private boolean instanceOf(Class<?> cls, Class<?> toClass) {
        cls = ClassUtils.primitiveToWrapper(cls);
        toClass = ClassUtils.primitiveToWrapper(toClass);
        return ClassUtils.isAssignable(cls, toClass);
    }

    private FieldCallback generateId = (target, field) -> {
        try {
            field.setAccessible(true); //make field accessible to set/get value
            if (isId(field)) {
                checkAndGenerateId(target, field);
            } else {
                Object child = field.get(target);
                if (child != null) {
                    if (child instanceof Collection) {
                        generateIdForList((Collection) child);
                    } else if (child instanceof Object[]) {
                        generateIdForArray((Object[]) child);
                    } else if (child instanceof Map) {
                        generateIdForMap((Map) child);
                    } else {
                        generateId(child);
                    }
                }
            }

        } catch (Throwable e) {
            throw new IdGenerationException(e);
        }
    };

    private interface FieldCallback {
        void apply(Object target, Field field);
    }

    /**
     * Filters for fields.
     */
    private Predicate<Field> skipSyntheticField = field -> !field.isSynthetic();
    private Predicate<Field> skipFinalField = field -> !Modifier.isFinal(field.getModifiers());
    private Predicate<Field> skipStaticField = field -> !Modifier.isStatic(field.getModifiers());

    private void doWithFields(Object target, Class<?> clazz, FieldCallback fc, Predicate<Field>... filters)
            throws IllegalArgumentException {
        Class<?> targetClass = clazz;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                if (filters != null && filters.length > 0 && !applyFilters(field, filters)) {
                    continue;
                }
                fc.apply(target, field);
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }

    private boolean applyFilters(Field field, Predicate<Field>... filters) {
        for (Predicate<Field> filter : Arrays.asList(filters)) {
            if (!filter.test(field)) {
                return false;
            }
        }
        return true;
    }

}
