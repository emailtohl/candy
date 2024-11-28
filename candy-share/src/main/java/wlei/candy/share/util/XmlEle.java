package wlei.candy.share.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 简易的xml元素数据模型，但满足自定义的equals hashcode，可在容器中识别
 *
 * @author HeLei
 */
public class XmlEle {
  /**
   * 调试日志
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(XmlEle.class);
  /**
   * 文件创建
   */
  private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
  /**
   * 节点名称
   */
  private final String name;
  /**
   * 子节点
   */
  private final List<XmlEle> children = new ArrayList<>();
  /**
   * 该元素上的属性
   */
  private final XmlAttrs xmlAttrs = new XmlAttrs();
  /**
   * 元素的文本集合
   */
  private final List<String> texts = new ArrayList<>();

  /**
   * 填充节点数据
   *
   * @param element 节点
   */
  private XmlEle(Element element) {
    this.name = element.getNodeName();
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      Node n = attributes.item(i);
      if (n instanceof Attr) {
        Attr a = (Attr) n;
        this.xmlAttrs.put(a.getNodeName(), a.getNodeValue());
      }
    }
    this.fillChildren(element.getChildNodes());
    if (LOGGER.isTraceEnabled()) {
      //记录调试日志
      LOGGER.trace("{}元素的json信息", name);
      LOGGER.trace(JsonUtil.writeValue(this));
    }
  }

  /**
   * 将xml解析为自定义元素数据结构
   *
   * @param xml xml串
   * @return Ele数据对象
   */
  public static XmlEle from(String xml) {
    // 先声明
    XmlEle el = null;
    // 捕捉异常
    try {
      // 先创建一个builder
      DocumentBuilder builder = FACTORY.newDocumentBuilder();
      // 再获取输入流
      InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
      // 解析成文档
      Document document = builder.parse(inputStream);
      // 获取根路径
      Element root = document.getDocumentElement();
      // 生成新的xml实例
      el = new XmlEle(root);
    } catch (ParserConfigurationException | IOException ex) {
      // 记录错误
      LOGGER.error(ex.getMessage(), ex);
    } catch (SAXException ex) {
      // 记录错误
      throw new IllegalArgumentException(ex);
    }
    // 返回实例
    return el;
  }

  /**
   * 将子节点填充数据
   *
   * @param nodeList 数据列表
   */
  private void fillChildren(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;
        XmlEle el = new XmlEle(element);
        this.children.add(el);
      } else if (node instanceof Attr) {
        Attr attr = (Attr) node;
        this.xmlAttrs.put(attr.getNodeName(), attr.getNodeValue());
      } else if (node instanceof Text) {
        String textContent = node.getTextContent();
        if (StringUtils.hasText(textContent)) {
          this.texts.add(textContent.trim());
        }
      }
    }
  }

  /**
   * hashCode方法
   *
   * @return hash值
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (xmlAttrs.isEmpty() ? 0 : xmlAttrs.hashCode());
    result = prime * result + (StringUtils.hasText(name) ? 0 : name.hashCode());
    result = prime * result + (texts.isEmpty() ? 0 : textHashCode());
    result = prime * result + (children.isEmpty() ? 0 : childreHashCode());
    return result;
  }

  /**
   * childreHashCode方法
   *
   * @return childreHash值
   */
  private int childreHashCode() {
    int result = 1;
    for (XmlEle e : children) {
      result = result + e.hashCode();
    }
    return result;
  }

  /**
   * textHashCode方法
   *
   * @return textHash值
   */
  private int textHashCode() {
    int result = 1;
    for (String s : texts) {
      result = result + s.hashCode();
    }
    return result;
  }

  /**
   * equals方法
   *
   * @param obj 比较对象
   * @return 比较结果
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    XmlEle other = (XmlEle) obj;
    if (!xmlAttrs.equals(other.xmlAttrs)) {
      return false;
    }
    if (!new HashSet<>(children).containsAll(other.children) || !new HashSet<>(other.children).containsAll(children)) {
      return false;
    }
    if (!new HashSet<>(texts).containsAll(other.texts) || !new HashSet<>(other.texts).containsAll(texts)) {
      return false;
    }
    if (name == null) {
      return other.name == null;
    } else return name.equals(other.name);
  }

  /**
   * toString方法
   *
   * @return String
   */
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append('\n').append('<').append(name).append(attrString()).append('>');
    // 将文本和元素的顺序交替写入数组中，以便于更符合实际xml的顺序
    Object[] arr = alternateTextsAndElements();
    for (Object o : arr) {
      s.append(o.toString());
    }
    s.append('\n').append('<').append('/').append(name).append('>');
    return s.toString();
  }

  /**
   * 将元素属性值创建成一段字符串
   *
   * @return 属性的字符串
   */
  private String attrString() {
    if (xmlAttrs.isEmpty()) {
      return "";
    }
    StringBuilder s = new StringBuilder();
    for (Entry<String, String> e : xmlAttrs.entrySet()) {
      s.append(' ').append(e.getKey()).append('=').append('"').append(e.getValue()).append('"');
    }
    return s.toString();
  }

  /**
   * 将文本和元素的顺序交替写入数组中，以便于更符合实际xml的顺序
   *
   * @return 文本和元素交替顺序的数组
   */
  private Object[] alternateTextsAndElements() {
    Object[] arr = new Object[texts.size() + children.size()];
    Iterator<String> itext = texts.iterator();
    Iterator<XmlEle> ichildren = children.iterator();
    int i = 0;
    while (itext.hasNext() && ichildren.hasNext()) {
      arr[i++] = itext.next();
      arr[i++] = ichildren.next();
    }
    while (itext.hasNext()) {
      arr[i++] = itext.next();
    }
    while (ichildren.hasNext()) {
      arr[i++] = ichildren.next();
    }
    return arr;
  }
}
