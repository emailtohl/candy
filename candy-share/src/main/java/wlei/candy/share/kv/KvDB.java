package wlei.candy.share.kv;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

/**
 * key，value存储访问
 * <p>
 * Created by helei on 2021/9/26.
 */
public interface KvDB {

  /**
   * @param key    key，不能为空
   * @param value  value，不能为null，否则获取时，无法得知该key是不存在呢，还是存储的值是null
   * @param expire 如果大于0，则在expire时间后删除该key，单位毫秒
   */
  void set(@NotEmpty String key, @NotNull String value, long expire);

  /**
   * @param key key，不能为空
   * @return value
   */
  Optional<String> get(@NotEmpty String key);

  /**
   * 删除此key
   *
   * @param key key
   */
  void delete(@NotEmpty String key);

  /**
   * @param key    key，不能为空
   * @param field  field，不能为空
   * @param value  value，不能为null，否则获取时，无法得知该key是不存在呢，还是存储的值是null
   * @param expire 如果大于0，则在expire时间后删除该key，单位毫秒
   */
  void hset(@NotEmpty String key, @NotEmpty String field, @NotNull String value, long expire);

  /**
   * @param key   key，不能为空
   * @param field field，不能为空
   * @return value
   */
  Optional<String> hget(@NotEmpty String key, @NotEmpty String field);

  /**
   * 删除key
   *
   * @param key   key，不能为空
   * @param field field，不能为空
   */
  void hdel(@NotEmpty String key, @NotEmpty String field);

  /**
   * 删除此hash
   *
   * @param key hash的key
   */
  void delHash(@NotEmpty String key);

}
