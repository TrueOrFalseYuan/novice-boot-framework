package cn.kinkii.novice.framework.controller.query;

import org.springframework.data.domain.Sort;

public enum Direction {
    
    DESC, ASC;

    public boolean isAscending() {
        return this.equals(ASC);
    }

    public boolean isDescending() {
        return this.equals(DESC);
    }

    public Sort.Direction toDirection() {
        return this.equals(ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

}
