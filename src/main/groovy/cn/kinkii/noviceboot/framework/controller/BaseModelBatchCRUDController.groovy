package cn.kinkii.noviceboot.framework.controller

import com.google.common.collect.Lists
import cn.kinkii.noviceboot.framework.controller.exception.InternalServiceException
import cn.kinkii.noviceboot.framework.controller.exception.InvalidParamException
import cn.kinkii.noviceboot.framework.entity.Identifiable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest

/**
 * 批量管理Controller
 *
 * @param < E >  Model
 * @param < ID >  主键类型
 */
abstract class BaseModelBatchCRUDController<E extends Identifiable, ID extends Serializable> extends BaseModelCRUDController<E, ID> {

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    BaseResult batchCreate() {
        // TODO
        return null
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PUT)
    @ResponseBody
    BaseResult batchUpdate() {
        // TODO
        return null
    }

    @RequestMapping(value = "/batch", method = RequestMethod.PATCH)
    @ResponseBody
    BaseResult batchPatch() {
        // TODO
        return null
    }

    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    @ResponseBody
    BaseResult batchDelete(String ids, HttpServletRequest request) {
        List<ID> parsedIds
        try {
            parsedIds = parseIdString(ids)
        } catch (ignored) {
            throw new InvalidParamException(getMessage(GlobalMessage.ERROR_PARAMETER.getMessageKey()))
        }


        def deleteModels = []
        parsedIds.each { id ->
            try {
                handleDeleteData(id, request)
                E model = clazz.newInstance() as E
                model.setId(id)
                deleteModels.add(model)
            } catch (ignored) {
            }
        }
        if (!deleteModels.isEmpty()) {
            try {
                invokeMethods("deleteInBatch", deleteModels)
                return BaseResult.success(getMessage(GlobalMessage.BATCHDELETE_SUCCESS.getMessageKey()))
            } catch (RuntimeException re) {
                re.printStackTrace()
                throw new InternalServiceException(getMessage(GlobalMessage.BATCHDELETE_FAILURE.getMessageKey()))
            }
        }
        throw new InvalidParamException(getMessage(GlobalMessage.BATCHDELETE_FAILURE.getMessageKey()))
    }

    protected List<ID> parseIdString(String ids) {
        List<ID> parsedIds = Lists.newArrayList()

        def strIds = (ids.split(",")*.trim())
        if (idClazz.isAssignableFrom(Number.class)) {
            strIds.each { strId ->
                try {
                    // Byte / Integer / Long / Float / Double
                    parsedIds.add(idClazz."parse${idClazz.simpleName}"(strId) as ID)
                } catch (ignored) {
                    throw new IllegalArgumentException("Parsing id value(${strId}) failed! ")
                }
            }
        } else if (idClazz == String.class) {
            strIds.each { strId ->
                parsedIds.add(strId as ID)
            }
        } else {
            throw new IllegalArgumentException("Parsing id string failed, please override the parseIdString() - unsupported classtype [" + idClazz.getName() + "]")
        }
        return parsedIds
    }

}
