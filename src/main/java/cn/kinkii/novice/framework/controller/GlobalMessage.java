package cn.kinkii.novice.framework.controller;


public enum GlobalMessage {

    SUCCESS("global.success"),
    FAILURE("global.failure"),
    FAILURE_NOT_EXISTED("global.failure.notexisted"),

    ERROR_DATA("global.error.data"),
    ERROR_SERVICE("global.error.service"),
    ERROR_PARAMETER("global.error.parameter"),
    ERROR_PERMISSION("global.error.permission"),

    CREATE_SUCCESS("global.create.success"),
    CREATE_FAILURE("global.create.failure"),
    CREATE_FAILURE_EXISTED("global.create.failure.existed"),

    UPDATE_SUCCESS("global.update.success"),
    UPDATE_FAILURE("global.update.failure"),
    UPDATE_FAILURE_EXISTED("global.update.failure.existed"),
    UPDATE_FAILURE_NOT_EXISTED("global.update.failure.notexisted"),

    DELETE_SUCCESS("global.delete.success"),
    DELETE_FAILURE("global.delete.failure"),
    DELETE_FAILURE_NOT_EXISTED("global.delete.failure.notexisted"),
    DELETE_FAILURE_HAS_BOUND("global.delete.failure.hasbound"),

    BATCHDELETE_SUCCESS("global.batchdelete.success"),
    BATCHDELETE_FAILURE("global.batchdelete.failure");

    private String messageKey;

    GlobalMessage(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
