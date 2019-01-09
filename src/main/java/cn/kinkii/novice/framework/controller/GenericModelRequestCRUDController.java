package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.request.GenericRequest;
import cn.kinkii.novice.framework.entity.Identifiable;

import javax.validation.Valid;
import java.io.Serializable;
import java.security.Principal;

@Valid
public abstract class GenericModelRequestCRUDController<E extends Identifiable<ID>, ID extends Serializable, R extends GenericRequest<E>>
        extends BaseModelRequestCRUDController<E, ID, R> {

    @Override
    protected E toCreateModel(R modelRequest, Principal principal) {
        return modelRequest.newTarget();
    }

    @Override
    protected E toUpdateModel(R modelRequest, Principal principal) {
        return modelRequest.newTarget();
    }

    @Override
    protected E toPatchModel(R modelRequest, Principal principal) {
        return modelRequest.newTarget();
    }
}
