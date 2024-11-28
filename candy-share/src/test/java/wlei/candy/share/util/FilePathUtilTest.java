package wlei.candy.share.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilePathUtilTest {

  /*
   *Join方法测试
   * @parm 路径符号（左右斜杠）转换
   */
  @Test
  void testJoin() {
    String dir1 = "/abc\\bcd/";
    String dir2 = "\\edf/ghi\\jk";
    String dir = FilePathUtil.join(dir1, dir2);
    assertNotNull(dir);
  }

  /*
   * Join方法测试
   * @parm 含有中文，特殊字符，空格
   */
  @Test
  void testJoin_for_special() {
    String dir1 = "///HTTP://中文有空格/ ";
    String dir2 = "\\/YOU/G——ROUP\\M_E";
    String dir3 = "FILENAME.LOG";
    String dir = FilePathUtil.join(dir1, dir2, dir3);
    //判断当前系统类型，根据系统类型进行断言
    assertTrue(dir.contains("中文有空格"));
//    System.out.println("///HTTP:" + "//中文有空格" + "/ " + "//YOU" + "/G——ROUP" + "/M_E" + "/FILENAME.LOG");
  }

  @Test
  void testGetOrCreateDir() throws IOException {
    String path = FilePathUtil.join(System.getProperty("java.io.tmpdir"), "test_dir");
    File dir = FilePathUtil.getAndCreateDir(path);
    assertTrue(dir.exists());
    assertTrue(dir.delete());
  }

  @Test
  void testGetOrCreateFile() throws IOException {
    String path = FilePathUtil.join(System.getProperty("java.io.tmpdir"), "test_file");
    File dir = FilePathUtil.getAndCreateFile(path);
    assertTrue(dir.exists());
    assertTrue(dir.delete());
  }

  @Test
  void findFiles() {
    String tmp = System.getProperty("java.io.tmpdir");
    List<File> files = FilePathUtil.findFiles(tmp);
    assertFalse(files.isEmpty());
  }
}