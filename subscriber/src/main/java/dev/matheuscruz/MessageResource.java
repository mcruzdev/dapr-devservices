package dev.matheuscruz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/pubsub")
@ApplicationScoped
public class MessageResource {

    static final Logger LOGGER = LoggerFactory.getLogger(MessageResource.class);

    @Topic(name = "${dapr.topicName}", pubsubName = "pubsub")
    @Path("/topic")
    @POST
    public void subscriber(CloudEvent<String> event) {
        LOGGER.info("Message from PubSub {}", event.getData());
    }
}
