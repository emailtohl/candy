package wlei.candy.jpa.matches.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.UsualRepositoryImpl;
import wlei.candy.jpa.matches.entities.QyUser;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface QyUserRepository extends JpaRepository<QyUser, Long>, QyUserRepositoryExt {

}

interface QyUserRepositoryExt extends UsualRepository<QyUser> {

}

@Repository
class QyUserRepositoryExtImpl extends UsualRepositoryImpl<QyUser> implements QyUserRepositoryExt {

}