package wlei.candy.jpa.tx;

import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

/**
 * 仓库层一般不会标记@Transactional，若要直接执行仓库层的代码，可使用本事务接口代替一般的service层代码
 * <p>
 * Author: HeLei
 * Date: 2024/11/27
 */
public interface TxService {

  /**
   * @param supplier 事务中的过程
   * @param <R>      结果类型
   * @return 执行结果
   */
  @Transactional
  <R> R exec(Supplier<R> supplier);
}
