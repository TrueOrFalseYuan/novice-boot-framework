package cn.kinkii.novice.framework.entity;


import java.io.Serializable;
import java.util.List;

public interface AssemblyNode<T extends Serializable> {

    T getSelfId();

    T getParentId();

    void setChildren(List<? extends AssemblyNode<T>> children);
}
