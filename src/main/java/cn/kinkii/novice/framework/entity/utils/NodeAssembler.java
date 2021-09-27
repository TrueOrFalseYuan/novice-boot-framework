package cn.kinkii.novice.framework.entity.utils;

import cn.kinkii.novice.framework.entity.AssemblyNode;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class NodeAssembler<E extends AssemblyNode<T>, T extends Serializable> {

    public Set<E> assemble(Set<E> nodes) {
        Set<E> result = new LinkedHashSet<>();
        for (E node : nodes) {
            if (node.getParentId() == null) {
                Set<E> children = assemble(nodes, node.getSelfId());
                if (node.getIsTransparent()) {
                    result.addAll(children);
                } else {
                    if (children.size() > 0) {
                        node.setChildren(Lists.newArrayList(children));
                    }
                    result.add(node);
                }
            }
        }
        return result;
    }

    private Set<E> assemble(Set<E> nodes, T parentId) {
        Set<E> result = new LinkedHashSet<>();
        for (E node : nodes) {
            if (parentId != null && parentId.equals(node.getParentId())) {
                Set<E> children = assemble(nodes, node.getSelfId());
                if (node.getIsTransparent()) {
                    result.addAll(children);
                } else {
                    if (children.size() > 0) {
                        node.setChildren(Lists.newArrayList(children));
                    }
                    result.add(node);
                }
            }
        }
        return result;
    }
}
