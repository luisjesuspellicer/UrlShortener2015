package urlshortener.bangladeshgreen.warningQueue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configures beans for managing the "warningQueue" queue.
 * It registers the queue, listener, threads pool and periodic check (thread).
 * Author: bangladesh-green
 */
@Configuration
@EnableScheduling
public class QueueConfigurationWarning {

    @Bean
    // Register the desired queue
    public Queue warningQueue() {
        return new Queue("warningQueue");
    }

    @Bean
    // Register the listener that takes messages from queue
    public ListenerWarning listenerWarning(){
        return new ListenerWarning();
    }

    @Bean
    // Periodic check for available URIs (in one thread).
    public PeriodicCheckWarning periodicCheckWarning() {
        return new PeriodicCheckWarning();
    }

    @Bean
    // Thread pool of the queue. It takes threads (workers) from this pool to do certain tasks.
    public TaskExecutor warningExecutor() {
        ThreadPoolTaskExecutor taskExecutorWarning = new ThreadPoolTaskExecutor();
        // If the queue is full (MAX_INT_VALUE), then it can create this number of threads.
        taskExecutorWarning.setMaxPoolSize(10);
        // It can be X process concurrently.
        taskExecutorWarning.setCorePoolSize(10);
        taskExecutorWarning.afterPropertiesSet();
        return taskExecutorWarning;
    }

}
