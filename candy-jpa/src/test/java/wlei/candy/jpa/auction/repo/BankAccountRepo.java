package wlei.candy.jpa.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.UsualRepositoryImpl;
import wlei.candy.jpa.auction.entities.BankAccount;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface BankAccountRepo extends JpaRepository<BankAccount, Long>, BankAccountRepoExt {

}

interface BankAccountRepoExt extends UsualRepository<BankAccount> {

}

@Repository
class BankAccountRepoExtImpl extends UsualRepositoryImpl<BankAccount> implements BankAccountRepoExt {

}