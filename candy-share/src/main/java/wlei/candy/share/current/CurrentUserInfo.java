package wlei.candy.share.current;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 当前用户信息
 * <p>
 * Created by HeLei on 2021/12/20.
 */
public interface CurrentUserInfo {

  default String username() {
    return "anonymousUser";
  }

  default Collection<String> authorities() {
    return new ArrayList<>();
  }
}
