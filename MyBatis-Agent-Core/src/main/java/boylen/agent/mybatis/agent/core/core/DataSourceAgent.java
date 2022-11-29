package boylen.agent.mybatis.agent.core.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DataSourceAgent extends AbstractRoutingDataSource {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceAgent.class);

    private static Map<Object, Object> dataSourceMap = new HashMap<>();


    @Override
    public void afterPropertiesSet() {
        Map<Object, Object> dataSourceMapTarget = dataSourceMap;

        setDefaultTargetDataSource(DataSourceLocal.getDataSource());
        setDefaultTargetDataSource(getDefaultDataSource());

        if (DataSourceLocal.getDataSourceName() != null) {
            dataSourceMapTarget.put(DataSourceLocal.getDataSourceName(), DataSourceLocal.getDataSource());
        }
        // AbstractRoutingDataSource会将"determineCurrentLookupKey的Key"去取"setTargetDataSources的Map"，取出来的DataSource就是目标DataSource
        setTargetDataSources(dataSourceMapTarget);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = DataSourceLocal.getDataSourceName();
        if (dataSourceName == null) {
            logger.debug("空数据源");
            throw new IllegalArgumentException("空数据源");
        }
        return dataSourceName;
    }

    public static void putDataSource(String name, DataSource dataSource) {
        dataSourceMap.put(name, dataSource);
    }

    public static DataSource getDataSource(Object name) {
        return (DataSource) dataSourceMap.get(name);
    }

    public DataSourceAgent(Map<String, DataSource> map) {
        for (String key : map.keySet()) {
            putDataSource(key, map.get(key));
        }
    }

    /**
     * 获取dataSourceMap的key集合
     */
    public static Set<Object> getSourceMapKeySet() {
        Set<Object> objects = dataSourceMap.keySet();
        return objects;
    }

    /**
     * 获取默认数据源，目前是选Map的第一个DataSource
     * TODO
     */
    public DataSource getDefaultDataSource() {
        for (Object key : dataSourceMap.keySet()) {
            return (DataSource) dataSourceMap.get(key);
        }
        return null;
    }
}
