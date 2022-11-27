package boylen.agent.mybatis.agent.core.core;

import javax.sql.DataSource;


public class DataSourceLocal {
    private static ThreadLocal<DataSource> dataSourceLocal = new ThreadLocal<>();

    private static ThreadLocal<String> dataSourceNameLocal = new ThreadLocal<>();

    static {
        setDefaultDataSource();
    }

    public static void setDataSource(String name, DataSource dataSource){
        dataSourceNameLocal.set(name);
        dataSourceLocal.set(dataSource);
    }

    public static DataSource getDataSource(){
        return dataSourceLocal.get();
    }

    public static String getDataSourceName(){
        return dataSourceNameLocal.get();
    }

    public static void setDefaultDataSource(){

    }
}
