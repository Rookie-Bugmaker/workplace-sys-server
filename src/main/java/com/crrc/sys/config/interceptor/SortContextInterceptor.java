  package com.crrc.sys.config.interceptor;

  import com.crrc.sys.common.SortContextHolder;
  import org.springframework.stereotype.Component;
  import org.springframework.web.servlet.HandlerInterceptor;

  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;

  /**
   * 排序参数拦截器
   * 与请求体传参方式无关，排序参数统一从 query string 提取写入上下文
   */
  @Component
  public class SortContextInterceptor implements HandlerInterceptor {

      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                               Object handler) {
          // getParameter 对 GET query、POST query 均生效；@RequestBody 的 JSON 体不会被解析进来，
          // 天然保证只认 URL 上的排序参数
          SortContextHolder.set(request.getParameter("fcSortField"),
                  request.getParameter("fcSortOrder"));
          return true;
      }

      @Override
      public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                  Object handler, Exception ex) {
          // 必须清理：Tomcat 线程池复用，否则上一个请求的排序会串到下一个请求
          SortContextHolder.clear();
      }
  }
