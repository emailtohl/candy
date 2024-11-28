package wlei.candy.jpa.tx;

import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * TxService的实现
 * <p>
 * Author: HeLei
 * Date: 2024/11/27
 */
@Service
public class TxServiceImpl implements TxService {

  /**
   * @param supplier 事务中的过程
   * @param <R>      结果类型
   * @return 执行结果
   */
  @Override
  public <R> R exec(Supplier<R> supplier) {
    return supplier.get();
  }
}