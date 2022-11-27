package boylen.agent.mybatis.agent.autoconfigration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = DataSourceProperties.PREFIX_DATASOURCE)
public class DataSourceProperties {
    public static final String PREFIX_DATASOURCE = "agent.datasource";

    private String[] jdbcUrl;

    private String[] driverClassName;

    private String[] username;

    private String[] password;

    // Get/Set
    public String[] getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String[] jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String[] getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String[] driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String[] getUsername() {
        return username;
    }

    public void setUsername(String[] username) {
        this.username = username;
    }

    public String[] getPassword() {
        return password;
    }

    public void setPassword(String[] password) {
        this.password = password;
    }
}
