package wlei.candy.jpa.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wlei.candy.jpa.UsualRepository;
import wlei.candy.jpa.UsualRepositoryImpl;
import wlei.candy.jpa.auction.entities.Category;

/**
 * Author: HeLei
 * Date: 2024/11/28
 */
public interface CategoryRepo extends JpaRepository<Category, Long>, CategoryRepoExt {

}

interface CategoryRepoExt extends UsualRepository<Category> {

}

@Repository
class CategoryRepoExtImpl extends UsualRepositoryImpl<Category> implements CategoryRepoExt {

}