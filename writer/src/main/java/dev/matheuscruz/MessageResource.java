package dev.matheuscruz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JacksonInject.Value;

import dev.matheuscruz.MessageResource.Values;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import io.dapr.client.domain.State;
import io.quarkiverse.dapr.core.SyncDaprClient;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/messages")
public class MessageResource {

    static final Logger LOGGER = LoggerFactory.getLogger(MessageResource.class);
    static final String VALUES_KEY_NAME = "values";
    SyncDaprClient dapr;
    String stateStoreName;
    String topicName;

    public MessageResource(
            final SyncDaprClient dapr,
            @ConfigProperty(name = "dapr.stateStoreName") final String stateStoreName,
            @ConfigProperty(name = "dapr.topicName") final String topicName) {
        this.dapr = dapr;
        this.stateStoreName = stateStoreName;
        this.topicName = topicName;
    }

    @POST
    public Response message(@QueryParam("message") String message) {
        LOGGER.info("[application:write] Storing new message: {}", message);

        State<Values> state = dapr.getState(stateStoreName, VALUES_KEY_NAME, Values.class);
        Values values = state.getValue();
        if (values == null) {
            Values newValues = new Values(List.of(message));
            dapr.saveState(stateStoreName, VALUES_KEY_NAME, newValues);
        } else {
            values.getValues().add(message);
            dapr.saveState(stateStoreName, VALUES_KEY_NAME, values);
        }

        dapr.publishEvent(topicName, message);

        return Response.ok().build();
    }

    @GET
    public Values reader() {
        try {
            State<Values> state = dapr.getState(this.stateStoreName, VALUES_KEY_NAME, Values.class);
            return state.getValue();
        } catch (Exception e) {
            LOGGER.error("Error while getting messages", e);
            return new Values();
        }
    }

    @POST
    @Topic(name = "${dapr.topicName}", pubsubName = "pubsub")
    @Path("/topic")
    public Response subscriber(CloudEvent<String> event) {
        LOGGER.info("[application:writer] Receiving CloudEvent from Dapr {}", event.getData());
        return Response.ok().build();
    }

    @RegisterForReflection
    static class Values {
        List<String> values = new ArrayList<>();

        public Values() {
        }

        public Values(List<String> values) {
            this.values = values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        public List<String> getValues() {
            return values;
        }
    }
}
