package com.csfrez.tool.sip;

import java.util.ArrayList;
import java.util.Properties;
import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

public class SipClientSample implements SipListener {

    private SipFactory sipFactory;
    private SipStack sipStack;
    private SipProvider sipProvider;
    private AddressFactory addressFactory;
    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;

    private String stackName = "SipClientSample";
    private String traceLevel = "32";
    private String serverLog = "logs/SipClientSample_server.log";
    private String debugLog = "logs/SipClientSample_debug.log";
    private String transport = "udp";

    public SipClientSample(String ip, int port) throws Exception {
        // 初始化SIP栈
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", stackName);
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", traceLevel);
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", serverLog);
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", debugLog);

        sipStack = sipFactory.createSipStack(properties);
        ListeningPoint udp = sipStack.createListeningPoint(ip, port, transport);
        sipProvider = sipStack.createSipProvider(udp);
        sipProvider.addSipListener(this);

        addressFactory = sipFactory.createAddressFactory();
        messageFactory = sipFactory.createMessageFactory();
        headerFactory = sipFactory.createHeaderFactory();
    }

    public void sendRegister(String fromUser, String fromHost, String fromTag,
                             String toUser, String toHost, String toTag,
                             String displayName, int maxForwards, String branchId, long cSeq) throws Exception {
        // 创建地址
        SipURI fromAddress = addressFactory.createSipURI(fromUser, fromHost);
        Address fromNameAddress = addressFactory.createAddress(fromAddress);
        fromNameAddress.setDisplayName(displayName);

        SipURI toAddress = addressFactory.createSipURI(toUser, toHost);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(displayName);

        // 创建请求URI
        SipURI requestURI = addressFactory.createSipURI(toUser, toHost);

        // 创建Via头字段
        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProvider.getListeningPoint(transport).getIPAddress(), sipProvider.getListeningPoint(transport).getPort(), transport, branchId);
        viaHeaders.add(viaHeader);


        // 创建REGISTER请求
        Request request = messageFactory.createRequest(
                requestURI,
                Request.REGISTER,
                sipProvider.getNewCallId(),
                headerFactory.createCSeqHeader(cSeq, Request.REGISTER),
                headerFactory.createFromHeader(fromNameAddress, fromTag),
                headerFactory.createToHeader(toNameAddress, toTag),
                viaHeaders,
                headerFactory.createMaxForwardsHeader(maxForwards)
        );

        // 创建Contact头字段
        SipURI contactURI = addressFactory.createSipURI(fromUser, sipProvider.getListeningPoint(transport).getIPAddress());
        contactURI.setPort(sipProvider.getListeningPoint(transport).getPort());
        Address contactAddress = addressFactory.createAddress(contactURI);
        contactAddress.setDisplayName(displayName);
        ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
        request.addHeader(contactHeader);

        ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(3600);
        request.addHeader(expiresHeader);

        // 发送请求
        ClientTransaction transaction = sipProvider.getNewClientTransaction(request);
        transaction.sendRequest();
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

    public static void main(String[] args) {
        try {
            // 配置信息
            String ip = "10.96.2.190";
            int port = 5061;

            // 创建SIP客户端
            SipClientSample client = new SipClientSample(ip, port);

            // 注册请求参数
            String fromUser = "jack";
            String fromHost = "10.96.2.190:5061";
            String fromTag = "123456";
            String toUser = "jack";
            String toHost = "10.96.2.190:5060";
            String toTag = "654321";
            String displayName = "Alice";
            int maxForwards = 70;
            String branchId = null; // 自动生成
            long cSeq = 1L;

            // 发送注册请求
            client.sendRegister(fromUser, fromHost, fromTag, toUser, toHost, toTag, displayName, maxForwards, branchId, cSeq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}