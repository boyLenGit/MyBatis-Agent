package boylen.agent.mybatis.agent.core.interceptor;

import boylen.agent.mybatis.agent.core.core.DataSourceAgent;
import boylen.agent.mybatis.agent.core.core.DataSourceLocal;
import boylen.agent.mybatis.agent.core.core.DataTableAdapter;
import boylen.agent.mybatis.agent.core.core.SourceAgent;
import boylen.agent.mybatis.agent.core.service.ConfigAgentService;
import boylen.agent.mybatis.agent.core.util.ReflectUtils;
import boylen.agent.mybatis.agent.core.util.SpringTool;
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
        //判断当前是否有实际事务处于活动状态。true=是
        boolean synchronizationActive = TransactionSynchronizationManager.isActualTransactionActive();
        // 获取参数
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        // 执行的mapper方法的全路径名。例如：len.feature.sqlagent.dao.UserMapper.getUserStatus
        String mapperId = mappedStatement.getId();
        // 打印SQL语句
        printSql(mappedStatement, args);
        // 获取 sqlCommandType: UNKNOWN, INSERT, UPDATE, DELETE, SELECT, FLUSH
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 反射获取方法与类
        Method mapperMethod = getMapperMethod(mapperId);
        Class mapperClass = getMapperClass(mapperId);
        // 判别代理场景：
        if (mapperMethod != null && mapperClass != null) {
            // ↓ 对方法注解进行数据源配置。优先级：方法注解>类注解>无注解
            if (mapperMethod.isAnnotationPresent(SourceAgent.class) && !"".equals(mapperMethod.getAnnotation(SourceAgent.class).database())) {
                // 选择注解指定的DataSource
                String database = mapperMethod.getAnnotation(SourceAgent.class).database();
                DataSource dataSource = DataSourceAgent.getDataSource(database);
                if (dataSource == null) {
                    System.out.println("intercept" + "DataSource不存在！");
                }
                DataSourceLocal.setDataSource(database, dataSource);
            }
            // ↓ 对类注解进行数据源配置
            else if (mapperClass.isAnnotationPresent(SourceAgent.class) && !"".equals(((SourceAgent) mapperClass.getAnnotation(SourceAgent.class)).database())) {
                // 选择注解指定的DataSource
                String database = ((SourceAgent) mapperClass.getAnnotation(SourceAgent.class)).database();
                DataSource dataSource = DataSourceAgent.getDataSource(database);
                if (dataSource == null) {
                    System.out.println("intercept" + "DataSource不存在！");
                }
                DataSourceLocal.setDataSource(database, dataSource);
            } else {
                // 自适应匹配对应的DataSource
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
        if (configAgentService == null){
            configAgentService = SpringTool.getBean(ConfigAgentService.class);
        }
        if (configAgentService.getAgentProperties().isPrintSql()) {
            // 获取 SQL
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
        // 非空判断
        if (StringUtils.isEmpty(methodName) || StringUtils.isEmpty(className)) {
            return null;
        }
        // 获取方法
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
        // 非空判断
        if (StringUtils.isEmpty(className)) {
            return null;
        }
        // 获取方法
        return ReflectUtils.getClass(className);
    }
}
