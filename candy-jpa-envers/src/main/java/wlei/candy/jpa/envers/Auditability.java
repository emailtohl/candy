package wlei.candy.jpa.envers;

import wlei.candy.jpa.GenericEntity;

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
  String PROP_UPDATE_TIME = "updateTime";
  String PROP_UPDATE_BY = "updateBy";

  LocalDateTime getCreateTime();

  E setCreateTime(LocalDateTime createTime);

  String getCreateBy();

  E setCreateBy(String createBy);

  LocalDateTime getUpdateTime();

  E setUpdateTime(LocalDateTime updateTime);

  String getUpdateBy();

  E setUpdateBy(String updateBy);
}
