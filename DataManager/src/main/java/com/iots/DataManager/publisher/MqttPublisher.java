package com.iots.DataManager.publisher;

public interface MqttPublisher {
    void publish(String topic, String messageContent);
}