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


/**
 * @author mabolun
 * @date 2022/11/29
 */
public class DataTableAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DataTableAdapter.class);

    /**
     * 数据表名-DataSource
     */
    private static Map<String, DataSource> tableToDataSourceMap = new HashMap<>();

    /**
     * 数据表名-Database名
     */
    private static Map<String, String> tableToDatabaseMap = new HashMap<>();


    /**
     * 获取指定DataSource下的所有数据表，从而生成"数据表-DataSource"对应关系，用于自适应数据源功能
     */
    public static void initDataTableAdapter() throws SQLException {
        tableToDataSourceMap = new HashMap<>();
        tableToDatabaseMap = new HashMap<>();
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) SpringTool.getBean("sqlSessionFactory");
        String sql = "select table_name name from information_schema.tables where TABLE_SCHEMA='{DATABASE}'";
        for (Object key : DataSourceAgent.getSourceMapKeySet()) {
            String sqlExecute = sql.replace("{DATABASE}", key.toString());
            DataSourceLocal.setDataSource(key.toString(), DataSourceAgent.getDataSource(key));
            ResultSet resultSet = sqlSessionFactory.openSession().getConnection().createStatement().executeQuery(sqlExecute);
            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                tableToDataSourceMap.put(tableName, DataSourceAgent.getDataSource(key));
                tableToDatabaseMap.put(tableName, key.toString());
            }
        }
        logger.info("自适应数据表DataTableAdapter初始化完毕，数据表数量：" + tableToDataSourceMap.size());
    }

    public DataSource getDataSourceByTableName(String tableName) {
        DataSource dataSource = tableToDataSourceMap.get(tableName);
        if (dataSource == null) {
            try {
                initDataTableAdapter();
            } catch (SQLException e) {
                logger.debug("initTableSourceMap异常：" + e);
            }
            dataSource = tableToDataSourceMap.get(tableName);
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

    /**
     * 通过数据表名来配置DataSource，将其配置进DataSourceLocal中
     */
    public static void setDataSourceByTableName(String tableName) throws SQLException {
        DataSource dataSource = tableToDataSourceMap.get(tableName);
        String databaseName = tableToDatabaseMap.get(tableName);
        // 如果dataSource，先刷新一下数据表-DataSource，防止初始化后增加了数据表；如果还没有，说明确实是没有
        if (dataSource == null) {
            try {
                initDataTableAdapter();
            } catch (SQLException e) {
                logger.debug("initTableSourceMap异常：" + e);
            }
            dataSource = tableToDataSourceMap.get(tableName);
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
