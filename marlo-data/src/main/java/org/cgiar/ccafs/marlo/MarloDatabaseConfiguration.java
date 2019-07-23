/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;


/**
 * Configuration for Database and Flyway beans.
 */
@Configuration
public class MarloDatabaseConfiguration {

  /**
   * MARLO properties are now accesible via the spring @Value annotation
   */
  @Value("${mysql.url}")
  private String databaseUrl;

  @Value("${mysql.user}")
  private String databaseUser;

  @Value("${mysql.password}")
  private String password;

  @Value("${mysql.database}")
  private String databaseSchema;

  @Value("${mysql.show_sql}")
  private Boolean showSql;

  private final Logger log = LoggerFactory.getLogger(MarloDatabaseConfiguration.class);

  @Bean(name = "dataSource", destroyMethod = "close")
  public DataSource getDataSource() {

    HikariConfig config = new HikariConfig();

    // config.setDriverClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
    config.setDriverClassName("com.mysql.jdbc.Driver");
    config.setJdbcUrl(databaseUrl);
    config.setUsername(databaseUser);
    config.setPassword(password);
    // config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

    config.setMaximumPoolSize(40);
    config.setMinimumIdle(20);
    config.setIdleTimeout(5000);
    config.setConnectionTimeout(900000);
    // config.setConnectionTestQuery("SELECT 1");
    // hard code for now
    config.addDataSourceProperty("cachePrepStmts", true);
    config.addDataSourceProperty("prepStmtCacheSize", 250);
    config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    config.addDataSourceProperty("useServerPrepStmts", true);

    config.addDataSourceProperty("autoReconnect", true);
    config.addDataSourceProperty("useSSL", false);


    HikariDataSource dataSource = new HikariDataSource(config);
    return dataSource;
  }

  /**
   * Leave commented out for now.
   * 
   * @return
   */
  // @Bean
  // public PersistenceExceptionTranslationPostProcessor getExceptionTranslation() {
  // return new PersistenceExceptionTranslationPostProcessor();
  // }

  @Bean(name = "sessionFactory")
  public SessionFactory getSessionFactory(final DataSource dataSource) {
    log.info("Setting SessionFactory");
    LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
    sessionBuilder.configure("hibernate.cfg.xml");
    sessionBuilder.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
    if (showSql) {
      sessionBuilder.setProperty(Environment.SHOW_SQL, "true");
    }

    // Enable second level cache with ehcache
    sessionBuilder.setProperty("hibernate.cache.use_second_level_cache", Boolean.TRUE.toString());
    sessionBuilder.setProperty("hibernate.cache.region.factory_class",
      "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");

    // // Enable query cache
    sessionBuilder.setProperty("hibernate.cache.use_query_cache", Boolean.TRUE.toString());

    SessionFactory sessionFactory = sessionBuilder.buildSessionFactory();
    return sessionFactory;
  }


  @Bean(name = "transactionManager")
  public HibernateTransactionManager getTransactionManager(final SessionFactory sessionFactory) {
    final HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
    return transactionManager;
  }

}
