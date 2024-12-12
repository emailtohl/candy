package wlei.candy.jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wlei.candy.jpa.auction.entities.FileInfo;
import wlei.candy.jpa.auction.repo.FileInfoRepo;
import wlei.candy.jpa.tx.TxService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Author: HeLei
 * Date: 2024/12/12
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConf.class)
@ActiveProfiles(/*SpringConf.POSTGRES*/)
class FileInfoRepoTest {
  @Autowired
  FileInfoRepo fileInfoRepo;
  @Autowired
  TxService txService;

  @Test
  void test() throws IOException {
    ClassPathResource r = new ClassPathResource("log4j2-test.xml");
    try (InputStream in = r.getInputStream()) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      byte[] bytes = out.toByteArray();
      FileInfo fileInfo = new FileInfo().setId(UUID.randomUUID().toString()).withData(bytes).setName("log4j2-test.xml");
      String id = txService.exec(() -> fileInfoRepo.save(fileInfo).getId());
      FileInfo fi = txService.exec(() -> fileInfoRepo.findById(id)).orElseThrow(IllegalArgumentException::new);
      byte[] data = fi.fetchData();
      Assertions.assertNotNull(data);
    }
  }
}
