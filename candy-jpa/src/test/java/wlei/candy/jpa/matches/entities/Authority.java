package wlei.candy.jpa.matches.entities;

/**
 * Author: HeLei
 * Date: 2024/11/24
 */
public enum Authority {
  admin("admin"),
  manager("manager"),
  ordinary("ordinary"),
  ;

  private final String name;

  Authority(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
