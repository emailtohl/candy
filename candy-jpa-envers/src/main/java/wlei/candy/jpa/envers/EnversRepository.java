package wlei.candy.jpa.envers;

import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import wlei.candy.jpa.GenericEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 审计查询的访问仓库
 * <p>
 * Author: HeLei
 * Date: 2024/11/26
 *
 * @param <I> ID类型
 * @param <E> 实体对象
 * @param <A> 实现可审计接口的类型
 */
public class EnversRepository<I extends Serializable, E extends GenericEntity<I, E>, A extends Auditability<I, E>> {
  private final EntityManager entityManager;
  private final Class<E> entityClass;

  public EnversRepository(EntityManager entityManager, Class<E> entityClass) {
    this.entityManager = entityManager;
    this.entityClass = entityClass;
  }

  /**
   * 获取该实体所有历史版本 注意：实体上未注解@Audited的不能调用此接口
   *
   * @param id 实体的id
   * @return 历史各个修订版
   */
  @SuppressWarnings("unchecked")
  public List<RevTuple<I, E, A>> getRevisions(I id) {
    AuditReader auditReader = AuditReaderFactory.get(entityManager);
    AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(entityClass, false, true);
    query.add(AuditEntity.id().eq(id));
    List<?> result = query.getResultList();
    List<RevTuple<I, E, A>> tuples = new ArrayList<>();
    for (Object o : result) {
      if (o instanceof Object[]) {
        Object[] arr = (Object[]) o;
        DefaultRevisionEntity r = (DefaultRevisionEntity) arr[1];
        RevTuple<I, E, A> tuple = new RevTuple<>((A) arr[0], r.getId(), r.getRevisionDate(), (RevisionType) arr[2]);
        tuples.add(tuple);
      }
    }
    return tuples;
  }

}
