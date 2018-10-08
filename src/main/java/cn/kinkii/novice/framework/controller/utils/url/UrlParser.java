package cn.kinkii.novice.framework.controller.utils.url;

import cn.kinkii.novice.framework.controller.utils.url.exception.InvalidUrlException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class UrlParser {

  public static UrlObject parse(String rawUrl) throws InvalidUrlException {
    return parse(rawUrl, null);
  }

  public static UrlObject parse(String rawUrl, String enc) throws InvalidUrlException {
    if (rawUrl == null || rawUrl.isEmpty()) {
      return null;
    }
    if (enc == null) {
      enc = "utf-8";
    }
    UrlObject urlObject = new UrlObject();
    urlObject.setCharset(enc);

    String remaining = rawUrl;
    int index = remaining.lastIndexOf("#");
    if (index >= 0) {
      String anchor = decode(remaining.substring(index + 1, remaining.length()), enc);
      urlObject.setAnchor(anchor == null || anchor.isEmpty() ? null : anchor);
      remaining = remaining.substring(0, index);
    }

    if (remaining.isEmpty()) {
      return urlObject;
    }

    index = remaining.indexOf("?");
    if (index >= 0) {
      String queryString = remaining.substring(index + 1, remaining.length());
      urlObject.setQueryString(queryString.isEmpty() ? null : queryString);
      remaining = remaining.substring(0, index);
    }

    index = remaining.indexOf("//");
    if (index >= 0) {
      String schema = remaining.substring(0, index);
      if (schema.charAt(schema.length() - 1) == ':') {
        urlObject.setScheme(schema.substring(0, schema.length() - 1));
      } else {
        throw new InvalidUrlException("Invalid schema! - [url: [" + rawUrl + "]]");
      }
      remaining = remaining.substring(index + 2, remaining.length());
    }


    index = remaining.lastIndexOf('@');
    if (index >= 0) {
      String credentials = remaining.substring(0, index);
      if (credentials.contains(":")) {
        String[] parts = credentials.split(":", 2);
        urlObject.setUsername(decode(parts[0], enc));
        urlObject.setPassword(decode(parts[1], enc));
      } else {
        urlObject.setUsername(decode(credentials, enc));
      }
      remaining = remaining.substring(index + 1, remaining.length());
    }
    System.out.println(remaining);

    index = remaining.indexOf('/');
    if (index >= 0) {
      String path = remaining.substring(index, remaining.length());
      urlObject.setPath(path.isEmpty() ? null : path);
      remaining = remaining.substring(0, index);
    }

    index = remaining.lastIndexOf(':');
    if (index >= 0) {
      String portString = remaining.substring(index + 1, remaining.length());
      Integer portNum;
      try {
        portNum = Integer.valueOf(portString);
      } catch (NumberFormatException e) {
        throw new InvalidUrlException("Invalid port! - [url: [" + rawUrl + "]]");
      }
      urlObject.setPort(portNum);
      remaining = remaining.substring(0, index);
    }

    if (remaining.startsWith("[")) {
      index = remaining.lastIndexOf("]");
      if (index < 0) {
        throw new InvalidUrlException("IPv6 host detected, but missing closing ']' tag!");
      }
    }
    urlObject.setHost(remaining);

    return urlObject;
  }

  private static String decode(String str, String enc) {
    try {
      return URLDecoder.decode(str, enc);
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }
}
