package dev.matheuscruz;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import io.dapr.client.domain.State;
import io.quarkiverse.dapr.core.SyncDaprClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/messages")
public class MessageResource {

    static final Logger LOGGER = LoggerFactory.getLogger(MessageResource.class);
    static final String VALUES_KEY_NAME = "values";
    SyncDaprClient dapr;
    String stateStoreName;

    public MessageResource(SyncDaprClient dapr,
    @ConfigProperty(name = "dapr.stateStoreName") String stateStoreName) {
        this.dapr = dapr;
        this.stateStoreName = stateStoreName;
    }

    @GET
    public List<String> reader() {
        try {
            State<List> state = dapr.getState(this.stateStoreName, VALUES_KEY_NAME, List.class);
            return state.getValue();
        } catch (Exception e) {
            LOGGER.error("Error while getting messages", e);
            return Collections.emptyList();
        }
    }
}
