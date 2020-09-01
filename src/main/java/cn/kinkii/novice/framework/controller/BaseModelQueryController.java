package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.exception.InternalServiceException;
import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"unchecked", "WeakerAccess"})
@Valid
public abstract class BaseModelQueryController<E extends Identifiable<ID>, ID extends Serializable> extends BaseModelController<E, ID> {

    protected Boolean canGet() {
        return true;
    }

    protected Boolean handleGet(ID id, Principal principal) {
        // Do nothing...
        return true;
    }

    protected void handleAfterGet(E model, Principal principal) {
        // Do nothing...
    }

    protected Boolean canQueryAll() {
        return true;
    }

    protected Boolean handleQueryAll(Principal principal) {
        // Do nothing...
        return true;
    }

    protected Boolean handleQueryAllByPage(Pageable pageable, Principal principal) {
        // Do nothing...
        return handleQueryAll(principal);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public E get(@PathVariable("id") ID id, Principal principal) {
        if (!canGet()) {
            return null;
        }
        try {
            if (handleGet(id, principal)) {
                E model = ((Optional<E>) invokeMethods("findById", new Class[]{Object.class}, Optional.class, id)).orElse(null);
                if (model != null) {
                    handleAfterGet(model, principal);
                }
                return model;
            }
            return null;
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.ERROR_SERVICE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public List<E> all(Principal principal) {
        if (!canQueryAll()) {
            return null;
        }
        try {
            if (handleQueryAll(principal)) {
                return (List<E>) invokeMethods("findAll", new Class[]{}, List.class);
            }
            return Collections.emptyList();
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.ERROR_SERVICE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/all/page", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public Page<E> allByPage(Pageable pageable, Principal principal) {
        if (!canQueryAll()) {
            return null;
        }
        try {
            if (handleQueryAllByPage(pageable, principal)) {
                return (Page<E>) invokeMethods("findAll", new Class[]{Pageable.class}, Page.class, pageable);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.ERROR_SERVICE.getMessageKey()));
        }
    }

}
