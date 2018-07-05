package org.ozwillo.dcimporter.config;

import org.ozwillo.dcimporter.model.datacore.DCBusinessResourceLight;
import org.ozwillo.dcimporter.service.marchesecurise.rabbitMQ.ReceiverMS;
import org.ozwillo.dcimporter.service.marchesecurise.rabbitMQ.SenderMS;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.TopicExchange;

@Configuration
public class MarcheSecuriseRabbitMQ {
    private final String EXCHANGER_NAME = "dcimporter";

    @Bean
    public TopicExchange topic(){
        return new TopicExchange(EXCHANGER_NAME);
    }

    private static class ReceiverConfig{

        private final String QUEUE_MS_NAME = "marchesecurise";
        private final String KEY = "consultation.20003019500115.create";

        public String getKey(DCBusinessResourceLight dcConsultation, String baseKey, String action){
            String uri = dcConsultation.getUri().toString() ;   //TODO:Extraire siret de uri ?
            return baseKey +"." + uri + "." + action;
        }

        @Bean
        public ReceiverMS receiver(){
            return new ReceiverMS();
        }

        @Bean
        public Queue queueMS(){
            return new Queue(QUEUE_MS_NAME);
        }

        @Bean
        public Binding bindingTestConsultation(TopicExchange topic, Queue queueMS){
            return BindingBuilder.bind(queueMS).to(topic).with(KEY);
        }

    }

    @Bean
    public SenderMS sender(){
        return new SenderMS();
    }

}