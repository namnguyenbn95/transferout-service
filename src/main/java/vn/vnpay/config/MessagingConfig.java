package vn.vnpay.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {
    @Value("${rabbitmq.queue}")
    private String QUEUE;
    @Value("${rabbitmq.exchange}")
    public static String EXCHANGE = "transBatchEchange";
    @Value("${rabbit.routingKey}")
    public static String ROUTING_KEY = "transBatchRoutingkey";
    @Value("${RABBITMQ_HOST}")
    private String host;
    @Value("${RABBITMQ_PORT}")
    private String port;
    @Value("${RABBITMQ_USERNAME}")
    private String username;
    @Value("${RABBITMQ_PASSWORD}")
    private String password;
    @Value("${RABBITMQ_VIRTUALHOST}")
    private String virtualHost;
    @Value("${RABBITMQ_HEARTBEAT}")
    private String requestedHeartBeat;

    @Bean
    public Queue queue1() {
        return new Queue(QUEUE);
    }

    @Bean(value = "exchange1")
    public TopicExchange exchange1() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding1(@Qualifier("queue1") Queue queue, @Qualifier("exchange1") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter1() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public CachingConnectionFactory firstConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, Integer.parseInt(port));
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setRequestedHeartBeat(Integer.parseInt(requestedHeartBeat));
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean(value = "firstRabbitTemplate")
    public RabbitTemplate firstRabbitTemplate() {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(firstConnectionFactory());
        rabbitTemplate.setMessageConverter(converter1());
        return rabbitTemplate;
    }

}