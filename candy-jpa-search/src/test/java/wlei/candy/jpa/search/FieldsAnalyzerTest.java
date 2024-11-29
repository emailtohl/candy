package wlei.candy.jpa.search;

import org.junit.jupiter.api.Test;
import wlei.candy.jpa.search.auction.entities.Item;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 对搜索库构造时，解析@Field的测试
 * <p>
 * Created by HeLei on 2021/4/24.
 */
class FieldsAnalyzerTest {

  @Test
  void test() {
    FieldsAnalyzer analyzer = new FieldsAnalyzer(Item.class);
    String[] onFields = analyzer.parse();
    Set<String> set = Arrays.stream(onFields).collect(Collectors.toSet());
    assertTrue(set.contains("name"));
    assertTrue(set.contains("description"));
    assertTrue(set.contains("seller.name"));
    assertTrue(set.contains("bids.name"));
    assertTrue(set.contains("bids.bidder.name"));
  }

}
