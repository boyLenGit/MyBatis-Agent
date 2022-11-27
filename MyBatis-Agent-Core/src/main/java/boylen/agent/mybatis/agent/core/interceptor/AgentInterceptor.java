package boylen.agent.mybatis.agent.core.interceptor;

import boylen.agent.mybatis.agent.core.core.DataSourceAgent;
import boylen.agent.mybatis.agent.core.core.DataSourceLocal;
import boylen.agent.mybatis.agent.core.core.DataTableAdapter;
import boylen.agent.mybatis.agent.core.core.SourceAgent;
import boylen.agent.mybatis.agent.core.service.ConfigAgentService;
import boylen.agent.mybatis.agent.core.util.ReflectUtils;
import boylen.agent.mybatis.agent.core.util.SqlTool;
import boylen.agent.mybatis.agent.core.util.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Properties;


@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class AgentInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(AgentInterceptor.class);

    @Autowired
    private ConfigAgentService configAgentService;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        boolean synchronizationActive = TransactionSynchronizationManager.isActualTransactionActive();
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        String mapperId = mappedStatement.getId();
        printSql(mappedStatement, args);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        Method mapperMethod = getMapperMethod(mapperId);
        Class mapperClass = getMapperClass(mapperId);
        if (mapperMethod != null && mapperClass != null) {
            if (mapperMethod.isAnnotationPresent(SourceAgent.class) && !"".equals(mapperMethod.getAnnotation(SourceAgent.class).database())) {
                String database = mapperMethod.getAnnotation(SourceAgent.class).database();
                DataSource dataSource = DataSourceAgent.getDataSource(database);
                if (dataSource == null) {
                    System.out.println("intercept" + "DataSource不存在！");
                }
                DataSourceLocal.setDataSource(database, dataSource);
            } else if (mapperClass.isAnnotationPresent(SourceAgent.class) && !"".equals(((SourceAgent) mapperClass.getAnnotation(SourceAgent.class)).database())) {
                String database = ((SourceAgent) mapperClass.getAnnotation(SourceAgent.class)).database();
                DataSource dataSource = DataSourceAgent.getDataSource(database);
                if (dataSource == null) {
                    System.out.println("intercept" + "DataSource不存在！");
                }
                DataSourceLocal.setDataSource(database, dataSource);
            } else {
                String sqlRaw = getSql(mappedStatement, args);
                String tableName = SqlTool.getTableNameByRawSql(sqlRaw);
                DataTableAdapter.setDataSourceByTableName(tableName);
            }
        }

//        if (synchronizationActive){
//            System.out.println("处于事务中");
//        }else {
//            // 方法是否有注解，有则根据注解选择数据源；没有则根据表名选择数据源
//
//        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

    public void printSql(MappedStatement mappedStatement, Object[] args) {
        if (configAgentService.getAgentProperties().isPrintSql()) {
            BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(args[1]);
            String sql = boundSql.getSql().replace("[\\t\\n\\r]", " ");
            String[] sqlFormat = SqlTool.formatSqlWithParameter(sql, args[1]);
            logger.info("SQL语句：" + sqlFormat[0]);
            logger.info("SQL参数：" + sqlFormat[1]);
        }
    }

    public String getSql(MappedStatement mappedStatement, Object[] args) {
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(args[1]);
        return boundSql.getSql();
    }

    private Method getMapperMethod(String mapper) throws ClassNotFoundException {
        String methodName = null;
        String className = null;
        for (int i = mapper.length() - 1; i >= 0; i--) {
            if (mapper.charAt(i) == '.') {
                methodName = mapper.substring(i + 1);
                className = mapper.substring(0, i);
                break;
            }
        }
        if (StringUtils.isEmpty(methodName) || StringUtils.isEmpty(className)) {
            return null;
        }
        Method method = ReflectUtils.getMethodByName(ReflectUtils.getClass(className), methodName);
        return method;
    }

    private Class getMapperClass(String mapper) throws ClassNotFoundException {
        String className = null;
        for (int i = mapper.length() - 1; i >= 0; i--) {
            if (mapper.charAt(i) == '.') {
                className = mapper.substring(0, i);
                break;
            }
        }
        if (StringUtils.isEmpty(className)) {
            return null;
        }
        return ReflectUtils.getClass(className);
    }
}
