package cn.kinkii.noviceboot.framework.controller

import cn.kinkii.noviceboot.framework.controller.exception.CheckedServiceException
import cn.kinkii.noviceboot.framework.controller.exception.InternalServiceException
import cn.kinkii.noviceboot.framework.controller.exception.InvalidParamException
import cn.kinkii.noviceboot.framework.entity.Identifiable
import cn.kinkii.noviceboot.framework.service.exception.ServiceException
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest

abstract class BaseModelCRUDController<E extends Identifiable, ID extends Serializable> extends BaseModelController<E, ID> {

    /**
     * 添加不同业务实体前自定义的数据处理,多用于数据完整性、合法性校验. 如果校验不通过则根据校验数据抛出WebException.
     *
     * @param model 业务实体
     * @param request HttpServletRequest获取其他非自动装配参数
     * @return E 经过处理的业务实体对象
     */
    protected abstract E handleCreateData(E model, HttpServletRequest request)

    /**
     * 编辑不同业务实体前自定义的数据处理,多用于数据完整性、合法性校验.
     *
     * @param model 业务实体
     * @param request HttpServletRequest获取其他非自动装配参数
     * @return E 经过处理的业务实体对象
     */
    protected abstract E handleUpdateData(E model, HttpServletRequest request)

    protected abstract E handlePatchData(E model, HttpServletRequest request)

    /**
     * 删除业务实体前自定义的数据处理,多用于数据完整性、合法性校验.
     *
     * @param id 业务实体 id
     * @param request HttpServletRequest获取其他非自动装配参数
     */
    protected abstract void handleDeleteData(ID id, HttpServletRequest request)

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    BaseResult create(E model, HttpServletRequest request) {
        E newModel = handleCreateData(model, request)
        try {
            invokeMethods("create", newModel)
            return BaseResult.success(getMessage(GlobalMessage.CREATE_SUCCESS.getMessageKey())).addValue("id", newModel.getId())
        } catch (ServiceException se) {
            // exception from customized ModelService
            throw new CheckedServiceException(se.getCode())
        } catch (RuntimeException re) {
            // exception from JPA
            re.printStackTrace()
            throw new InternalServiceException(getMessage(GlobalMessage.CREATE_FAILURE.getMessageKey()))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    BaseResult update(@PathVariable("id") ID id, E model, HttpServletRequest request) {
        if (invokeMethod("exists",id) as Boolean) {
            throw new InvalidParamException(getMessage(GlobalMessage.UPDATE_FAILURE_NOT_EXISTED.getMessageKey()))
        }
        E updatingModel = handleUpdateData(model, request)
        try {
            invokeMethods("update", updatingModel)
            return BaseResult.success(getMessage(GlobalMessage.UPDATE_SUCCESS.getMessageKey()))
        } catch (ServiceException se) {
            throw new CheckedServiceException(se.getCode())
        } catch (RuntimeException re) {
            re.printStackTrace()
            throw new InternalServiceException(getMessage(GlobalMessage.UPDATE_FAILURE.getMessageKey()))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    BaseResult patch(@PathVariable("id") ID id, E modelData, HttpServletRequest request) {
        if (invokeMethod("exists",id) as Boolean) {
            throw new InvalidParamException(getMessage(GlobalMessage.UPDATE_FAILURE_NOT_EXISTED.getMessageKey()))
        }
        E updatingData = handlePatchData(modelData, request)
        try {
            invokeMethods("patch", updatingData)
            return BaseResult.success(getMessage(GlobalMessage.UPDATE_SUCCESS.getMessageKey()))
        } catch (ServiceException se) {
            throw new CheckedServiceException(se.getCode())
        } catch (RuntimeException re) {
            re.printStackTrace()
            throw new InternalServiceException(getMessage(GlobalMessage.UPDATE_FAILURE.getMessageKey()))
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    BaseResult delete(@PathVariable ID id, HttpServletRequest request) {
        if (invokeMethod("exists",id) as Boolean) {
            throw new InvalidParamException(getMessage(GlobalMessage.DELETE_FAILURE_NOT_EXISTED.getMessageKey()))
        }
        handleDeleteData(id, request)
        try {
            invokeMethods("delete", id)
            return BaseResult.success(getMessage(GlobalMessage.DELETE_SUCCESS.getMessageKey()))
        } catch (ServiceException se) {
            throw new CheckedServiceException(se.getCode())
        } catch (RuntimeException re) {
            re.printStackTrace()
            throw new InternalServiceException(getMessage(GlobalMessage.DELETE_FAILURE.getMessageKey()))
        }
    }

}
