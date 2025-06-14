package com.example.route;

import com.example.service.RequestCounterService;
import com.example.service.RequestIdService;
import com.example.util.LogMasker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

@ApplicationScoped
public class NotificationRoute extends RouteBuilder {

//    @Inject
//    RequestCounterService requestCounterService;

    @Inject
    RequestIdService requestIdService;

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


        from("direct:processNotification")
                .routeId("notification-api-route")
                .process(e -> {
                    String requestId = requestIdService.generateId();
                    e.setProperty("requestId", requestId);
                    e.getIn().setHeader("X-Request-ID", requestId);
                })
                .log("Request ID: ${exchangeProperty.requestId} - Received request: ${body}")
                .bean("authMerger", "merge")
                .process(new LogMasker())
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .toD("{{notification.api.url}}?bridgeEndpoint=true")
                // Add this line before the final log
                .process(e -> {
                    Object body = e.getIn().getBody();
                    if (body instanceof java.util.Map) {
                        ((java.util.Map)body).put("requestId", e.getProperty("requestId"));
                    }
                })
                .log("Request ID: ${exchangeProperty.requestId} - External API response: ${body}");
    }
}



