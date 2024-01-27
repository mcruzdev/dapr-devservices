package dev.matheuscruz;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dapr.client.domain.State;
import io.quarkiverse.dapr.core.SyncDaprClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/messages")
public class MessageResource {

    static final Logger LOGGER = LoggerFactory.getLogger(MessageResource.class);
    static final String VALUES_KEY_NAME = "values";
    static final String STATE_STORE_NAME = "statestore";
    static final String TOPIC_NAME = "topic";
    SyncDaprClient dapr;

    public MessageResource(
            final SyncDaprClient dapr) {
        this.dapr = dapr;
    }

    @POST
    public Response message(@QueryParam("message") String message) {
        LOGGER.info("[application:write] Storing new message: {}", message);

        State<Values> state = dapr.getState(STATE_STORE_NAME, VALUES_KEY_NAME, Values.class);
        Values values = state.getValue();
        if (values == null) {
            Values newValues = new Values(List.of(message));
            dapr.saveState(STATE_STORE_NAME, VALUES_KEY_NAME, newValues);
        } else {
            values.values().add(message);
            dapr.saveState(STATE_STORE_NAME, VALUES_KEY_NAME, values);
        }

        LOGGER.info("Publishing CloudEvent {}", values);
        dapr.publishEvent(TOPIC_NAME, message);

        return Response.ok().build();
    }

    public record Values(List<String> values) {
    }

}
