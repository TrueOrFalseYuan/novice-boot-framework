package cn.kinkii.novice.framework.entity;


import java.io.Serializable;
import java.util.List;

public interface AssemblyNode<T extends Serializable> {

    T getSelfId();

    T getParentId();

    default Boolean getIsTransparent() {
        return false;
    }

    void setChildren(List<? extends AssemblyNode<T>> children);

}
