package wlei.candy.jpa.envers.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.UsualRepositoryImpl;
import wlei.candy.jpa.envers.EnversRepository;
import wlei.candy.jpa.envers.RevTuple;
import wlei.candy.jpa.envers.auction.entities.Item;

import java.util.List;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface ItemRepo extends JpaRepository<Item, Long>, ItemRepoExt {

}

interface ItemRepoExt extends UsualRepository<Item> {

  List<RevTuple<Long, Item, Item>> getRevisions(long id);
}

@Repository
class ItemRepoExtImpl extends UsualRepositoryImpl<Item> implements ItemRepoExt {

  @Override
  public List<RevTuple<Long, Item, Item>> getRevisions(long id) {
    return new EnversRepository<Long, Item, Item>(entityManager, entityClass).getRevisions(id);
  }
}