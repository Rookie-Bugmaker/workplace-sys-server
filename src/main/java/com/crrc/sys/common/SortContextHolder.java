 package com.crrc.sys.common;

  import java.util.Map;

  /**
   * 排序参数上下文
   * 排序参数统一由 query string 传递（sortField=字段编码&sortOrder=asc/desc），
   * 由拦截器在请求入口写入，业务层任意位置读取，请求结束自动清理
   */
  public final class SortContextHolder {

      private static final ThreadLocal<SortContext> HOLDER = new ThreadLocal<>();

      private SortContextHolder() {
      }

      public static void set(String sortField, String sortOrder) {
          HOLDER.set(new SortContext(sortField, "asc".equalsIgnoreCase(sortOrder)));
      }

      public static void clear() {
          HOLDER.remove();
      }

      /** 排序字段编码（前端传的编码，非列名） */
      public static String getSortField() {
          SortContext ctx = HOLDER.get();
          return ctx == null ? null : ctx.sortField;
      }

      /** 排序字段方式(默认降序DESC) */
      public static String getSortOrder() {
          SortContext ctx = HOLDER.get();
          return ctx != null ? (ctx.asc ? "ASC" : "DESC") : "DESC";
      }

      /** 是否升序（未传默认降序） */
      public static boolean isAsc() {
          SortContext ctx = HOLDER.get();
          return ctx != null && ctx.asc;
      }

      private static final class SortContext {
          final String sortField;
          final boolean asc;

          SortContext(String sortField, boolean asc) {
              this.sortField = sortField;
              this.asc = asc;
          }
      }
  }
