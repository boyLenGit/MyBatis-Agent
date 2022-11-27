package boylen.agent.mybatis.agent.autoconfigration.config;

import boylen.agent.mybatis.agent.autoconfigration.properties.AgentPropertiesE;
import boylen.agent.mybatis.agent.autoconfigration.properties.DataSourceProperties;
import boylen.agent.mybatis.agent.core.core.DataSourceAgent;
import boylen.agent.mybatis.agent.core.interceptor.AgentInterceptor;
import boylen.agent.mybatis.agent.core.properties.AgentProperties;
import boylen.agent.mybatis.agent.core.service.ConfigAgentService;
import boylen.agent.mybatis.agent.core.service.InitAgentService;
import boylen.agent.mybatis.agent.core.util.SpringTool;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties({AgentPropertiesE.class, DataSourceProperties.class})
public class AutoConfiguration {
    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private AgentProperties agentProperties;

    @Bean
    public DataSourceAgent dataSourceAgent() {
        Map<String, DataSource> dataSourceMap = getDataSourceMapByProperties();
        return new DataSourceAgent(dataSourceMap);
    }

    @Bean
    public AgentInterceptor agentInterceptor() {
        return new AgentInterceptor();
    }

    @Bean
    public InitAgentService initMyBatisAgentService() {
        return new InitAgentService();
    }

    @Bean
    public ConfigAgentService configAgentService() {
        return new ConfigAgentService(agentProperties);
    }

    @Bean
    public SpringTool springTool() {
        return new SpringTool();
    }

//    @Bean
//    public SqlSessionFactory sqlSessionFactory(org.apache.ibatis.session.Configuration configuration) throws Exception {
//        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//        // DataSource
//        Map<String, DataSource> dataSourceMap = getDataSourceMapByProperties();
//        for (String key : dataSourceMap.keySet()) {
//            if (dataSourceMap.get(key) != null) {
//                sessionFactoryBean.setDataSource(dataSourceMap.get(key));
//                break;
//            }
//        }
//        // mapper
//        Resource[] resources = agentProperties.initMapperLocations();
//        if (resources.length > 0) {
//            sessionFactoryBean.setMapperLocations(resources);
//        }
//        // Interceptor拦截器
//        Interceptor[] interceptors = agentProperties.initInterceptors();
//        if (interceptors.length > 0){
//            sessionFactoryBean.setPlugins(interceptors);
//        }
//        // Configuration
//        sessionFactoryBean.setConfiguration(configuration);
//        return sessionFactoryBean.getObject();
//    }

    @Bean
    @ConditionalOnMissingBean
    public org.apache.ibatis.session.Configuration configuration() {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
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
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (int i = 0; i < length; i++) {
            HikariConfig dataSourceConfig = new HikariConfig();
            dataSourceConfig.setJdbcUrl(jdbcUrl[jdbcUrl.length == 1 ? 0 : i]);
            dataSourceConfig.setUsername(username[username.length == 1 ? 0 : i]);
            dataSourceConfig.setPassword(password[password.length == 1 ? 0 : i]);
            dataSourceConfig.setDriverClassName(driverClassName[driverClassName.length == 1 ? 0 : i]);
            HikariDataSource dataSource = new HikariDataSource(dataSourceConfig);
            String databaseName = getDatabaseNameFromJdbcUrl(jdbcUrl[jdbcUrl.length == 1 ? 0 : i]);
            if (databaseName == null) {
                continue;
            }
            dataSourceMap.put(databaseName, dataSource);
        }
        return dataSourceMap;
    }
}
