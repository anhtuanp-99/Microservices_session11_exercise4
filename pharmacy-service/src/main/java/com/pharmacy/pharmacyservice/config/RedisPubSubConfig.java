package com.pharmacy.pharmacyservice.config;

import com.pharmacy.pharmacyservice.subscriber.PharmacyAlertSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisPubSubConfig {

    public static final String CHANNEL = "pharmacy-alerts";

    /**
     * ChannelTopic — đại diện cho channel "pharmacy-alerts".
     * Có thể tạo nhiều topic cho nhiều channel khác nhau.
     */
    @Bean
    public ChannelTopic pharmacyAlertTopic() {
        return new ChannelTopic(CHANNEL);
    }

    /**
     * MessageListenerAdapter — bọc Subscriber bean để container gọi khi có message.
     * "onMessage" là tên method của Subscriber sẽ được gọi.
     */
    @Bean
    public MessageListenerAdapter pharmacyAlertListenerAdapter(
            PharmacyAlertSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    /**
     * RedisMessageListenerContainer — container quản lý việc subscribe.
     * Đây là "cầu nối" giữa Redis channel và Subscriber bean.
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter pharmacyAlertListenerAdapter,
            ChannelTopic pharmacyAlertTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(pharmacyAlertListenerAdapter, pharmacyAlertTopic);
        return container;
    }
}