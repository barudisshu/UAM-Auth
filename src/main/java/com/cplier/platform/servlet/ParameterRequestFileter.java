package com.cplier.platform.servlet;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

// 实现过滤html危险字符
public class ParameterRequestFileter implements Filter {

  @Override
  public void destroy() {
  }

  private static String htmlTag(String str) {
    if (StringUtils.isEmpty(str)) return str;
    str = str.replace("'", "");
    str = str.replace("\"", "");
    return str;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(request);
    HttpServletResponse response = (HttpServletResponse) resp;
    Enumeration<String> enumer = request.getParameterNames();
    while (enumer.hasMoreElements()) {
      String key = enumer.nextElement();
      String[] vals = request.getParameterValues(key);
      if (vals != null && vals.length > 0) {
        String[] strs = new String[vals.length];
        for (int i = 0; i < vals.length; i++) {
          strs[i] = htmlTag(vals[i]);
          requestWrapper.addParameter(key, strs);
        }
      } else {
        requestWrapper.addParameter(key, vals);
      }
    }
    chain.doFilter(requestWrapper, response);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {}
}
