package com.example.kafkatesting;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.AppInfoParser;
import org.json.JSONObject;


import java.util.Properties;

public class KafkaProducerExample {
    private static final String KAFKA_BROKER_ADDRESS = "http://192.168.0.172:9092";
    private static final String TOPIC_NAME = "warehouse_events";

    public void sendMessage(JSONObject data) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER_ADDRESS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put("app.info.parser", CustomAppInfoParser.class.getName());

        KafkaProducer<String, JSONObject> producer = new KafkaProducer<>(props);
        ProducerRecord<String, JSONObject> record = new ProducerRecord<>(TOPIC_NAME, data);

        producer.send(record);
        producer.close();
    }

    class CustomAppInfoParser extends AppInfoParser implements com.example.kafkatesting.CustomAppInfoParser {
        @Override
        public void unregisterAppInfo(String appId, String appName) {
            // Implement your custom logic here
            // For example, you can log a message or perform some other action
            System.out.println("Unregistering app info for " + appId + " - " + appName);
        }
    }
}

