/**
 * 拷贝自spring boot，使用本包的策略可使JPA在自动创建表时，命名格式与spring boot默认的一致，使用方式如下：
 * <p>
 * properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
 * properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
 * </p>
 * Created by HeLei on 2021/9/3.
 */
package wlei.candy.jpa.namingstrategy;