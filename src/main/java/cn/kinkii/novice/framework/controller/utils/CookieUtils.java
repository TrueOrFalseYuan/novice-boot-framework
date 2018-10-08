package cn.kinkii.novice.framework.controller.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CookieUtils {

  private static final int DEFAULT_COOKIE_MAX_AGE = 7 * 24 * 60 * 60; //7 days

  private static ThreadLocal<Map<String, String>> cookiesLocal = new ThreadLocal<>();

  public static void setCookie(String name, String value) {
    setCookie(name, value, DEFAULT_COOKIE_MAX_AGE);
  }

  public static void setCookie(String name, String value, int maxAge) {
    if (value == null) {
      return;
    }
    Cookie cookie = new Cookie(name.trim(), value.trim());
    cookie.setMaxAge(maxAge);
    cookie.setPath("/");
    ResponseUtils.getResponse().addCookie(cookie);
  }

  public static String getCookie(String name) {
    return getCookies().getOrDefault(name, null);
  }

  public static Map<String, String> getCookies() {
    Map<String, String> result = cookiesLocal.get();
    if (result == null) {
      result = new HashMap<>();
      HttpServletRequest request = RequestUtils.getRequest();
      Cookie[] cookies = request.getCookies();
      if (null != cookies) {
        for (Cookie cookie : cookies) {
          result.put(cookie.getName(), cookie.getValue());
        }
      }
      cookiesLocal.set(result);
    }
    return result;
  }

  public static void deleteCookie(String name) {
    setCookie(name, null, 0);
  }

}
