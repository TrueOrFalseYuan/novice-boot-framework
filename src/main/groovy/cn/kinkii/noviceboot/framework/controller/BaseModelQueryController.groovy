package cn.kinkii.noviceboot.framework.controller

import cn.kinkii.noviceboot.framework.controller.exception.InternalServiceException
import cn.kinkii.noviceboot.framework.entity.Identifiable
import org.springframework.web.bind.annotation.RequestMethod


import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


abstract class BaseModelQueryController<E extends Identifiable, ID extends Serializable> extends BaseModelController<E, ID> {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    E get(@PathVariable("id") ID id) {
        try {
            return (E) getRepository().findOne(id)
        } catch (RuntimeException re) {
            re.printStackTrace()
            throw new InternalServiceException(getMessage(GlobalMessage.ERROR_SERVICE.getMessageKey()))
        }

    }

    /**
     * 查询所有对象实体.
     *
     * @return List < E >  所有对象实体
     */
    @RequestMapping(value = "/all")
    @ResponseBody
    protected List<E> findAll() {
        return getRepository().findAll()
    }

    /**
     * 分页查找对象实体
     * @param pageable ： spring 默认 pageable，可覆盖PageableHandlerMethodArgumentResolver配置来更改默认配置
     * @return
     */
    @RequestMapping(value = "/all/page")
    @ResponseBody
    protected Page<E> findPage(Pageable pageable) {
        return getRepository().findAll(pageable)
    }

}
