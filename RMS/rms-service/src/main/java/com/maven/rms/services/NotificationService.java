package com.maven.rms.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.BillingMyTaskListingRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.OTCReceiptCancellationAssignToRequest;
import com.maven.rms.models.OTCReceiptCancellationCreatedByRequest;
import com.maven.rms.models.RefundMyTaskListingRequest;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationService implements WebSocketHandler {

    // Use thread-safe collections
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<WebSocketSession, String> sessionParameters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MFTWFService mftwfService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private OTCRcptCclService otcrcptcclService;
    @Autowired
    private BillingMyTaskService billingService;
    @Autowired
    private CreditControllerService ccSvc;

    @Async
    public void sendNotificationUpdate() {
        // Create a copy to avoid ConcurrentModificationException
        Set<WebSocketSession> sessionsCopy = new HashSet<>(sessions);

        for (WebSocketSession session : sessionsCopy) {
            if (session.isOpen()) {
                try {
                    String username = sessionParameters.get(session);
                    if (username == null) {
                        continue; // Skip if no username associated
                    }

                    // mft
                    MFTWFRequest mftwfRequest = new MFTWFRequest();
                    mftwfRequest.setI_assign_to(username);
                    mftwfRequest.setI_created_by(username);
                    Integer myTaskCount = mftwfService.sp_getmytaskactivetaskcount(mftwfRequest);
                    Integer createdTaskCount = mftwfService.sp_getcreatedtaskactivetaskcount(mftwfRequest);

                    // refund
                    RefundMyTaskListingRequest refundMyTaskListingRequest = new RefundMyTaskListingRequest();
                    refundMyTaskListingRequest.setI_assigned_to(username);
                    refundMyTaskListingRequest.setI_created_by(username);
                    Integer myTaskAssignedCountRefund = refundService
                            .sp_getrefundassignedtaskactivetaskcount(refundMyTaskListingRequest);
                    Integer createdTaskCountRefund = refundService
                            .sp_getrefundcreatedtaskactivetaskcount(refundMyTaskListingRequest);

                    // otc receipt cancellation
                    OTCReceiptCancellationAssignToRequest otcrcptcclAssignToRequest = new OTCReceiptCancellationAssignToRequest();
                    OTCReceiptCancellationCreatedByRequest otcrcptcclCreatedByRequest = new OTCReceiptCancellationCreatedByRequest();
                    otcrcptcclAssignToRequest.setI_assign_to(username);
                    otcrcptcclCreatedByRequest.setI_created_by(username);
                    Integer myTaskAssignedCountOTCRC = otcrcptcclService
                            .sp_getotcrcassignedtaskactivetaskcount(otcrcptcclAssignToRequest);
                    Integer createdTaskCountOTCRC = otcrcptcclService
                            .sp_getotcrccreatedtaskactivetaskcount(otcrcptcclCreatedByRequest);

                    // Billing
                    BillingMyTaskListingRequest billingRequest = new BillingMyTaskListingRequest();
                    billingRequest.setI_assigned_to(username);
                    billingRequest.setI_created_by(username);
                    Integer myTaskAssignedCountBilling = billingService
                            .sp_getbillingassignedtaskactivetaskcount(billingRequest);
                    Integer createdTaskCountBilling = billingService
                            .sp_getbillingcreatedtaskactivetaskcount(billingRequest);

                    // Credit Control
                    Integer myTaskAssignedCountCC = ccSvc.sp_getcccassignedtaskactivetaskcount(username);
                    Integer createdTaskCountCC = ccSvc.sp_getccccreatedtaskactivetaskcount(username);

                    String message = String.format(
                            "{\"type\":\"notificationUpdate\","
                                    + "\"myTaskCount\":%d,"
                                    + "\"createdTaskCount\":%d,"
                                    + "\"myTaskAssignedCountRefund\":%d,"
                                    + "\"createdTaskCountRefund\":%d,"
                                    + "\"myTaskAssignedCountOTCRC\":%d,"
                                    + "\"createdTaskCountOTCRC\":%d,"
                                    + "\"myTaskAssignedCountBilling\":%d,"
                                    + "\"createdTaskCountBilling\":%d,"
                                    + "\"myTaskAssignedCountCC\":%d,"
                                    + "\"createdTaskCountCC\":%d}",
                            myTaskCount, createdTaskCount, myTaskAssignedCountRefund, createdTaskCountRefund,
                            myTaskAssignedCountOTCRC, createdTaskCountOTCRC, myTaskAssignedCountBilling,
                            createdTaskCountBilling,
                            myTaskAssignedCountCC, createdTaskCountCC);

                    session.sendMessage(new TextMessage(message));

                } catch (Exception e) {
                    log.debug("Error sending notification to session {}: ", session.getId(), e);
                    // If session is broken, remove it
                    if (!session.isOpen()) {
                        sessions.remove(session);
                        sessionParameters.remove(session);
                    }
                }
            } else {
                // Session is closed, remove it
                sessions.remove(session);
                sessionParameters.remove(session);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        try {
            sessions.remove(session);
            sessionParameters.remove(session);
            log.info("Connection closed on session {}", session.getId());
        } catch (Exception e) {
            log.error("Notification Error in afterConnectionClosed: ", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            sessions.add(session);
            log.info("Connection established on session {}", session.getId());
        } catch (Exception e) {
            log.error("Notification Error in afterConnectionEstablished: ", e);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            String payload = message.getPayload().toString();

            Map<String, Object> payloadMap = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
            String type = (String) payloadMap.get("type");

            if ("ping".equals(type)) {
                session.sendMessage(new TextMessage("{\"type\": \"pong\"}"));
                return;
            }

            String username = (String) payloadMap.get("username");
            if (username != null) {
                sessionParameters.put(session, username);
                log.info("Username {} associated with session {}", username, session.getId());
            }
        } catch (Exception e) {
            log.error("Notification Error in handleMessage: ", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            log.info("Transport error on session {}: {}", session.getId(), exception.getMessage());
            // Clean up the broken session
            sessions.remove(session);
            sessionParameters.remove(session);
        } catch (Exception e) {
            log.error("Notification Error in handleTransportError: ", e);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

// original version
// package com.maven.rms.services;

// import java.util.HashSet;
// import java.util.Map;
// import java.util.Set;
// import java.util.concurrent.ConcurrentHashMap;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.web.socket.CloseStatus;
// import org.springframework.web.socket.TextMessage;
// import org.springframework.web.socket.WebSocketHandler;
// import org.springframework.web.socket.WebSocketMessage;
// import org.springframework.web.socket.WebSocketSession;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.maven.rms.models.BillingMyTaskListingRequest;
// import com.maven.rms.models.MFTWFRequest;
// import com.maven.rms.models.OTCReceiptCancellationAssignToRequest;
// import com.maven.rms.models.OTCReceiptCancellationCreatedByRequest;
// import com.maven.rms.models.RefundMyTaskListingRequest;
// import com.fasterxml.jackson.core.type.TypeReference;

// import lombok.extern.slf4j.Slf4j;

// //@Service is not required because use bean in WebSocketConfig.java
// @Slf4j
// public class NotificationService implements WebSocketHandler {
// // extends TextWebSocketHandler {

// // extends TextWebSocketHandler {

// private final Set<WebSocketSession> sessions = new HashSet<>();
// private final Map<WebSocketSession, String> sessionParameters = new
// ConcurrentHashMap<>();
// private final ObjectMapper objectMapper = new ObjectMapper();

// @Autowired
// private MFTWFService mftwfService;
// @Autowired
// private RefundService refundService;

// @Autowired
// private OTCRcptCclService otcrcptcclService;

// @Autowired
// private BillingMyTaskService billingService;

// @Autowired
// private CreditControllerService ccSvc;

// // @Override
// // public void afterConnectionEstablished(WebSocketSession session) throws
// // Exception {
// // sessions.add(session);
// // }

// // @Override
// // protected void handleTextMessage(WebSocketSession session,
// // org.springframework.web.socket.TextMessage message) throws Exception {
// // // Parse incoming WebSocket message
// // String payload = message.getPayload();
// // // Example: check if the message is to trigger the API call
// // if ("triggerAPI".equals(payload)) {

// // MFTWFRequest mftwfRequest = new MFTWFRequest();
// // List<MFTWF> result = mftwfService.sp_getmftwf(mftwfRequest);

// // // Optionally send the response back to the WebSocket client
// // if (result != null && !result.isEmpty()) {
// // sendNotificationUpdate(result.size(), 0); // Example: sending back the
// size
// // of the result
// // } else {
// // sendNotificationUpdate(0, 0); // No data found
// // }
// // }
// // }
// @Async
// public void sendNotificationUpdate() {
// // System.out.println("Notification sent 4");

// for (WebSocketSession session : sessions) {
// if (session.isOpen()) {
// try {
// // mft
// MFTWFRequest mftwfRequest = new MFTWFRequest();
// mftwfRequest.setI_assign_to(sessionParameters.get(session));
// mftwfRequest.setI_created_by(sessionParameters.get(session));
// Integer myTaskCount = mftwfService.sp_getmytaskactivetaskcount(mftwfRequest);
// Integer createdTaskCount =
// mftwfService.sp_getcreatedtaskactivetaskcount(mftwfRequest);

// // refund
// RefundMyTaskListingRequest refundMyTaskListingRequest = new
// RefundMyTaskListingRequest();
// refundMyTaskListingRequest.setI_assigned_to(sessionParameters.get(session));
// ;
// refundMyTaskListingRequest.setI_created_by(sessionParameters.get(session));
// Integer myTaskAssignedCountRefund = refundService
// .sp_getrefundassignedtaskactivetaskcount(refundMyTaskListingRequest);
// Integer createdTaskCountRefund = refundService
// .sp_getrefundcreatedtaskactivetaskcount(refundMyTaskListingRequest);

// // otc reciept cancellation
// OTCReceiptCancellationAssignToRequest otcrcptcclAssignToRequest = new
// OTCReceiptCancellationAssignToRequest();
// OTCReceiptCancellationCreatedByRequest otcrcptcclCreatedByRequest = new
// OTCReceiptCancellationCreatedByRequest();
// otcrcptcclAssignToRequest.setI_assign_to(sessionParameters.get(session));
// otcrcptcclCreatedByRequest.setI_created_by(sessionParameters.get(session));
// Integer myTaskAssignedCountOTCRC = otcrcptcclService
// .sp_getotcrcassignedtaskactivetaskcount(otcrcptcclAssignToRequest);
// Integer createdTaskCountOTCRC = otcrcptcclService
// .sp_getotcrccreatedtaskactivetaskcount(otcrcptcclCreatedByRequest);

// // Billing
// BillingMyTaskListingRequest billingRequest = new
// BillingMyTaskListingRequest();
// billingRequest.setI_assigned_to(sessionParameters.get(session));
// billingRequest.setI_created_by(sessionParameters.get(session));
// Integer myTaskAssignedCountBilling = billingService
// .sp_getbillingassignedtaskactivetaskcount(billingRequest);
// Integer createdTaskCountBilling = billingService
// .sp_getbillingcreatedtaskactivetaskcount(billingRequest);

// // Credit Control
// String username = sessionParameters.get(session);
// Integer myTaskAssignedCountCC =
// ccSvc.sp_getcccassignedtaskactivetaskcount(username);
// Integer createdTaskCountCC =
// ccSvc.sp_getccccreatedtaskactivetaskcount(username);

// // String message =
// //
// String.format("{\"type\":\"notificationUpdate\",\"myTaskCount\":%d,\"createdTaskCount\":%d,\"myTaskAssignedCountRefund\":%d,\"createdTaskCountRefund\":%d,\"myTaskAssignedCountOTCRC\":%d,\"createdTaskCountOTCRC\":%d}",
// // myTaskCount, createdTaskCount, myTaskAssignedCountRefund,
// // createdTaskCountRefund, myTaskAssignedCountOTCRC, createdTaskCountOTCRC);

// String message = String.format(
// "{\"type\":\"notificationUpdate\","
// + "\"myTaskCount\":%d,"
// + "\"createdTaskCount\":%d,"
// + "\"myTaskAssignedCountRefund\":%d,"
// + "\"createdTaskCountRefund\":%d,"
// + "\"myTaskAssignedCountOTCRC\":%d,"
// + "\"createdTaskCountOTCRC\":%d,"
// + "\"myTaskAssignedCountBilling\":%d,"
// + "\"createdTaskCountBilling\":%d,"
// + "\"myTaskAssignedCountCC\":%d,"
// + "\"createdTaskCountCC\":%d}",
// myTaskCount, createdTaskCount, myTaskAssignedCountRefund,
// createdTaskCountRefund,
// myTaskAssignedCountOTCRC, createdTaskCountOTCRC, myTaskAssignedCountBilling,
// createdTaskCountBilling,
// myTaskAssignedCountCC, createdTaskCountCC);

// session.sendMessage(new org.springframework.web.socket.TextMessage(message));
// // System.out.println("Notification sent 5");
// } catch (Exception e) {
// log.debug("Notification service error: ", e);
// }
// }
// }
// }

// // @Override
// // public void afterConnectionClosed(WebSocketSession session, CloseStatus
// // closeStatus) throws Exception {
// // sessions.remove(session);
// // sessionParameters.remove(session);
// // log.info("Connection closed on session {}", session.getId());
// // }

// @Override
// public void afterConnectionClosed(WebSocketSession session, CloseStatus
// closeStatus) {
// try {
// sessions.remove(session);
// sessionParameters.remove(session);
// log.info("Connection closed on session {}", session.getId());
// } catch (Exception e) {
// log.debug("Notification service error: ", e);
// }
// }

// // @Override
// // public void afterConnectionEstablished(WebSocketSession session) throws
// // Exception {
// // sessions.add(session); // no need add sessionParameter here because it
// will
// // be added in handleMessage
// // log.info("Connection established on seesion {}", session.getId());
// // }

// @Override
// public void afterConnectionEstablished(WebSocketSession session) {
// try {
// sessions.add(session); // no need add sessionParameter here because it will
// be added in handleMessage
// log.info("Connection established on session {}", session.getId());
// } catch (Exception e) {
// log.debug("Notification service error: ", e);
// }
// }

// // @Override
// // public void handleMessage(WebSocketSession session, WebSocketMessage<?>
// // message) throws Exception {
// // String payload = message.getPayload().toString();
// // log.info("Message received from session {}: {}", session.getId(),
// payload);

// // Map<String, Object> payloadMap = objectMapper.readValue(payload, new
// // TypeReference<Map<String, Object>>() {});
// // String type = (String) payloadMap.get("type");

// // if ("ping".equals(type)) {
// // // Reply with pong to keep connection alive
// // session.sendMessage(new TextMessage("{\"type\": \"pong\"}"));
// // return;
// // }

// // String username = (String) payloadMap.get("username");
// // sessionParameters.put(session, username);
// // log.info("Received username: {}", username);
// // }

// @Override
// public void handleMessage(WebSocketSession session, WebSocketMessage<?>
// message) {
// try {
// String payload = message.getPayload().toString();

// Map<String, Object> payloadMap = objectMapper.readValue(payload, new
// TypeReference<Map<String, Object>>() {
// });
// String type = (String) payloadMap.get("type");

// if ("ping".equals(type)) {
// session.sendMessage(new TextMessage("{\"type\": \"pong\"}"));
// return;
// }

// String username = (String) payloadMap.get("username");
// sessionParameters.put(session, username);
// } catch (Exception e) {
// log.debug("Notification service error: ", e);
// }
// }

// // @Override
// // public void handleTransportError(WebSocketSession session, Throwable
// // exception) throws Exception {
// // log.info("Exception occured on session {}", session.getId());
// // }
// @Override
// public void handleTransportError(WebSocketSession session, Throwable
// exception) {
// try {
// log.info("Exception occurred on session {}", session.getId());
// } catch (Exception e) {
// log.debug("Notification service error: ", e);
// }
// }

// @Override
// public boolean supportsPartialMessages() {
// return false;
// }
// }
