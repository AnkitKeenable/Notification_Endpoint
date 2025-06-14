package com.example.route;

import com.example.util.LogMasker;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

@ApplicationScoped
public class NotificationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // REST Configuration
        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.json);

        // REST Endpoint
        rest("/get-notifications")
                .post()
                .consumes("application/json")
                .produces("application/json")
                .to("direct:processNotification");

        // Processing route
        from("direct:processNotification")
                .routeId("notification-api-route")
                .log("Received request: ${body}")
                .bean("authMerger", "merge")
                .process(new LogMasker()) // Initialize the masker
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .toD("{{notification.api.url}}?bridgeEndpoint=true")
                .log("External API response: ${body}");
    }
}