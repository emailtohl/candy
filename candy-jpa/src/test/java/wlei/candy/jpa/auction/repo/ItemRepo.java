package wlei.candy.jpa.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.*;
import wlei.candy.jpa.auction.entities.Item;

import java.util.List;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface ItemRepo extends JpaRepository<Item, Long>, ItemRepoExt {

}

interface ItemRepoExt extends SearchableRepository<Long, Item> {

  List<RevTuple<Long, Item, Item>> getRevisions(long id);
}

@Repository
class ItemRepoExtImpl extends SearchableRepositoryImpl<Long, Item> implements ItemRepoExt {

  @Override
  public List<RevTuple<Long, Item, Item>> getRevisions(long id) {
    return new EnversRepository<Long, Item, Item>(entityManager, entityClass).getRevisions(id);
  }
}