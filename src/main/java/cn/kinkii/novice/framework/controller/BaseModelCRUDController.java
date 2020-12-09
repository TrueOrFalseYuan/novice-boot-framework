package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.entity.Identifiable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.Serializable;
import java.security.Principal;

@Valid
public abstract class BaseModelCRUDController<E extends Identifiable<ID>, ID extends Serializable> extends BaseModelRequestCRUDController<E, ID, E> {

    protected E handleUpdate(E model, Principal principal, HttpServletRequest request) {
        return model;
    }

    protected E handlePatch(E model, Principal principal, HttpServletRequest request) {
        return model;
    }

    @Override
    protected E handleUpdate(ID id, E model, Principal principal, HttpServletRequest request) {
        model.setId(id);
        return handleUpdate(model, principal, request);
    }

    @Override
    protected E handlePatch(ID id, E model, Principal principal, HttpServletRequest request) {
        model.setId(id);
        return handlePatch(model, principal, request);
    }

    @Override
    protected E toCreateModel(E model, Principal principal) {
        return model;
    }

    @Override
    protected E toUpdateModel(E model, Principal principal) {
        return model;
    }

    @Override
    protected E toPatchModel(E model, Principal principal) {
        return model;
    }
}
