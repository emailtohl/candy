package wlei.candy.jpa.envers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import wlei.candy.jpa.event.EntityStateEventPublisher;
import wlei.candy.jpa.namingstrategy.SpringImplicitNamingStrategy;
import wlei.candy.jpa.namingstrategy.SpringPhysicalNamingStrategy;
import wlei.candy.jpa.tx.TxService;
import wlei.candy.jpa.tx.TxServiceImpl;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@ComponentScan
@EnableTransactionManagement
@EnableCaching
@EnableJpaRepositories(basePackages = {"wlei.candy.jpa.envers.matches.repo", "wlei.candy.jpa.envers.auction.repo"},
    repositoryImplementationPostfix = "ExtImpl",
    transactionManagerRef = "annotationDrivenTransactionManager",
    entityManagerFactoryRef = "entityManagerFactory")
class SpringConf {

  public static final String POSTGRES = "postgres";

  @Bean
  public DataSource embeddedDataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        // .addScripts("classpath:test-data.sql")
        .build();
  }

  @Profile(POSTGRES)
  @Primary
  @Bean
  public DataSource postgres() {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/candy");
    hikariConfig.setDriverClassName("org.postgresql.Driver");
    hikariConfig.setUsername("postgres");
    hikariConfig.setPassword("123456");
    return new HikariDataSource(hikariConfig);
  }

  @Bean
  public JpaVendorAdapter h2Adapter() {
    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setDatabase(Database.H2);
    adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
    // 此处配置已经不再生效
    adapter.setShowSql(false);
    adapter.setGenerateDdl(false);
    return adapter;
  }

  @Profile(POSTGRES)
  @Primary
  @Bean
  public JpaVendorAdapter postgresAdapter() {
    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setDatabase(Database.POSTGRESQL);
    adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
    // 此处配置已经不再生效
    adapter.setShowSql(false);
    adapter.setGenerateDdl(false);
    return adapter;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
    LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
    emfb.setDataSource(dataSource);
    emfb.setJpaVendorAdapter(jpaVendorAdapter);
    // 实际上hibernate可以扫描类路径下有JPA注解的实体类，但是JPA规范并没有此功能，所以最好还是告诉它实际所在位置
    emfb.setPackagesToScan("wlei.candy.jpa.envers.matches.entities", "wlei.candy.jpa.envers.auction.entities");
    Map<String, Object> properties = jpaConf();
    emfb.setJpaPropertyMap(properties);
    return emfb;
  }

  private Map<String, Object> jpaConf() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.use_sql_comments", "true");
    properties.put("hibernate.hbm2ddl.auto", "create-drop");
    // 测试时打印的参数通过log4j2-test.xml生效
    properties.put("hibernate.show_sql", "false");
    properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
    properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
    // ID仍然取值于hibernate_sequence序列
    properties.put("hibernate.id.db_structure_naming_strategy", "legacy");
    properties.put("hibernate.search.backend.type", "lucene");
//    properties.put("hibernate.search.backend.directory.root", "some/filesystem/path");
    // Store the index in the local JVM heap. Local heap directories and all contained indexes are lost when the JVM shuts down
    properties.put("hibernate.search.backend.directory.type", "local-heap");
    return properties;
  }

  @Bean(name = "annotationDrivenTransactionManager")
  public PlatformTransactionManager jpaTransactionManager(LocalContainerEntityManagerFactoryBean f) {
    return new JpaTransactionManager(Objects.requireNonNull(f.getObject()));
  }

  @Bean
  public EntityStateEventPublisher entityStateEventPublisher(ApplicationEventPublisher publisher) {
    return new EntityStateEventPublisher(publisher);
  }

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager();
  }

  @Bean
  public TxService txService() {
    return new TxServiceImpl();
  }
}
