package boylen.agent.mybatis.agent.core.service;

import boylen.agent.mybatis.agent.core.properties.AgentProperties;
import lombok.Data;


public class ConfigAgentService {
    private AgentProperties agentProperties;

    public ConfigAgentService(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }

    public AgentProperties getAgentProperties() {
        return agentProperties;
    }
}
