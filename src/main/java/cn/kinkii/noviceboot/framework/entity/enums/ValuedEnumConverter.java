package cn.kinkii.noviceboot.framework.entity.enums;

import cn.kinkii.noviceboot.framework.utils.GenericsUtils;

import javax.persistence.AttributeConverter;

import static cn.kinkii.noviceboot.framework.utils.GenericsUtils.getSuperclassGenericType;

public abstract class ValuedEnumConverter<E extends ValuedEnum<V>, V> implements AttributeConverter<E, V> {

    private final Class<E> enumClazz;

    @SuppressWarnings("unchecked")
    public ValuedEnumConverter() {
        enumClazz = GenericsUtils.getSuperclassGenericType(getClass(), 0);
        if (enumClazz != null && enumClazz.getEnumConstants() == null) {
            throw new IllegalArgumentException("ValuedEnum should be a type of Enum! - " + enumClazz.getSimpleName());
        }
    }

    @Override
    public V convertToDatabaseColumn(E e) {
        return e.getValue();
    }

    @Override
    public E convertToEntityAttribute(V v) {
        for (E e : enumClazz.getEnumConstants()) {
            if (e.getValue().equals(v)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown enum value! - " + v.toString());
    }

}
