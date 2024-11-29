package wlei.candy.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 可被审计的接口
 * <p>
 * Author: HeLei
 * Date: 2024/11/28
 *
 * @param <I> ID
 * @param <E> 可被审计的实体的类型
 */
public interface Auditability<I extends Serializable, E extends GenericEntity<I, E>> {
  String PROP_CREATE_BY = "createBy";
  String PROP_MODIFY_TIME = "modifyTime";
  String PROP_MODIFY_BY = "modifyBy";

  LocalDateTime getCreateTime();

  E setCreateTime(LocalDateTime createTime);

  String getCreateBy();

  E setCreateBy(String createBy);

  LocalDateTime getModifyTime();

  E setModifyTime(LocalDateTime modifyTime);

  String getModifyBy();

  E setModifyBy(String modifyBy);
}
