package cn.kinkii.novice.framework.controller;

public class GlobalRegex {

    public static final String REGEX_MOBILE = "^1\\d{10}$";
    public static final String REGEX_PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";

}
