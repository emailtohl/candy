package wlei.candy.jpa.search.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.search.SearchableRepository;
import wlei.candy.jpa.search.SearchableRepositoryImpl;
import wlei.candy.jpa.search.auction.entities.Participator;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface ParticipatorRepo extends JpaRepository<Participator, Long>, ParticipatorRepoExt {

}

interface ParticipatorRepoExt extends SearchableRepository<Long, Participator> {

}

@Repository
class ParticipatorRepoExtImpl extends SearchableRepositoryImpl<Long, Participator> implements ParticipatorRepoExt {

}