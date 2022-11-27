package boylen.agent.mybatis.agent.core.service;

import boylen.agent.mybatis.agent.core.core.DataTableAdapter;
import org.springframework.context.SmartLifecycle;

import java.sql.SQLException;


public class InitAgentService implements SmartLifecycle {
    private volatile boolean running = false;

    @Override
    public void start() {
        // 初始化
        try {
            DataTableAdapter.initTableSourceMap();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
