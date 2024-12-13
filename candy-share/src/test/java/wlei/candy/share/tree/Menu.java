package wlei.candy.share.tree;

import java.util.Optional;

/**
 * Author: HeLei
 * Date: 2024/12/13
 */
class Menu implements SelfReference {
  private String id;

  private String name;

  private Menu parent;

  @Override
  public String getKey() {
    return id;
  }

  @Override
  public void setKey(String key) {
    id = key;
  }

  @Override
  public String getParentKey() {
    return Optional.ofNullable(parent).map(Menu::getKey).orElse(null);
  }

  @Override
  public void setParentKey(String key) {
    if (parent == null) {
      parent = new Menu();
    }
    parent.setKey(key);
  }

  @Override
  public String toString() {
    return String.format("%s:%s:%s", getKey(), name, getParentKey());
  }

  public String getId() {
    return id;
  }

  public Menu setId(String id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Menu setName(String name) {
    this.name = name;
    return this;
  }

  public Menu getParent() {
    return parent;
  }

  public Menu setParent(Menu parent) {
    this.parent = parent;
    return this;
  }

}
