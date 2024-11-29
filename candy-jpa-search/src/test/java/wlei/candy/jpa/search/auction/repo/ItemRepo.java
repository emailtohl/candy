package wlei.candy.jpa.search.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.search.SearchableRepository;
import wlei.candy.jpa.search.SearchableRepositoryImpl;
import wlei.candy.jpa.search.auction.entities.Item;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface ItemRepo extends JpaRepository<Item, Long>, ItemRepoExt {

}

interface ItemRepoExt extends SearchableRepository<Long, Item> {

}

@Repository
class ItemRepoExtImpl extends SearchableRepositoryImpl<Long, Item> implements ItemRepoExt {

}