package wlei.candy.jpa.envers.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.UsualRepositoryImpl;
import wlei.candy.jpa.envers.auction.entities.CreditCard;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface CreditCardRepo extends JpaRepository<CreditCard, Long>, CreditCardRepoExt {

}

interface CreditCardRepoExt extends UsualRepository<CreditCard> {

}

@Repository
class CreditCardRepoExtImpl extends UsualRepositoryImpl<CreditCard> implements CreditCardRepoExt {

}