package wlei.candy.jpa.search.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.search.SearchableRepository;
import wlei.candy.jpa.search.SearchableRepositoryImpl;
import wlei.candy.jpa.search.auction.entities.Bid;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface BidRepo extends JpaRepository<Bid, Long>, BidRepoExt {

}

interface BidRepoExt extends SearchableRepository<Long, Bid> {

}

@Repository
class BidRepoExtImpl extends SearchableRepositoryImpl<Long, Bid> implements BidRepoExt {

}