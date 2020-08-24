package cn.kinkii.novice.framework.controller;

import cn.kinkii.novice.framework.controller.query.jpa.JpaQuery;
import cn.kinkii.novice.framework.controller.query.jpa.JpaQuerySpecification;
import cn.kinkii.novice.framework.entity.Identifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "WeakerAccess"})
@Valid
public abstract class BaseJpaQueryController<E extends Identifiable<ID>, ID extends Serializable, Q extends JpaQuery<E>> extends BaseModelQueryController<E, ID> {

    protected Boolean canQuery() {
        return true;
    }

    @SuppressWarnings("unused")
    protected Boolean handleQuery(Q query, Principal principal) {
        return true;
    }

    protected Boolean canQueryByPage() {
        return true;
    }

    @SuppressWarnings("unused")
    protected Boolean handlePageQuery(Q query, Pageable pageable, Principal principal) {
        return handleQuery(query, principal);
    }

    @RequestMapping(value = "/query", method = {RequestMethod.POST, RequestMethod.GET})
    @Transactional
    @ResponseBody
    protected List<E> query(@Valid Q query, Principal principal) {
        if (!canQuery()) {
            return null;
        }
        if (handleQuery(query, principal)) {
            return (List<E>) invoke(getRepository(), "findAll", new Class[]{Specification.class}, List.class, new JpaQuerySpecification<>(query));
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/query/page", method = {RequestMethod.POST, RequestMethod.GET})
    @Transactional
    @ResponseBody
    protected Page<E> queryByPage(@Valid Q query, Pageable pageable, Principal principal) {
        if (!canQueryByPage()) {
            return null;
        }
        if (handlePageQuery(query, pageable, principal)) {
            if (pageable.getSort().iterator().hasNext()) {
                query.setIsSortByAnnotation(false);
            }
            return (Page<E>) invoke(getRepository(), "findAll", new Class[]{Specification.class, Pageable.class}, Page.class, new Object[]{new JpaQuerySpecification<>(query), pageable});
        } else {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    @RequestMapping(value = "/count", method = {RequestMethod.POST, RequestMethod.GET})
    @Transactional
    @ResponseBody
    protected Long count(@Valid Q query, Principal principal) {
        handleQuery(query, principal);
        return (long) invoke(getRepository(), "count", new Class[]{Specification.class}, new JpaQuerySpecification<>(query));
    }

}
