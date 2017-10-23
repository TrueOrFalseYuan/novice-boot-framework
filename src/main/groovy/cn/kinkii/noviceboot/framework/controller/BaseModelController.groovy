package cn.kinkii.noviceboot.framework.controller

import cn.kinkii.noviceboot.framework.controller.BaseController
import cn.kinkii.noviceboot.framework.entity.Identifiable
import cn.kinkii.noviceboot.framework.repository.ModelRepository
import cn.kinkii.noviceboot.framework.service.ModelService

import static cn.kinkii.noviceboot.framework.utils.GenericsUtils.getSuperclassGenericType

abstract class BaseModelController<E extends Identifiable, ID extends Serializable> extends BaseController {

    protected final Class clazz = getSuperclassGenericType(getClass(), 0)
    protected final Class idClazz = getSuperclassGenericType(getClass(), 1)

    protected ModelRepository<E, ID> getRepository() {
        return null
    }

    protected ModelService<E, ID> getService() {
        return null
    }

    protected invokeMethods(String method, Object params) {
        def service = getService()
        if (service?.respondsTo(method)) {
            return service.invokeMethod(method, params)
        }

        def repository = getRepository()
        if (repository?.respondsTo(method)) {
            return repository.invokeMethod(method, params)
        }
        throw new IllegalArgumentException("Unknown method!")
    }
}
