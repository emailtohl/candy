package wlei.candy.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wlei.candy.jpa.matches.entities.Authority;
import wlei.candy.jpa.matches.entities.QyUser;
import wlei.candy.jpa.matches.repo.QyUserRepository;
import wlei.candy.jpa.tx.TxService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Author: HeLei
 * Date: 2024/11/26
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConf.class)
@ActiveProfiles(/*SpringConf.POSTGRES*/)
public class QyTest {
  @Autowired
  QyUserRepository qyUserRepo;
  @Autowired
  TxService txService;

  @Test
  void test() {
    QyUser u = new QyUser()
        .setName("admin")
        .setPassword("123456")
        .setPhone("654321")
        .setAuthority(Authority.admin.getName());
    txService.exec(() -> qyUserRepo.add(u));
    Optional<QyUser> o = qyUserRepo.get(u.getId());
    assertTrue(o.isPresent());
  }

}
