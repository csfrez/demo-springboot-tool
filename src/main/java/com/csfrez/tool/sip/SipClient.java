package com.csfrez.tool.sip;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author frez
 * @date 2024/5/29 9:10
 */
public class SipClient implements SipListener {

    private SipFactory sipFactory;
    private SipStack sipStack;
    private SipProvider sipProvider;
    private AddressFactory addressFactory;
    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;

    public SipClient() throws Exception {
        // Initialize SIP stack
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        Properties properties = new Properties();

        properties.setProperty("javax.sip.STACK_NAME", "SipClient");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "textclient.txt");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "textclientdebug.log");

        sipStack = sipFactory.createSipStack(properties);
        ListeningPoint udp = sipStack.createListeningPoint("10.96.2.190", 5060, "udp");
        sipProvider = sipStack.createSipProvider(udp);
        sipProvider.addSipListener(this);

        addressFactory = sipFactory.createAddressFactory();
        messageFactory = sipFactory.createMessageFactory();
        headerFactory = sipFactory.createHeaderFactory();
    }

    public void sendRegister(String fromUser, String fromHost, String toUser, String toHost) throws Exception {
        // Create addresses
        SipURI fromAddress = addressFactory.createSipURI(fromUser, fromHost);
        Address fromNameAddress = addressFactory.createAddress(fromAddress);
        fromNameAddress.setDisplayName(fromUser);

        SipURI toAddress = addressFactory.createSipURI(toUser, toHost);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(toUser);

        // Create request URI
        SipURI requestURI = addressFactory.createSipURI(toUser, toHost);

        int port = sipProvider.getListeningPoint("udp").getPort();

        // Create via headers
        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader("10.96.2.190", port, "udp", null);
        viaHeaders.add(viaHeader);

        // Create content type header
        ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");

        // Create contact header
        SipURI contactURI = addressFactory.createSipURI(fromUser, "10.96.2.190");
        contactURI.setPort(port);
        Address contactAddress = addressFactory.createAddress(contactURI);
        contactAddress.setDisplayName(fromUser);
        ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);

        // Create the REGISTER request
        Request request = messageFactory.createRequest(
                requestURI,
                Request.REGISTER,
                sipProvider.getNewCallId(),
                headerFactory.createCSeqHeader(1L, Request.REGISTER),
                headerFactory.createFromHeader(fromNameAddress, "12345"),
                headerFactory.createToHeader(toNameAddress, null),
                viaHeaders,
                headerFactory.createMaxForwardsHeader(70)
        );

        request.addHeader(contactHeader);
        request.setContent("Test Content", contentTypeHeader);

        // Send the request
        ClientTransaction transaction = sipProvider.getNewClientTransaction(request);
        transaction.sendRequest();
    }

    public static void main(String[] args) {
        try {
            SipClient client = new SipClient();
            client.sendRegister("peter", "example.com", "alice", "example.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processRequest(RequestEvent requestEvent) {
        // 处理收到的请求
        Request request = requestEvent.getRequest();
        System.out.println("Received request: " + request.toString());
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        // 处理收到的响应
        Response response = responseEvent.getResponse();
        System.out.println("Received response: " + response.toString());
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        // 处理超时事件
        System.out.println("Timeout event: " + timeoutEvent);
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        // 处理IO异常
        System.out.println("IOException event: " + exceptionEvent);
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        // 处理事务终止事件
        System.out.println("Transaction terminated event: " + transactionTerminatedEvent);
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        // 处理对话终止事件
        System.out.println("Dialog terminated event: " + dialogTerminatedEvent);
    }
}