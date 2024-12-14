package wlei.candy.share.tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Author: HeLei
 * Date: 2024/12/13
 */
class ForestTest {
  private List<Menu> menus;

  @BeforeEach
  void setUp() {
    menus = new ArrayList<>();
    Menu m1 = new Menu().setId("1").setName("前菜");
    Menu m11 = new Menu().setId("11").setName("沙拉").setParent(m1);
    Menu m12 = new Menu().setId("12").setName("汤类").setParent(m1);
    Menu m13 = new Menu().setId("13").setName("小吃").setParent(m1);

    Menu m2 = new Menu().setId("2").setName("主菜");
    Menu m21 = new Menu().setId("21").setName("牛排").setParent(m2);
    Menu m211 = new Menu().setId("211").setName("西冷牛排").setParent(m21);
    Menu m212 = new Menu().setId("212").setName("菲力牛排").setParent(m21);
    Menu m22 = new Menu().setId("22").setName("海鲜").setParent(m2);
    Menu m221 = new Menu().setId("221").setName("烤三文鱼").setParent(m22);
    Menu m222 = new Menu().setId("222").setName("炸虾").setParent(m22);
    Menu m23 = new Menu().setId("23").setName("素食").setParent(m2);
    Menu m231 = new Menu().setId("231").setName("蔬菜烩饭").setParent(m23);
    Menu m232 = new Menu().setId("232").setName("豆腐炒面").setParent(m23);

    Menu m3 = new Menu().setId("3").setName("甜点");
    Menu m31 = new Menu().setId("31").setName("蛋糕").setParent(m3);
    Menu m311 = new Menu().setId("311").setName("巧克力蛋糕").setParent(m31);
    Menu m312 = new Menu().setId("312").setName("草莓蛋糕").setParent(m31);
    Menu m32 = new Menu().setId("32").setName("冰淇淋").setParent(m3);
    Menu m321 = new Menu().setId("321").setName("香草冰淇淋").setParent(m32);
    Menu m322 = new Menu().setId("322").setName("巧克力冰淇淋").setParent(m32);

    Menu m4 = new Menu().setId("4").setName("饮品");
    Menu m41 = new Menu().setId("41").setName("非酒精饮料").setParent(m4);
    Menu m411 = new Menu().setId("411").setName("果汁").setParent(m41);
    Menu m412 = new Menu().setId("412").setName("碳酸饮料").setParent(m41);
    Menu m42 = new Menu().setId("42").setName("酒类").setParent(m4);
    Menu m421 = new Menu().setId("421").setName("红酒").setParent(m42);
    Menu m422 = new Menu().setId("422").setName("啤酒").setParent(m42);

    menus.add(m1);
    menus.add(m11);
    menus.add(m12);
    menus.add(m13);
    menus.add(m2);
    menus.add(m21);
    menus.add(m211);
    menus.add(m212);
    menus.add(m22);
    menus.add(m221);
    menus.add(m222);
    menus.add(m23);
    menus.add(m231);
    menus.add(m232);
    menus.add(m3);
    menus.add(m31);
    menus.add(m311);
    menus.add(m312);
    menus.add(m32);
    menus.add(m321);
    menus.add(m322);
    menus.add(m4);
    menus.add(m41);
    menus.add(m411);
    menus.add(m412);
    menus.add(m42);
    menus.add(m421);
    menus.add(m422);
    Collections.shuffle(menus);
  }

  @Test
  void build() {
    Forest<Menu> forest = new Forest<>(menus);
    assertEquals(4, forest.size());

    List<Menu> bfs = forest.breadthFirst();
    assertEquals(menus.size(), bfs.size());
  }

  @Test
  void initKey() {
    Forest<Menu> forest = new Forest<>(menus);
    removeKey(new ArrayList<>(forest));
    forest.initKey();
    List<Menu> bfs = forest.breadthFirst();
    assertEquals(menus.size(), bfs.size());
//    assertTrue(bfs.stream().anyMatch(m -> m.getParent() != null));
  }

  private void removeKey(List<TreeNode<Menu>> treeNodes) {
    if (treeNodes == null) {
      return;
    }
    for (TreeNode<Menu> n : treeNodes) {
      n.setKey(null);
      n.setParentKey(null);
      n.getNode().setParent(null);
      removeKey(n.getChildren());
    }
  }
}