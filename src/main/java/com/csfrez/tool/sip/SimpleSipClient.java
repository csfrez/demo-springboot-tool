package com.csfrez.tool.sip;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Frez
 * @date 2024/5/28 19:00
 */
public class SimpleSipClient implements SipListener {

    private SipFactory sipFactory;
    private SipStack sipStack;
    private SipProvider sipProvider;
    private AddressFactory addressFactory;
    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;

    public SimpleSipClient() throws Exception {
        // 初始化SIP工厂
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        // 配置SIP栈属性
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "SimpleSipStack");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "sipDebug.log");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "sipServer.log");

        // 创建SIP栈
        sipStack = sipFactory.createSipStack(properties);

        // 创建ListeningPoint并添加到SIP提供者
        ListeningPoint udpListeningPoint = sipStack.createListeningPoint("10.96.2.190", 5060, "udp");
        sipProvider = sipStack.createSipProvider(udpListeningPoint);
        sipProvider.addSipListener(this);

        // 创建工厂
        addressFactory = sipFactory.createAddressFactory();
        messageFactory = sipFactory.createMessageFactory();
        headerFactory = sipFactory.createHeaderFactory();
    }

    public void sendInvite1() throws Exception {
        // 创建请求URI
        SipURI requestURI = addressFactory.createSipURI("toUser", "example.com");

        // 创建From和To头
        Address fromAddress = addressFactory.createAddress("sip:fromUser@localhost");
        Address toAddress = addressFactory.createAddress("sip:toUser@example.com");
        FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, "12345");
        ToHeader toHeader = headerFactory.createToHeader(toAddress, null);

        // 创建Via头
        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader("10.96.2.190", 5060, "udp", null);
        viaHeaders.add(viaHeader);

        // 创建CallId头
        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        // 创建CSeq头
        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.INVITE);

        // 创建MaxForwards头
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);

        // 创建Contact头
        SipURI contactURI = addressFactory.createSipURI("fromUser", "10.96.2.190");
        contactURI.setPort(5060);
        Address contactAddress = addressFactory.createAddress(contactURI);
        ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);

        // 创建请求
        Request request = messageFactory.createRequest(
                requestURI,
                Request.INVITE,
                callIdHeader,
                cSeqHeader,
                fromHeader,
                toHeader,
                viaHeaders,
                maxForwardsHeader
        );

        // 添加Contact头
        request.addHeader(contactHeader);

        // 发送请求
        ClientTransaction transaction = sipProvider.getNewClientTransaction(request);
        transaction.sendRequest();
    }


    public void sendInvite2() throws Exception {
        // 创建请求URI
        SipURI requestURI = addressFactory.createSipURI("user", "example.com");

        // 创建From和To头
        Address fromAddress = addressFactory.createAddress("sip:fromUser@localhost");
        Address toAddress = addressFactory.createAddress("sip:toUser@example.com");
        FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, "12345");
        ToHeader toHeader = headerFactory.createToHeader(toAddress, null);

        List<ViaHeader> viaHeaderList = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader("10.96.2.190", sipProvider.getListeningPoint("udp").getPort(), "udp", null);
        viaHeaderList.add(viaHeader);

        // Create content type header
        ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");

        // Create contact header
        SipURI contactURI = addressFactory.createSipURI("fromUser", "10.96.2.190");
        contactURI.setPort(sipProvider.getListeningPoint("udp").getPort());
        Address contactAddress = addressFactory.createAddress(contactURI);
        contactAddress.setDisplayName("fromUser");
        ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);


        // 创建请求
        Request request = messageFactory.createRequest(
                requestURI,
                Request.INVITE,
                sipProvider.getNewCallId(),
                headerFactory.createCSeqHeader(1L, Request.INVITE),
                fromHeader,
                toHeader,
                viaHeaderList,
                //sipProvider.getNewViaHeader("10.96.2.190", 5060, "udp", null),
                headerFactory.createMaxForwardsHeader(70)
        );

        request.addHeader(contactHeader);
        //request.setContent("Dummy Content", contentTypeHeader);

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
            SimpleSipClient client = new SimpleSipClient();
            client.sendInvite1();
            //client.sendInvite2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
