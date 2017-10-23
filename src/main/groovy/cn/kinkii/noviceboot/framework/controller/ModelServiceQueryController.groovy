package cn.kinkii.noviceboot.framework.controller

import cn.kinkii.noviceboot.framework.entity.Identifiable
import org.apache.commons.lang3.time.DateUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Method
import java.lang.reflect.Parameter

abstract class ModelServiceQueryController<E extends Identifiable, ID extends Serializable> extends BaseModelQueryController<E, ID> {

    protected List<String> getInvokable() {
        return null
    }

    protected Boolean isInvokable(String methodName) {
        return getInvokable() == null || getInvokable()?.any { invokableServiceName -> (methodName == invokableServiceName) }
    }

    /**
     * 根据传入的method顺序查找Service -> Repository 中的相应方法进行查询
     *
     * @param methodName
     * @param request
     *
     * @return List < E >
     */
    @RequestMapping(value = "/query/{methodName}")
    @ResponseBody
    protected List<E> query(@PathVariable String methodName, HttpServletRequest request) {
        if (isInvokable(methodName)) {
            def findMethod = { Method method ->
                method.declaredAnnotations
                method.name == methodName && method.returnType.isAssignableFrom(List.class) && !hasPageableParams(method)
            }

            def service = getService()
            if (service?.respondsTo(methodName)) {
                Method method = service.class.declaredMethods.find(findMethod)
                if (method) {
                    return service.invokeMethod(methodName, parseMethodParams(method.parameters, request)) as List<E>
                }
            }

            def repository = getRepository()
            if (repository?.respondsTo(methodName)) {
                Method method = repository.class.declaredMethods.find(findMethod)
                if (method) {
                    return repository.invokeMethod(methodName, parseMethodParams(method.parameters, request)) as List<E>
                }
            }
            throw new IllegalArgumentException("The method ${methodName} is illegal for public service!Please check the parameter type and the return type.")
        } else {
            throw new IllegalArgumentException("The method ${methodName} dosen't provide public service!")
        }
    }

    /**
     * 根据传入的method顺序查找Service -> Repository中的方法进行分页查询
     *
     * @param method
     * @param request
     *
     * @return List < E >
     */
    @RequestMapping(value = "/query/{methodName}/page")
    @ResponseBody
    protected Page<E> queryPage(
            @PathVariable String methodName,
            @PageableDefault(value = 15, sort = ["id"], direction = Sort.Direction.DESC) Pageable pageable, HttpServletRequest request) {
        if (isInvokable(methodName)) {

            def findMethod = { Method method ->
                method.name == methodName && method.returnType.isAssignableFrom(Page.class) && hasPageableParams(method)
            }

            def service = getService()
            if (service?.respondsTo(methodName)) {
                Method method = service.class.declaredMethods.find(findMethod)
                if (method) {
                    return service.invokeMethod(methodName, parseMethodParams(method.parameters, pageable, request)) as Page<E>
                }
            }

            def repository = getRepository()
            if (repository.respondsTo(methodName)) {
                Method method = repository.class.declaredMethods.find(findMethod)
                if (method) {
                    return repository.invokeMethod(methodName, parseMethodParams(method.parameters, pageable, request)) as Page<E>
                }
            }
            throw new IllegalArgumentException("The method ${methodName} is illegal for public service!Please check the parameter type and the return type.")
        } else {
            throw new IllegalArgumentException("The method ${methodName} isn't a public service!")
        }
    }


    protected static Object parseCustomParam(String paramName, String paramValue) {
        throw new IllegalArgumentException("Unsupported parameter type - ${paramName}/${paramValue}")
    }

    protected static Boolean hasPageableParams(Method method) {
        return method.parameters.any {
            param -> param.type == Pageable
        }
    }

    protected static parseMethodParams(Parameter[] parameters, HttpServletRequest request) {
        return parseMethodParams(parameters, null, request)
    }

    protected static parseMethodParams(Parameter[] parameters, Pageable page, HttpServletRequest request) {
        def params = parameters.collect { Parameter param ->
            Class<?> _type = param.type
            if (_type == Pageable.class) {
                if (page) {
                    return page
                } else {
                    throw new IllegalArgumentException("Pageable Object can't be null for paged query!")
                }
            } else {
                def value = request.getParameter(param.name)
                if (value) {
                    if (_type == String.class) {
                        return value
                    } else if (_type.isAssignableFrom(Number.class)) {
                        try {
                            // Byte / Integer / Long / Float / Double
                            return _type."parse${_type.simpleName}"(value)
                        } catch (ignored) {
                            throw new IllegalArgumentException("Unsupported param type or illegal value for the parameter ${param.name}")
                        }
                    } else if (_type == Date.class) {
                        Date dateValue = null
                        try {
                            dateValue = DateUtils.parseDate(value, DEFAULT_PARAM_DATE_FORMATS)
                            dateValue = new Date(Long.parseLong(value))
                        } catch (ignored) {
                        }
                        if (dateValue) {
                            return dateValue
                        } else {
                            throw new IllegalArgumentException("Illegal date value for the parameter ${param.name}")
                        }
                    } else {
                        return parseCustomParam(param.name, value)
                    }
                } else {
                    throw new IllegalArgumentException("Parameter ${param.name} is null")
                }
            }
        }
        return params.toArray()
    }

    final DEFAULT_PARAM_DATE_FORMATS = ["yyyy/MM/dd HH:mm", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy-MM-dd"] as String[]

}
