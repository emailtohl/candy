package wlei.candy.jpa.envers.matches.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.envers.matches.entities.QyUser;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface QyUserRepository extends JpaRepository<QyUser, Long>, QyUserRepositoryExt {

}

interface QyUserRepositoryExt extends wlei.candy.jpa.GenericRepository<Long, QyUser> {

}

@Repository
class QyUserRepositoryExtImpl extends wlei.candy.jpa.GenericRepositoryImpl<Long, QyUser> implements QyUserRepositoryExt, UsualRepository<QyUser> {

}