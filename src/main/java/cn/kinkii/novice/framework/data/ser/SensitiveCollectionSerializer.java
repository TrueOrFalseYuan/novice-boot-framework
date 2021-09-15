package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.databind.BeanProperty;

import java.util.Collection;
import java.util.Objects;

public abstract class SensitiveCollectionSerializer<T> extends SensitiveArraySerializer<Collection<T>> implements SensitiveTypeSerializer<Collection<T>> {

    protected final Class<T> elementType;

    protected SensitiveCollectionSerializer(Class<T> elementType) {
        super(Collection.class);
        this.elementType = elementType;
    }

    @Override
    public boolean isSupport(BeanProperty property) {
        return property.getType().isCollectionLikeType() && Objects.equals(property.getType().getContentType().getRawClass(), this.elementType);
    }


}