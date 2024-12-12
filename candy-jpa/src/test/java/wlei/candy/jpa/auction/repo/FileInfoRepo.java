package wlei.candy.jpa.auction.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wlei.candy.jpa.auction.entities.FileInfo;

/**
 * Author: HeLei
 * Date: 2024/12/12
 */
public interface FileInfoRepo extends JpaRepository<FileInfo, String> {
}
