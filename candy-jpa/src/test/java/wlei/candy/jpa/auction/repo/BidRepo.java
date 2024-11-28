package wlei.candy.jpa.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.UsualRepositoryImpl;
import wlei.candy.jpa.auction.entities.Bid;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface BidRepo extends JpaRepository<Bid, Long>, BidRepoExt {

}

interface BidRepoExt extends UsualRepository<Bid> {

}

@Repository
class BidRepoExtImpl extends UsualRepositoryImpl<Bid> implements BidRepoExt {

}