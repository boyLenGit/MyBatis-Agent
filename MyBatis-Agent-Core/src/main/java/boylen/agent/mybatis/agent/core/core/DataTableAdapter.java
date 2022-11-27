package boylen.agent.mybatis.agent.core.core;

import boylen.agent.mybatis.agent.core.util.SpringTool;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class DataTableAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DataTableAdapter.class);

    private static Map<String, DataSource> dataSourceMap = new HashMap<>();

    private static Map<String, String> databaseNameMap = new HashMap<>();


    public static void initTableSourceMap() throws SQLException {
        dataSourceMap.clear();
        databaseNameMap.clear();
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) SpringTool.getBean("sqlSessionFactory");
        String sql = "select table_name name from information_schema.tables where TABLE_SCHEMA='{DATABASE}'";
        for (Object key : DataSourceAgent.getSourceMapKeySet()) {
            String sqlExecute = sql.replace("{DATABASE}", key.toString());
            DataSourceLocal.setDataSource(key.toString(), DataSourceAgent.getDataSource(key));
            ResultSet resultSet = sqlSessionFactory.openSession().getConnection().createStatement().executeQuery(sqlExecute);
            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                dataSourceMap.put(tableName, DataSourceAgent.getDataSource(key));
                databaseNameMap.put(tableName, key.toString());
            }
        }
        logger.info("自适应数据表DataTableAdapter初始化完毕，数据表数量：" + dataSourceMap.size());
    }

    public DataSource getDataSourceByTableName(String tableName) {
        DataSource dataSource = dataSourceMap.get(tableName);
        if (dataSource == null) {
            try {
                initTableSourceMap();
            } catch (SQLException e) {
                logger.debug("initTableSourceMap异常：" + e);
            }
            dataSource = dataSourceMap.get(tableName);
            if (dataSource == null) {
                logger.debug("数据表对应的数据库不存在！数据表名：" + tableName);
                // TODO 异常
                return null;
            } else {
                return dataSource;
            }
        } else {
            return dataSource;
        }
    }

    public static void setDataSourceByTableName(String tableName) throws SQLException {
        DataSource dataSource = dataSourceMap.get(tableName);
        String databaseName = databaseNameMap.get(tableName);
        if (dataSource == null) {
            try {
                initTableSourceMap();
            } catch (SQLException e) {
                logger.debug("initTableSourceMap异常：" + e);
            }
            dataSource = dataSourceMap.get(tableName);
            if (dataSource == null) {
                logger.debug("数据表对应的数据库不存在！数据表名：" + tableName);
                throw new SQLException("数据表对应的数据库不存在！数据表名：" + tableName);
            } else {
                DataSourceLocal.setDataSource(databaseName, dataSource);
            }
        } else {
            DataSourceLocal.setDataSource(databaseName, dataSource);
        }
    }
}
