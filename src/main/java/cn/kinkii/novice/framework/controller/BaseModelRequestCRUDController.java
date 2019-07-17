package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.aop.NoRepeatSubmit;
import cn.kinkii.novice.framework.controller.exception.InternalServiceException;
import cn.kinkii.novice.framework.controller.exception.InvalidParamException;
import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;

@Valid
public abstract class BaseModelRequestCRUDController<E extends Identifiable<ID>, ID extends Serializable, R> extends BaseModelController<E, ID> {

    protected Boolean canCreate(Principal principal) {
        return true;
    }

    protected Boolean canUpdate(Principal principal) {
        return true;
    }

    protected Boolean canPatch(Principal principal) {
        return true;
    }

    protected Boolean canDelete(Principal principal) {
        return true;
    }

    protected Boolean canBatchDelete(Principal principal) {
        return true;
    }

    protected R handleCreate(R modelRequest, Principal principal, HttpServletRequest request) {
        return modelRequest;
    }

    protected R handleUpdate(ID id, R modelRequest, Principal principal, HttpServletRequest request) {
        return modelRequest;
    }

    protected R handlePatch(ID id, R modelRequest, Principal principal, HttpServletRequest request) {
        return modelRequest;
    }

    protected void handleDelete(ID id, Principal principal, HttpServletRequest request) {

    }

    protected void handleBatchDelete(List<ID> ids, Principal principal, HttpServletRequest request) {

    }

    protected void handleAfterCreate(E model, Principal principal) {
        // Do nothing...
    }

    protected void handleAfterUpdate(E model, Principal principal) {
        // Do nothing...
    }

    protected void handleAfterPatch(E model, Principal principal) {
        // Do nothing...
    }
    protected void handleAfterDelete(ID id, Principal principal) {
        // Do nothing...
    }

    protected void handleAfterBatchDelete(List<ID> ids, Principal principal) {
        // Do nothing...
    }

    protected abstract E toCreateModel(R modelRequest, Principal principal);

    protected abstract E toUpdateModel(R modelRequest, Principal principal);

    protected abstract E toPatchModel(R modelRequest, Principal principal);

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    @NoRepeatSubmit
    public BaseResult create(@Valid R modelRequest, Principal principal, HttpServletRequest request) {
        if (!canCreate(principal)) {
            return null;
        }
        R handledModelRequest = handleCreate(modelRequest, principal, request);
        E newModel = toCreateModel(handledModelRequest, principal);
        try {
            invokeMethods("create", new Class[]{Identifiable.class}, null, newModel);
            handleAfterCreate(newModel, principal);
            return BaseResult.success(getMessage(GlobalMessage.CREATE_SUCCESS.getMessageKey())).addValue("id", newModel.getId());
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.CREATE_FAILURE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    @NoRepeatSubmit
    public BaseResult updateWithPost(@PathVariable("id") ID id, @Valid R modelRequest, Principal principal, HttpServletRequest request) {
        return update(id, modelRequest, principal, request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    @NoRepeatSubmit
    public BaseResult update(@PathVariable("id") ID id, @Valid R modelRequest, Principal principal, HttpServletRequest request) {
        if (!canUpdate(principal)) {
            return null;
        }
        if (!(Boolean) invokeMethods("existsById", new Class[]{Object.class}, boolean.class, id)) {
            throw new InvalidParamException(getMessage(GlobalMessage.UPDATE_FAILURE_NOT_EXISTED.getMessageKey()));
        }
        R handledModelRequest = handleUpdate(id, modelRequest, principal, request);
        E updatingModel = toUpdateModel(handledModelRequest, principal);
        updatingModel.setId(id);
        try {
            invokeMethods("update", new Class[]{Identifiable.class}, null, updatingModel);
            handleAfterUpdate(updatingModel, principal);
            return BaseResult.success(getMessage(GlobalMessage.UPDATE_SUCCESS.getMessageKey()));
        } catch (Exception ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.UPDATE_FAILURE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/{id}/patch", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    @NoRepeatSubmit
    public BaseResult patchWithPost(@PathVariable("id") ID id, @Valid R modelRequest, Principal principal, HttpServletRequest request) {
        return patch(id, modelRequest, principal, request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    @Transactional
    @NoRepeatSubmit
    public BaseResult patch(@PathVariable("id") ID id, @Valid R modelRequest, Principal principal, HttpServletRequest request) {
        if (!canPatch(principal)) {
            return null;
        }
        if (!(Boolean) invokeMethods("existsById", new Class[]{Object.class}, boolean.class, id)) {
            throw new InvalidParamException(getMessage(GlobalMessage.UPDATE_FAILURE_NOT_EXISTED.getMessageKey()));
        }
        R handledModelRequest = handlePatch(id, modelRequest, principal, request);
        E patchModel = toPatchModel(handledModelRequest, principal);
        patchModel.setId(id);
        try {
            invokeMethods("patch", new Class[]{Identifiable.class}, null, patchModel);
            handleAfterPatch(patchModel, principal);
            return BaseResult.success(getMessage(GlobalMessage.UPDATE_SUCCESS.getMessageKey()));
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.UPDATE_FAILURE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    @NoRepeatSubmit
    public BaseResult delete(@PathVariable ID id, Principal principal, HttpServletRequest request) {
        if (!canDelete(principal)) {
            return null;
        }
        if (!(Boolean) invokeMethods("existsById", new Class[]{Object.class}, boolean.class, id)) {
            throw new InvalidParamException(getMessage(GlobalMessage.DELETE_FAILURE_NOT_EXISTED.getMessageKey()));
        }
        handleDelete(id, principal, request);
        try {
            invokeMethods("deleteById", new Class[]{Object.class}, null, id);
            handleAfterDelete(id, principal);
            return BaseResult.success(getMessage(GlobalMessage.DELETE_SUCCESS.getMessageKey()));
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.DELETE_FAILURE.getMessageKey()));
        }
    }


    @RequestMapping(value = "/batchdelete", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    @NoRepeatSubmit
    public BaseResult batchDelete(String ids, Principal principal, HttpServletRequest request) {
        if (!canBatchDelete(principal)) {
            return null;
        }
        List<ID> parsedIds;
        try {
            parsedIds = parseIdString(ids);
        } catch (Exception e) {
            throw new InvalidParamException(getMessage(GlobalMessage.ERROR_PARAMETER.getMessageKey()));
        }

        handleBatchDelete(parsedIds, principal, request);
        if (!parsedIds.isEmpty()) {
            try {
                invokeMethods("deleteInBatchById", new Class[]{Iterable.class}, null, parsedIds);
                handleAfterBatchDelete(parsedIds, principal);
                return BaseResult.success(getMessage(GlobalMessage.BATCHDELETE_SUCCESS.getMessageKey()));
            } catch (RuntimeException ignored) {
                throw new InternalServiceException(getMessage(GlobalMessage.BATCHDELETE_FAILURE.getMessageKey()));
            }
        }
        throw new InvalidParamException(getMessage(GlobalMessage.BATCHDELETE_FAILURE.getMessageKey()));
    }


}
