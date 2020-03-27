package cn.kinkii.novice.framework.utils;

import cn.kinkii.novice.framework.entity.AssemblyNode;
import com.google.common.collect.Lists;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class KAssembleUtils<E extends AssemblyNode<T>, T extends Serializable> {

    public Set<E> assemble(Set<E> nodes) {
        Set<E> result = new LinkedHashSet<>();
        for (E node : nodes) {
            if (!StringUtils.hasText(node.getParentId().toString())) {
                Set<E> children = assemble(nodes, node.getSelfId());
                if (children != null && children.size() > 0) {
                    node.setChildren(Lists.newArrayList(children));
                }
                result.add(node);
            }
        }
        return result;
    }

    private Set<E> assemble(Set<E> nodes, T parentId) {
        Set<E> result = new LinkedHashSet<>();
        for (E node : nodes) {
            if (StringUtils.hasText(node.getParentId().toString()) && parentId.equals(node.getParentId())) {
                Set<E> children = assemble(nodes, node.getSelfId());
                if (children != null && children.size() > 0) {
                    node.setChildren(Lists.newArrayList(children));
                }
                result.add(node);
            }
        }
        return result;
    }
}
