package dev.matheuscruz;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JacksonInject.Value;

import java.util.Collections;

import io.dapr.client.domain.State;
import io.quarkiverse.dapr.core.SyncDaprClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/messages")
public class MessageResource {

    static final Logger LOGGER = LoggerFactory.getLogger(MessageResource.class);
    static final String VALUES_KEY_NAME = "values";
    static final String STATE_STORE_NAME = "kvstore";
    SyncDaprClient dapr;

    public MessageResource(SyncDaprClient dapr) {
        this.dapr = dapr;
    }

    @GET
    public Values reader() {
        try {
            State<Values> state = dapr.getState(STATE_STORE_NAME, VALUES_KEY_NAME, Values.class);
            return state.getValue();
        } catch (Exception e) {
            LOGGER.error("Error while getting messages", e);
            return new Values(List.of());
        }
    }

    public record Values(List<String> values) {
    }
}
