package cn.kinkii.novice.framework.controller.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class RequestUtils {

  private static final String USER_AGENT_HEADER = "User-Agent";
  private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
  private static final String X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto";
  private static final List<String> CUSTOM_IP_HEADERS = Arrays.asList("X-Real-Ip", "Proxy-Client-Ip", "Http-Client-Ip");
  private static final String UNKNOWN_HEADER_VALUE = "unknown";

  public static HttpServletRequest getRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
    throw new IllegalStateException("Failed to get the request!");
  }

  private static boolean isHeaderValued(String headerValue) {
    return !(headerValue == null || headerValue.length() == 0 || UNKNOWN_HEADER_VALUE.equalsIgnoreCase(headerValue));
  }


  public static String getIpAddress() {
    HttpServletRequest request = getRequest();
    String ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
    if (isHeaderValued(ipAddress)) {
      return ipAddress.split(",")[0].trim();
    } else {
      for (String header : CUSTOM_IP_HEADERS) {
        ipAddress = request.getHeader(header);
        if (isHeaderValued(ipAddress)) {
          return ipAddress;
        }
      }
    }
    return request.getRemoteAddr();
  }

  public static String getUserAgent() {
    HttpServletRequest request = getRequest();
    return request.getHeader(USER_AGENT_HEADER);
  }

  public static String getScheme() {
    HttpServletRequest request = getRequest();
    String scheme = request.getHeader(X_FORWARDED_PROTO_HEADER);
    if (isHeaderValued(scheme)) {
      return scheme;
    }
    return request.getScheme();
  }

  public static String getRequestPath() {
    HttpServletRequest request = getRequest();
    return (request.getQueryString() == null || request.getQueryString().trim().equals("")) ? request.getServletPath() : request.getServletPath() + "?" + request.getQueryString();
  }

  public static String getHostPath() {
    HttpServletRequest request = getRequest();
    String scheme = getScheme();
    String hostPath = scheme + "://" + request.getServerName();
    if ((scheme.equals("http") && request.getServerPort() != 80) || (scheme.equals("https") && request.getServerPort() != 443)) {
      hostPath += ":" + request.getServerPort();
    }
    return hostPath;
  }

  public static String getFullContextPath() {
    return getHostPath() + getRequest().getContextPath();
  }
}
