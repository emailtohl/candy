package wlei.candy.jpa.envers;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.envers.RevisionType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static wlei.candy.share.util.DateUtil.GMT_8;
import static wlei.candy.share.util.DateUtil.TIME_PATTERN;

/**
 * 修订记录的元祖信息
 * <p>
 * Author: HeLei
 * Date: 2024/11/25
 *
 * @param <I> ID类型
 * @param <A> 实现审计信息接口的实体类型
 */
public class RevTuple<I extends Serializable, E extends GenericEntity<I, E>, A extends Auditability<I, E>> {

  /**
   * 该版本下的实体 转换时可以用domain的对象
   */
  private A entity;
  /**
   * 版本号的id，对应数据库REVINFO表中的REV
   */
  private int rev;
  /**
   * 时间格式的版本号，对应数据库REVINFO表中的REVTSTMP
   */
  @DateTimeFormat(pattern = TIME_PATTERN)
  @JsonFormat(pattern = TIME_PATTERN, timezone = GMT_8)
  private Date revStamp;
  /**
   * 修订的类型
   */
  @JsonFormat(shape = STRING)
  private RevisionType revisionType;

  /**
   * Instantiates a new Rev tuple.
   */
  public RevTuple() {
  }

  /**
   * Instantiates a new Rev tuple.
   *
   * @param entity       the entity
   * @param rev          the rev
   * @param revStamp     the revStamp
   * @param revisionType the revision type
   */
  public RevTuple(A entity, int rev, Date revStamp, RevisionType revisionType) {
    this.entity = entity;
    this.rev = rev;
    this.revStamp = revStamp;
    this.revisionType = revisionType;
  }

  /**
   * Gets entity.
   * 当版本是DEL时，字段会被修改为null，使得一些非空的约束失效，所以当版本是DEL时，会新建一个实例返回
   *
   * @return the entity
   */
  @SuppressWarnings("unchecked")
  public A getEntity() {
    if (revisionType == RevisionType.DEL) {
      try {
        return (A) entity.getClass().getDeclaredConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new IllegalStateException(e);
      }
    }
    return entity;
  }

  public RevTuple<I, E, A> setEntity(A entity) {
    this.entity = entity;
    return this;
  }

  public int getRev() {
    return rev;
  }

  public RevTuple<I, E, A> setRev(int rev) {
    this.rev = rev;
    return this;
  }

  public Date getRevStamp() {
    return revStamp;
  }

  public RevTuple<I, E, A> setRevStamp(Date revStamp) {
    this.revStamp = revStamp;
    return this;
  }

  public RevisionType getRevisionType() {
    return revisionType;
  }

  public RevTuple<I, E, A> setRevisionType(RevisionType revisionType) {
    this.revisionType = revisionType;
    return this;
  }

  @Override
  public String toString() {
    return "rev=" + rev +
        ", revStamp=" + revStamp +
        ", revisionType=" + revisionType;
  }

}
