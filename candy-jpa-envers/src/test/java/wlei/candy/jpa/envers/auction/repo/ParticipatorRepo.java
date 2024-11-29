package wlei.candy.jpa.envers.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.UsualRepositoryImpl;
import wlei.candy.jpa.envers.auction.entities.Participator;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface ParticipatorRepo extends JpaRepository<Participator, Long>, ParticipatorRepoExt {

}

interface ParticipatorRepoExt extends UsualRepository<Participator> {

}

@Repository
class ParticipatorRepoExtImpl extends UsualRepositoryImpl<Participator> implements ParticipatorRepoExt {

}