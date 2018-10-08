package cn.kinkii.novice.framework.controller.utils;

import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtils {


  public static HttpServletResponse getResponse() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      return ((ServletRequestAttributes) requestAttributes).getResponse();
    }
    throw new IllegalStateException("Failed to get the response!");
  }

  public static void redirect(String url) {
    Assert.hasText(url, "The url to redirect can't be blank!");

    HttpServletResponse response = getResponse();
    if (response == null) {
      return;
    }
    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    response.setHeader("Location", url);
  }
}
