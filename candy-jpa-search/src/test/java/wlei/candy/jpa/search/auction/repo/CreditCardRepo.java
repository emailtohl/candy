package wlei.candy.jpa.search.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.search.SearchableRepository;
import wlei.candy.jpa.search.SearchableRepositoryImpl;
import wlei.candy.jpa.search.auction.entities.CreditCard;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface CreditCardRepo extends JpaRepository<CreditCard, Long>, CreditCardRepoExt {

}

interface CreditCardRepoExt extends SearchableRepository<Long, CreditCard> {

}

@Repository
class CreditCardRepoExtImpl extends SearchableRepositoryImpl<Long, CreditCard> implements CreditCardRepoExt {

}