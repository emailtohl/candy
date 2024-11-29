package wlei.candy.jpa.search.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.search.SearchableRepository;
import wlei.candy.jpa.search.SearchableRepositoryImpl;
import wlei.candy.jpa.search.auction.entities.BankAccount;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface BankAccountRepo extends JpaRepository<BankAccount, Long>, BankAccountRepoExt {

}

interface BankAccountRepoExt extends SearchableRepository<Long, BankAccount> {

}

@Repository
class BankAccountRepoExtImpl extends SearchableRepositoryImpl<Long, BankAccount> implements BankAccountRepoExt {

}