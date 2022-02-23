package br.com.lab.consumerinvestimentdone.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerInvestmentDoneConfig {

    @Value("${application.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${application.rabbitmq.user}")
    private String rabbitmqUser;

    @Value("${application.rabbitmq.password}")
    private String rabbitmqPassword;

    @Bean
    public ConnectionFactory rabbitmqConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(5672);
        connectionFactory.setUsername(rabbitmqUser);
        connectionFactory.setPassword(rabbitmqPassword);

        return connectionFactory;
    }
}
