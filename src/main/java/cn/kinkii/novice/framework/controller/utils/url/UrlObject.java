package cn.kinkii.novice.framework.controller.utils.url;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlObject {

  private String charset = "utf-8";
  private String scheme;
  private String username;
  private String password;
  private String host;
  private Integer port;
  private String path;
  private String queryString;
  private Map<String, String> queryParams;
  private String anchor;

  public void setQueryString(String queryString) {
    this.queryString = queryString;
    queryParams = new HashMap<>();
    String[] queryParamArray = queryString.split("&");
    Stream.of(queryParamArray).forEach(s -> {
      String[] queryParam = s.split("=");
      if (queryParam.length > 1 && queryParam[0] != null && !queryParam[0].isEmpty()) {
        try {
          queryParams.put(queryParam[0], URLDecoder.decode(queryParam[1], charset));
        } catch (UnsupportedEncodingException e) {
          queryParams.put(queryParam[0], "");
        }
      } else {
        queryParams.put(queryParam[0], "");
      }
    });
  }

  public UrlObject removeQueryParam(String param) {
    if (queryParams == null) {
      queryParams = new HashMap<>();
    }
    if (param != null && !param.isEmpty()) {
      queryParams.remove(param);
    }
    return this;
  }

  public UrlObject putQueryParam(String param, String value) {
    if (queryParams == null) {
      queryParams = new HashMap<>();
    }
    if (param != null && !param.isEmpty()) {
      try {
        queryParams.put(param, URLDecoder.decode(value, charset));
      } catch (UnsupportedEncodingException e) {
        queryParams.put(param, "");
      }
    }
    return this;
  }

  public String toUrl() {
    StringBuilder url = new StringBuilder();
    if (scheme != null && !scheme.isEmpty()) {
      url.append(scheme).append("://");
    }
    if (username != null && !username.isEmpty()) {
      try {
        url.append(URLEncoder.encode(username, charset));
      } catch (UnsupportedEncodingException ignored) {
      }
      if (password != null && !password.isEmpty()) {
        try {
          url.append(":").append(URLEncoder.encode(password, charset));
        } catch (UnsupportedEncodingException ignored) {
        }
      }
      url.append("@");
    }
    if (host != null && !host.isEmpty()) {
      url.append(host);
    }
    if (port != null && port > 0) {
      url.append(":").append(port);
    }
    if (path != null && !path.isEmpty()) {
      url.append(path);
    }
    if (queryString != null && !queryString.isEmpty()) {
      String conn = "?";
      for (String key : queryParams.keySet()) {
        url.append(conn).append(key).append("=");
        String value = queryParams.get(key);
        if (value != null && !value.isEmpty()) {
          try {
            url.append(URLEncoder.encode(value, charset));
          } catch (UnsupportedEncodingException ignored) {
          }
        }
        conn = "&";
      }
    }
    if (anchor != null && !anchor.isEmpty()) {
      url.append("#").append(anchor);
    }
    System.out.println(url.toString());
    return url.toString();
  }

  @Override
  public String toString() {
    return toUrl();
  }
}

