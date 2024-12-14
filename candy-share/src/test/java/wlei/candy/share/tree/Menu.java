package wlei.candy.share.tree;

/**
 * Author: HeLei
 * Date: 2024/12/13
 */
class Menu implements SelfReference<Menu> {
  private String id;

  private String name;

  private Menu parent;

  @Override
  public String getKey() {
    return id;
  }

  @Override
  public String toString() {
    return String.format("%s:%s", getKey(), name);
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

  @Override
  public Menu getParent() {
    return parent;
  }

  @Override
  public Menu setParent(Menu parent) {
    this.parent = parent;
    return this;
  }

}
