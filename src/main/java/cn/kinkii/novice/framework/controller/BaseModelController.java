package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.entity.Identifiable;
import cn.kinkii.novice.framework.repository.ModelRepository;
import cn.kinkii.novice.framework.service.ModelService;
import cn.kinkii.novice.framework.utils.KGenericsUtils;
import cn.kinkii.novice.framework.utils.KReflectionUtils;
import com.google.common.collect.Lists;
import org.springframework.util.NumberUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "unused", "SameParameterValue", "WeakerAccess"})
public abstract class BaseModelController<E extends Identifiable<ID>, ID extends Serializable> extends BaseController {

    protected final Class clazz = KGenericsUtils.getSuperclassGenericType(getClass(), 0);
    protected final Class idClazz = KGenericsUtils.getSuperclassGenericType(getClass(), 1);

    protected ModelRepository<E, ID> getRepository() {
        return null;
    }

    protected ModelService<E, ID> getService() {
        return null;
    }

    private Method respondsTo(Class clazz, String method, Class[] pTypes, Class<?> returnType) {
        return KReflectionUtils.findActualMethod(clazz, method, pTypes, returnType);
    }

    protected Object invoke(Object target, String methodName, Class[] pTypes, Class<?> returnType, Object... params) {
        if (target != null) {
            Method method = respondsTo(target.getClass(), methodName, pTypes, returnType);
            if (method != null) {
                return KReflectionUtils.invokeMethod(method, target, params);
            }
        }
        throw new IllegalArgumentException("Unknown method!");
    }

    protected Object invokeMethods(String methodName, Class[] pTypes, Class<?> returnType, Object... params) {
        try {
            return invoke(getService(), methodName, pTypes, returnType, params);
        } catch (IllegalArgumentException e) {
            return invoke(getRepository(), methodName, pTypes, returnType, params);
        }
    }

    protected Object invokeMethods(String methodName, Class[] pTypes, Object... params) {
        try {
            return invoke(getService(), methodName, pTypes, null, params);
        } catch (IllegalArgumentException e) {
            return invoke(getRepository(), methodName, pTypes, null, params);
        }
    }

    protected Object invokeMethods(String methodName, Object... params) {
        return invokeMethods(methodName, new Class[]{}, null, params);
    }

    protected List<ID> parseIdString(String ids) {
        List<ID> parsedIds = Lists.newArrayList();
        if (idClazz != null) {
            List strIds = new ArrayList<>();
            for (String id : ids.split(",")) {
                strIds.add(id.trim());
            }
            if (Number.class.isAssignableFrom(idClazz)) {
                strIds.forEach(strId -> {
                            try {
                                parsedIds.add((ID) NumberUtils.parseNumber((String) strId, idClazz));
                            } catch (Exception e) {
                                throw new IllegalArgumentException("Parsing id value(${strId}) failed! ");
                            }
                        }
                );
            } else if (idClazz == String.class) {
                parsedIds.addAll(strIds);
            } else {
                throw new IllegalArgumentException("Parsing id string failed, please override the parseIdString() - unsupported class type [" + idClazz.getName() + "]");
            }
        }
        return parsedIds;
    }

}
