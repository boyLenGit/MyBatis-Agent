package boylen.agent.mybatis.agent.autoconfigration.config;

import boylen.agent.mybatis.agent.autoconfigration.properties.AgentPropertiesE;
import boylen.agent.mybatis.agent.autoconfigration.properties.DataSourceProperties;
import boylen.agent.mybatis.agent.core.core.DataSourceAgent;
import boylen.agent.mybatis.agent.core.properties.AgentProperties;
import boylen.agent.mybatis.agent.core.service.ConfigAgentService;
import boylen.agent.mybatis.agent.core.service.InitAgentService;
import boylen.agent.mybatis.agent.core.util.SpringTool;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE) // 希望我们的自动配置类优先于其他自动配置候选类
@EnableConfigurationProperties({AgentPropertiesE.class, DataSourceProperties.class})
public class AutoConfiguration {
    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private AgentProperties agentProperties;

    /**
     * 注入DataSourceAgent代理器
     */
    @Bean
    public DataSource dataSourceAgent() {
        // 提取配置文件内容
        Map<String, DataSource> dataSourceMap = getDataSourceMapByProperties();
        return new DataSourceAgent(dataSourceMap);
    }


    /**
     * 注入初始化服务
     */
    @Bean
    public InitAgentService initMyBatisAgentService() {
        return new InitAgentService();
    }

    /**
     * 注入配置服务
     */
    @Bean
    public ConfigAgentService configAgentService() {
        return new ConfigAgentService(agentProperties);
    }

    @Bean
    public SpringTool springTool() {
        return new SpringTool();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(MybatisConfiguration configuration, DataSource dataSourceAgent) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        // DataSource 我注入的其实是我自己写的DataSourceAgent
        sessionFactoryBean.setDataSource(dataSourceAgent);
        // mapper
        Resource[] resources = agentProperties.initMapperLocations();
        if (resources.length > 0) {
            sessionFactoryBean.setMapperLocations(resources);
        }
        // Interceptor拦截器
        Interceptor[] interceptors = agentProperties.initInterceptors();
        if (interceptors.length > 0){
            sessionFactoryBean.setPlugins(interceptors);
        }
        // Configuration
        sessionFactoryBean.setConfiguration(configuration);
        return sessionFactoryBean.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisConfiguration configuration() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setSafeRowBoundsEnabled(agentProperties.isSafeRowBoundsEnabled());
        configuration.setSafeResultHandlerEnabled(agentProperties.isSafeResultHandlerEnabled());
        configuration.setMapUnderscoreToCamelCase(agentProperties.isMapUnderscoreToCamelCase());
        configuration.setAggressiveLazyLoading(agentProperties.isAggressiveLazyLoading());
        configuration.setMultipleResultSetsEnabled(agentProperties.isMultipleResultSetsEnabled());
        configuration.setUseGeneratedKeys(agentProperties.isUseGeneratedKeys());
        configuration.setUseColumnLabel(agentProperties.isUseColumnLabel());
        configuration.setCacheEnabled(agentProperties.isCacheEnabled());
        configuration.setCallSettersOnNulls(agentProperties.isCallSettersOnNulls());
        configuration.setUseActualParamName(agentProperties.isUseActualParamName());
        configuration.setReturnInstanceForEmptyRow(agentProperties.isReturnInstanceForEmptyRow());
        return configuration;
    }

    public String getDatabaseNameFromJdbcUrl(String jdbcUrl) {
        String databaseName = null;
        int indexOfSign = jdbcUrl.indexOf('?');
        for (int j = indexOfSign - 1; j >= 0; j--) {
            if (jdbcUrl.charAt(j) == '/') {
                databaseName = jdbcUrl.substring(j + 1, indexOfSign);
                break;
            }
        }
        return databaseName;
    }

    public Map<String, DataSource> getDataSourceMapByProperties() {
        String[] jdbcUrl = dataSourceProperties.getJdbcUrl();
        String[] username = dataSourceProperties.getUsername();
        String[] password = dataSourceProperties.getPassword();
        String[] driverClassName = dataSourceProperties.getDriverClassName();
        int length = Math.max(Math.max(jdbcUrl.length, username.length), Math.max(password.length, driverClassName.length));
        // 生成DataSource
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (int i = 0; i < length; i++) {
            HikariConfig dataSourceConfig = new HikariConfig();
            dataSourceConfig.setJdbcUrl(jdbcUrl[jdbcUrl.length == 1 ? 0 : i]);
            dataSourceConfig.setUsername(username[username.length == 1 ? 0 : i]);
            dataSourceConfig.setPassword(password[password.length == 1 ? 0 : i]);
            dataSourceConfig.setDriverClassName(driverClassName[driverClassName.length == 1 ? 0 : i]);
            HikariDataSource dataSource = new HikariDataSource(dataSourceConfig); // 数据源连接池
            // 获取数据库的名字
            String databaseName = getDatabaseNameFromJdbcUrl(jdbcUrl[jdbcUrl.length == 1 ? 0 : i]);
            // 空值检查
            if (databaseName == null) {
                continue;
            }
            // 存储DataSource
            dataSourceMap.put(databaseName, dataSource);
        }
        return dataSourceMap;
    }
}
