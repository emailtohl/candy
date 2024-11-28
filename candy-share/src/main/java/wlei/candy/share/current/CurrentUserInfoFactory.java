package wlei.candy.share.current;

import jakarta.validation.constraints.NotNull;

import java.util.function.Supplier;

/**
 * 获取当前用户的工厂方法
 * <p>
 * Created by HeLei on 2021/12/20.
 */
public class CurrentUserInfoFactory {
  private static volatile Supplier<CurrentUserInfo> supplier;

  @NotNull
  public static CurrentUserInfo get() {
    if (supplier == null) {
      return new CurrentUserInfo() {
      };
    }
    return supplier.get();
  }

  public synchronized static void setSupplier(Supplier<CurrentUserInfo> supplier) {
    CurrentUserInfoFactory.supplier = supplier;
  }
}
