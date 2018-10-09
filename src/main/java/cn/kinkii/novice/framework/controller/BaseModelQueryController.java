package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.exception.InternalServiceException;
import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Valid
public abstract class BaseModelQueryController<E extends Identifiable<ID>, ID extends Serializable> extends BaseModelController<E, ID> {

    protected Boolean canQueryAll() {
        return true;
    }

    protected void handleGet(Principal principal) {
        // Do nothing...
    }

    protected void handleQueryAll(Principal principal) {
        // Do nothing...
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public E get(@PathVariable("id") ID id, Principal principal) {
        try {
            handleGet(principal);
            return ((Optional<E>)invokeMethods("findById", id)).get();
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.ERROR_SERVICE.getMessageKey()));
        }

    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    protected List<E> all(Principal principal) {
        if (!canQueryAll()) {
            return null;
        }
        try {
            handleQueryAll(principal);
            return (List<E>) invokeMethods("findAll");
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.ERROR_SERVICE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/all/page", method = RequestMethod.GET)
    @ResponseBody
    protected Page<E> allByPage(Pageable pageable, Principal principal) {
        if (!canQueryAll()) {
            return null;
        }
        try {
            handleQueryAll(principal);
            return (Page<E>) invokeMethods("findAll", pageable);
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.ERROR_SERVICE.getMessageKey()));
        }
    }

}
