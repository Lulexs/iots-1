package com.iots.DataManager.publisher.impl;

import com.iots.DataManager.publisher.MqttPublisher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttPublisherImpl implements MqttPublisher {
    private MqttClient client;

    @Value("${mqtt.broker.url}")
    private String broker;

    @Value("${mqtt.client.id}")
    private String clientId;

    @PostConstruct
    public void init() {
        int retries = 10;
        while (retries > 0) {
            try {
                System.out.println("Broker, cliendId " + broker + "\\\\\\///" + clientId);
                client = new MqttClient(broker, clientId, new MemoryPersistence());
                client.connect();
                return;
            } catch (Exception e) {
                retries--;
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Failed to connect to MQTT broker after retries");
    }

    public void publish(String topic, String messageContent) {
        try {
            MqttMessage message = new MqttMessage(messageContent.getBytes());
            message.setQos(1);
            client.publish(topic, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish MQTT message", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
