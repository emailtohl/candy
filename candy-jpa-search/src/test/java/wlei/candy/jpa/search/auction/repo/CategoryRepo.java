package wlei.candy.jpa.search.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.search.SearchableRepository;
import wlei.candy.jpa.search.SearchableRepositoryImpl;
import wlei.candy.jpa.search.auction.entities.Category;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface CategoryRepo extends JpaRepository<Category, Long>, CategoryRepoExt {

}

interface CategoryRepoExt extends SearchableRepository<Long, Category> {

}

@Repository
class CategoryRepoExtImpl extends SearchableRepositoryImpl<Long, Category> implements CategoryRepoExt {

}