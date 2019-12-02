package com.cplier.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig implements TransactionManagementConfigurer {
  private final DataSource dataSource;

  public DatabaseConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public PlatformTransactionManager annotationDrivenTransactionManager() {
    return transactionManager();
  }

  private PlatformTransactionManager transactionManager() {
    DataSourceTransactionManager txManager = new DataSourceTransactionManager();
    txManager.setDataSource(dataSource);
    return txManager;
  }
}
