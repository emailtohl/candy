package wlei.candy.share.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JSON的包装工具
 *
 * @author HeLei
 */
public final class JsonUtil {

  public static final String CONVERT_JSON_TO_OBJECT_ERROR = "convert json to Object error";
  public static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
  public static final ObjectMapper NON_NULL_MAPPER = new ObjectMapper();
  public static final ObjectMapper NON_EMPTY_MAPPER = new ObjectMapper();
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
  private static final String CONVERT_OBJECT_TO_JSON_ERROR = "convert Object to json error";

  static {
    DEFAULT_MAPPER.findAndRegisterModules();
    NON_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL).findAndRegisterModules();
    NON_EMPTY_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY).findAndRegisterModules();
  }

  private JsonUtil() {
  }

  /**
   * 将json转成对象
   *
   * @param json json字符串
   * @param c    对象的class
   * @param <T>  对象类型
   * @return 对象
   */
  public static <T> T readValue(String json, Class<T> c) {
    try {
      return DEFAULT_MAPPER.readValue(json, c);
    } catch (Exception e) {
      LOGGER.error(CONVERT_JSON_TO_OBJECT_ERROR, e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 解析json数组
   *
   * @param jsonArray json数组
   * @param c         数组的类型
   * @param <T>       对象类型
   * @return 列表对象
   */
  public static <T> List<T> readArray(String jsonArray, Class<T[]> c) {
    try {
      T[] items = DEFAULT_MAPPER.readValue(jsonArray, c);
      return Arrays.stream(items).collect(Collectors.toList());
    } catch (Exception e) {
      LOGGER.error(CONVERT_JSON_TO_OBJECT_ERROR, e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 将json转成对象
   *
   * @param json         json字符串
   * @param valueTypeRef 容器对象的class
   * @param <T>          对象类型
   * @return 对象
   */
  public static <T> T readValue(String json, TypeReference<T> valueTypeRef) {
    try {
      return DEFAULT_MAPPER.readValue(json, valueTypeRef);
    } catch (Exception e) {
      LOGGER.error(CONVERT_JSON_TO_OBJECT_ERROR, e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 把对象转换为json字符串
   *
   * @param obj 可序列化的实例
   * @return 转换后的json字符串
   */
  public static String writeValue(Object obj) {
    try {
      return DEFAULT_MAPPER.writeValueAsString(obj);
    } catch (Exception e) {
      LOGGER.error(CONVERT_OBJECT_TO_JSON_ERROR, e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 把对象转换为json字符串，如果属性是null，则忽略
   *
   * @param obj 可序列化的实例
   * @return 转换后的json字符串
   */
  public static String writeValueNonNull(Object obj) {
    try {
      return NON_NULL_MAPPER.writeValueAsString(obj);
    } catch (Exception e) {
      LOGGER.error(CONVERT_OBJECT_TO_JSON_ERROR, e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 把对象转换为json字符串，如果属性是empty(包括空集合)，则忽略
   *
   * @param obj 可序列化的实例
   * @return 转换后的json字符串
   */
  public static String writeValueNonEmpty(Object obj) {
    try {
      return NON_EMPTY_MAPPER.writeValueAsString(obj);
    } catch (Exception e) {
      LOGGER.error(CONVERT_OBJECT_TO_JSON_ERROR, e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 对json的敏感字段脱敏
   *
   * @param json            json
   * @param map             脱敏的转换器
   * @param sensitiveFields 敏感字段名字
   * @return 已脱敏的json，如果存在解析异常，则原样返回
   */
  public static String mask(String json, Function<String, String> map, String... sensitiveFields) {
    try {
      if (sensitiveFields.length == 0) {
        return json;
      }
      JsonNode n = DEFAULT_MAPPER.readTree(json);
      Set<String> set = new HashSet<>(Arrays.asList(sensitiveFields));
      modify(n, map, set);
      return DEFAULT_MAPPER.writeValueAsString(n);
    } catch (Exception e) {
      LOGGER.debug(e.getMessage(), e);
      return json;
    }
  }

  private static void modify(JsonNode jNode, Function<String, String> map, Set<String> sensitiveFields) {
    if (jNode.isObject()) {
      ObjectNode oNode = (ObjectNode) jNode;
      for (Iterator<String> it = oNode.fieldNames(); it.hasNext(); ) {
        String fieldName = it.next();
        JsonNode subNode = oNode.get(fieldName);
        if (sensitiveFields.contains(fieldName) && subNode.isTextual()) {
          String text = subNode.asText("");
          String maskVal = map.apply(text);
          oNode.put(fieldName, maskVal);
        } else {
          modify(subNode, map, sensitiveFields);
        }
      }
    } else if (jNode.isArray()) {
      jNode.forEach(item -> modify(item, map, sensitiveFields));
    }
  }
}
