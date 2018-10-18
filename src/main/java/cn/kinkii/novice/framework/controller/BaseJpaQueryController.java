package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.query.jpa.JpaQuery;
import cn.kinkii.novice.framework.controller.query.jpa.JpaQuerySpecification;
import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;

@SuppressWarnings({"unchecked", "WeakerAccess"})
@Valid
public abstract class BaseJpaQueryController<E extends Identifiable<ID>, ID extends Serializable, Q extends JpaQuery<E>> extends BaseModelController<E, ID> {

    protected void handleQuery(Q query, Principal principal) {
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    protected List<E> query(@RequestBody Q query, Principal principal) {
        handleQuery(query, principal);
        return (List<E>) invoke(getRepository(), "findAll", new Class[]{Specification.class}, List.class, new JpaQuerySpecification(query));
    }

    @RequestMapping(value = "/query/page", method = RequestMethod.POST)
    @ResponseBody
    protected Page<E> queryByPage(@RequestBody Q query, Pageable pageable, Principal principal) {
        handleQuery(query, principal);
        return (Page<E>) invoke(getRepository(), "findAll", new Class[]{Specification.class, Pageable.class}, List.class, new Object[]{new JpaQuerySpecification(query), pageable});
    }
}
