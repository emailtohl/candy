package wlei.candy.web;

import jakarta.servlet.ReadListener;

/**
 * 用于测试的ReadListener
 * <p>
 * Created by HeLei on 2022/6/18.
 */
class AReadListener implements ReadListener {

  private boolean onDataAvailableDone = false;
  private boolean onAllDataReadDone = false;

  @Override
  public void onDataAvailable() {
    onDataAvailableDone = true;
  }

  @Override
  public void onAllDataRead() {
    onAllDataReadDone = true;
  }

  @Override
  public void onError(Throwable t) {
    // 不会执行到此处来
    throw new AssertionError(t);
  }

  public boolean isOnDataAvailableDone() {
    return onDataAvailableDone;
  }

  public boolean isOnAllDataReadDone() {
    return onAllDataReadDone;
  }
}
