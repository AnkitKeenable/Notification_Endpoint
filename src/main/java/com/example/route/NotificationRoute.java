//package com.example.route;
//
//import com.example.service.RequestIdService;
//import com.example.util.LogMasker;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import org.apache.camel.Exchange;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.model.rest.RestBindingMode;
//
//@ApplicationScoped
//public class NotificationRoute extends RouteBuilder {
//
////    @Inject
////    RequestCounterService requestCounterService;
//
//    @Inject
//    RequestIdService requestIdService;
//
//    @Override
//    public void configure() throws Exception {
//        // REST Configuration
//        restConfiguration()
//                .component("platform-http")
//                .bindingMode(RestBindingMode.json);
//
//        // REST Endpoint
//        rest("/get-notifications")
//                .post()
//                .consumes("application/json")
//                .produces("application/json")
//                .to("direct:processNotification");
//
//
//        from("direct:processNotification")
//                .routeId("notification-api-route")
//                .process(e -> {
//                    String requestId = requestIdService.generateId();
//                    e.setProperty("requestId", requestId);
//                    e.getIn().setHeader("X-Request-ID", requestId);
//                })
//                .log("[ID:${exchangeProperty.requestId}] | NotificationList |  - Received request: ${body}")
//                .bean("authMerger", "merge")
//                .process(new LogMasker())
//                .marshal().json()
//                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
//                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
//                .log("[ID:${exchangeProperty.requestId}] | NotificationList | Sending request to Upshot    - Body: ${body}")
//                .toD("{{notification.api.url}}?bridgeEndpoint=true")
//                .log("[ID:${exchangeProperty.requestId}] | NotificationList | Received request  from Upshot  - Body: ${body}")
//                .log("[ID:${exchangeProperty.requestId}] | NotificationList |  - Response Sent to User: ${body}");
//    }
//}
//
////request sent to upshot
////request receive from upshot
//
//




package com.example.route;

import com.example.service.RequestIdService;
import com.example.util.LogMasker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class NotificationRoute extends RouteBuilder {

    // Create logger instance
    private static final Logger logger = LoggerFactory.getLogger(NotificationRoute.class);

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
//                .routeId("notification-api-route")
                .process(e -> {
                    String requestId = requestIdService.generateId();
                    e.setProperty("requestId", requestId);
                    e.getIn().setHeader("X-Request-ID", requestId);

                    // DEBUG: Log request initiation
                    logger.debug("[ID:{}] Request processing started", requestId);
                })
                // INFO: Main flow points
//                .log("Ankit-  ${exchangeProperty}")
//                .log("Notification request received - ID: ${exchangeProperty.requestId}")
                .log(LoggingLevel.DEBUG, "DEBUG: Notification request received - ID: ${exchangeProperty.requestId}")

                .bean("authMerger", "merge")
                .process(new LogMasker())
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))

                // DEBUG: Full request details
                .process(e -> {
                    logger.debug("[ID:{}] Outgoing to Upshot - Headers: {}, Body: {}",
                            e.getProperty("requestId"),
                            e.getIn().getHeaders(),
                            e.getIn().getBody(String.class));
                })

                .toD("{{notification.api.url}}?bridgeEndpoint=true")

                // DEBUG: Full response details
                .process(e -> {
                    logger.debug("[ID:{}] Response from Upshot - Status: {}, Body: {}",
                            e.getProperty("requestId"),
                            e.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE),
                            e.getIn().getBody(String.class));
                })
                .log(LoggingLevel.INFO, "Notification request completed - ID: ${exchangeProperty.requestId}");

                // INFO: Summary of response
//                .log("Notification request completed - ID: ${exchangeProperty.requestId}");
    }
}