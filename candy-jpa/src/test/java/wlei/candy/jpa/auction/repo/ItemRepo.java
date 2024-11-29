package wlei.candy.jpa.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.UsualRepositoryImpl;
import wlei.candy.jpa.auction.entities.Item;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface ItemRepo extends JpaRepository<Item, Long>, ItemRepoExt {

}

interface ItemRepoExt extends UsualRepository<Item> {

}

@Repository
class ItemRepoExtImpl extends UsualRepositoryImpl<Item> implements ItemRepoExt {

}