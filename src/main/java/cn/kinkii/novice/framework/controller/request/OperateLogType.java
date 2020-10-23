package cn.kinkii.novice.framework.controller.request;

import lombok.Getter;

public enum OperateLogType {
    ADD("add"), UPDATE("update"), DELETE("delete"), QUERY("query"),
    LOGIN("login"), LOGOUT("logout"), IMPORT("import"), EXPORT("export");

    @Getter
    private final String value;

    OperateLogType(String value) {
        this.value = value;
    }
}
