package cn.kinkii.novice.framework.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface LogicalDeleteable {

    @JsonIgnore
    String getDelFlag();

    @JsonIgnore
    String getDelTimeFlag();
}
