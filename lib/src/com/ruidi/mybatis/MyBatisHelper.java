package com.ruidi.mybatis;

import com.ruidi.mybatis.model.Account;
import com.ruidi.mybatis.model.Currency;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class MyBatisHelper {
    private static SqlSessionFactory sqlSessionFactory;
    private  static DataSource getDataSource(Properties prop) {
        String driver = prop.getProperty("driver");
        String url = prop.getProperty("url");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        return new PooledDataSource(driver, url, username, password);
    }
    public static void init(Properties p){
        DataSource dataSource = getDataSource(p);
        initSqlSessionFactory(dataSource);
    }
    public static SqlSessionFactory instance(){
        return sqlSessionFactory;
    }
    private static void   initSqlSessionFactory(DataSource dataSource){
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);

        Configuration configuration = new Configuration(environment);
//        configuration.setLazyLoadingEnabled(true);
//        configuration.setEnhancementEnabled(true);

        configuration.getTypeAliasRegistry().registerAliases("com/ruidi/mybatis/model");
        configuration.addMappers("com/ruidi/mybatis/dao");

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = builder.build(configuration);
    }
}  