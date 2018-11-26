package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.exception.InternalServiceException;
import cn.kinkii.novice.framework.controller.exception.InvalidParamException;
import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;

@Valid
public abstract class BaseModelCRUDController<E extends Identifiable<ID>, ID extends Serializable> extends BaseModelController<E, ID> {

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

    protected abstract E handleCreate(E model, Principal principal, HttpServletRequest request);

    protected abstract E handleUpdate(E model, Principal principal, HttpServletRequest request);

    protected abstract E handlePatch(E model, Principal principal, HttpServletRequest request);

    protected abstract void handleDelete(ID id, Principal principal, HttpServletRequest request);

    protected abstract void handleBatchDelete(List<ID> ids, Principal principal, HttpServletRequest request);

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult create(E model, Principal principal, HttpServletRequest request) {
        if (!canCreate(principal)) {
            return null;
        }
        E newModel = handleCreate(model, principal, request);
        try {
            invokeMethods("create", new Class[]{Identifiable.class}, null, newModel);
            return BaseResult.success(getMessage(GlobalMessage.CREATE_SUCCESS.getMessageKey())).addValue("id", newModel.getId());
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.CREATE_FAILURE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public BaseResult update(@PathVariable("id") ID id, E model, Principal principal, HttpServletRequest request) {
        if (!canUpdate(principal)) {
            return null;
        }
        if (!(Boolean) invokeMethods("existsById", new Class[]{Object.class}, boolean.class, id)) {
            throw new InvalidParamException(getMessage(GlobalMessage.UPDATE_FAILURE_NOT_EXISTED.getMessageKey()));
        }

        model.setId(id);
        E updatingModel = handleUpdate(model, principal, request);
        try {
            invokeMethods("update", new Class[]{Identifiable.class}, null, updatingModel);
            return BaseResult.success(getMessage(GlobalMessage.UPDATE_SUCCESS.getMessageKey()));
        } catch (Exception ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.UPDATE_FAILURE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public BaseResult patch(@PathVariable("id") ID id, Principal principal, E modelData, HttpServletRequest request) {
        if (!canPatch(principal)) {
            return null;
        }
        if (!(Boolean) invokeMethods("existsById", new Class[]{Object.class}, boolean.class, id)) {
            throw new InvalidParamException(getMessage(GlobalMessage.UPDATE_FAILURE_NOT_EXISTED.getMessageKey()));
        }
        modelData.setId(id);
        E patchData = handlePatch(modelData, principal, request);
        try {
            invokeMethods("patch", new Class[]{Identifiable.class}, null, patchData);
            return BaseResult.success(getMessage(GlobalMessage.UPDATE_SUCCESS.getMessageKey()));
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.UPDATE_FAILURE.getMessageKey()));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
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
            return BaseResult.success(getMessage(GlobalMessage.DELETE_SUCCESS.getMessageKey()));
        } catch (RuntimeException ignored) {
            throw new InternalServiceException(getMessage(GlobalMessage.DELETE_FAILURE.getMessageKey()));
        }
    }


    @RequestMapping(value = "/batchdelete", method = RequestMethod.DELETE)
    @ResponseBody
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
                return BaseResult.success(getMessage(GlobalMessage.BATCHDELETE_SUCCESS.getMessageKey()));
            } catch (RuntimeException ignored) {
                throw new InternalServiceException(getMessage(GlobalMessage.BATCHDELETE_FAILURE.getMessageKey()));
            }
        }
        throw new InvalidParamException(getMessage(GlobalMessage.BATCHDELETE_FAILURE.getMessageKey()));
    }


}
