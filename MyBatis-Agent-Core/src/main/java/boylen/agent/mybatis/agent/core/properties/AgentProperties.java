package boylen.agent.mybatis.agent.core.properties;

import boylen.agent.mybatis.agent.core.interceptor.AgentInterceptor;
import lombok.Data;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Data
public class AgentProperties {
    public static final String PREFIX_AGENT = "agent.properties";

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();


    /**
     * 是否打印sql
     */
    private boolean isPrintSql = false;

    private String[] mapperLocations;

    private String[] plugins;

    private Resource[] resources;

    // router的属性
    private boolean safeRowBoundsEnabled;
    private boolean safeResultHandlerEnabled = true;
    private boolean mapUnderscoreToCamelCase;
    private boolean aggressiveLazyLoading;
    private boolean multipleResultSetsEnabled = true;
    private boolean useGeneratedKeys;
    private boolean useColumnLabel = true;
    private boolean cacheEnabled = true;
    private boolean callSettersOnNulls;
    private boolean useActualParamName = true;
    private boolean returnInstanceForEmptyRow;

    public Resource[] initMapperLocations() throws IOException {
        List<Resource[]> resourceList = new ArrayList<>();
        if (mapperLocations != null && mapperLocations.length > 0) {
            int length = 0;
            for (String mapperLocation : mapperLocations) {
                Resource[] resolveResources = resourceResolver.getResources(mapperLocation);
                resourceList.add(resolveResources);
                length += resolveResources.length;
            }
            resources = new Resource[length];
            int index = 0;
            for (Resource[] resolveResources : resourceList) {
                for (Resource resolveResource : resolveResources) {
                    resources[index++] = resolveResource;
                }
            }
        } else {
            resources = resourceResolver.getResources("classpath*:/mapper/*Mapper.xml");
        }
        return resources;
    }

    /**
     * 初始化自定义的拦截器，用于SqlSessionFactory加载plugin
     */
    public Interceptor[] initInterceptors() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (plugins != null && plugins.length != 0) {
            Interceptor[] interceptors = new Interceptor[plugins.length];
            for (int i = 0; i < plugins.length; i++) {
                Class<?> clazz = Class.forName(plugins[i]);
                Interceptor interceptor = (Interceptor) clazz.newInstance();
                interceptors[i] = interceptor;
            }
            return interceptors;
        } else {
            return new Interceptor[]{new AgentInterceptor()};
        }
    }
}
