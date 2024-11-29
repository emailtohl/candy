package wlei.candy.jpa.envers.matches.matches.entities;

/**
 * 比赛类型
 * <p>
 * Author: HeLei
 * Date: 2024/11/24
 */
public enum MatchType {
  Go("围棋"),
  ChineseChess("中国象棋"),
  Chess("国际象棋"),
  ;
  private final String zhName;

  MatchType(String zhName) {
    this.zhName = zhName;
  }

  public String getZhName() {
    return zhName;
  }
}
