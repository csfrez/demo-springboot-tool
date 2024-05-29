package com.csfrez.tool.sip;

import cn.hutool.crypto.SecureUtil;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.*;

public class JainSipServer implements SipListener {
    SipStack sipstack = null;
    HeaderFactory hf = null;
    AddressFactory af = null;
    MessageFactory mf = null;
    SipProvider sipProvider = null;

    /**
     * 保存正在注册的用户，注册第一步的
     */
    private static Set<String> registingId = new HashSet<>();
    /**
     * 保存当前注册的用户，注册成功的
     */
    private static Hashtable<String, URI> registedContactURI = new Hashtable<>();

    public static void main(String[] args) {
        JainSipServer test = new JainSipServer();
        test.init();
    }

    public void init() {
        SipFactory sf = SipFactory.getInstance();
        sf.setPathName("gov.nist");
        Properties prop = new Properties();
        prop.setProperty("javax.sip.STACK_NAME", "JainSipServer");

//		prop.setProperty("javax.sip.IP_ADDRESS", "10.96.2.190");
//		prop.setProperty("javax.sip.OUTBOUND_PROXY", "10.96.2.190:8888/UDP");
        // You need 16 for logging traces. 32 for debug + traces.
        // Your code will limp at 32 but it is best for debugging.
        prop.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        prop.setProperty("gov.nist.javax.sip.DEBUG_LOG", "logs/JainSipServer_debug.log");
        prop.setProperty("gov.nist.javax.sip.SERVER_LOG", "logs/JainSipServer_server.log");

        try {
            sipstack = sf.createSipStack(prop);
        } catch (PeerUnavailableException e) {
            e.printStackTrace();
        }

        try {
            hf = sf.createHeaderFactory();
            af = sf.createAddressFactory();
            mf = sf.createMessageFactory();
            ListeningPoint listeningPoint = sipstack.createListeningPoint("10.96.2.190", 5060, "udp");

            sipProvider = sipstack.createSipProvider(listeningPoint);
            sipProvider.addSipListener(this);
            System.out.println("服务启动完成。。。");
        } catch (TransportNotSupportedException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        } catch (ObjectInUseException e) {
            e.printStackTrace();
        } catch (PeerUnavailableException e) {
            e.printStackTrace();
        }

    }


    //Listener实现
    @Override
    public void processRequest(RequestEvent requestEvent) {
        Request request = requestEvent.getRequest();
        if (null == request) {
            System.out.println("收到的requestEvent.getRequest() is null.");
            return;
        }

        System.out.println(">>>>>收到的request内容是\n" + request);

        switch (request.getMethod().toUpperCase()) {
            case Request.INVITE:
                System.out.println("收到INVITE的请求");
                break;
            case Request.REGISTER:
                System.out.println("收到REGISTER的请求");
                doRegister(request, requestEvent);
                break;
            case Request.SUBSCRIBE:
                System.out.println("收到SUBSCRIBE的请求");
                break;
            case Request.ACK:
                System.out.println("收到ACK的请求");
                break;
            case Request.BYE:
                System.out.println("收到BYE的请求");
                break;
            case Request.CANCEL:
                System.out.println("收到CANCEL的请求");
                break;
            default:
                System.out.println("不处理的requestMethod：" + request.getMethod().toUpperCase());
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        Response response = responseEvent.getResponse();
        if (null == response) {
            System.out.println("response is null.");
            return;
        }
        System.out.println("收到的Response is :" + response);
        ClientTransaction clientTransaction = responseEvent.getClientTransaction();
        Request request = clientTransaction.getRequest();
        System.out.println("收到的Response for request:" + request);

        if (response.getStatusCode() == Response.TRYING) {
            System.out.println("收到的response is 100 TRYING");
            return;
        }
        switch (request.getMethod().toUpperCase()) {
            case Request.INVITE:
                System.out.println("收到INVITE的响应");
                break;
            case Request.BYE:
                System.out.println("收到BYE的响应");
                break;
            case Request.CANCEL:
                System.out.println("收到CANCEL的响应");
                break;
            default:
                System.out.println("不处理的response的请求类型：" + request.getMethod().toUpperCase());
        }
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        // TODO Auto-generated method stub
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        // TODO Auto-generated method stub
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        // TODO Auto-generated method stub
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        // TODO Auto-generated method stub
    }

    private void doRegister(Request request, RequestEvent requestEvent) {
        if (null == request || null == requestEvent) {
            System.out.println("无法处理REGISTER请求，request=" + request + ",event=" + requestEvent);
            return;
        }
        ServerTransaction serverTransactionId = requestEvent.getServerTransaction();
        try {
            Response response = null;

            ToHeader toHead = (ToHeader) request.getHeader(ToHeader.NAME);
            Address toAddress = toHead.getAddress();
            URI toURI = toAddress.getURI();
            SipURI sipURI_to = (SipURI) toURI;
            String toUserId = sipURI_to.getUser();
            System.out.println("注册的toUserId是" + toUserId);

            ContactHeader contactHeader = (ContactHeader) request.getHeader(ContactHeader.NAME);
            Address contactAddr = contactHeader.getAddress();
            URI contactURI = contactAddr.getURI();

            System.out.println("注册 from: " + toURI + " request str: " + contactURI);
            if (null == toUserId || "".equals(toUserId)) {
                System.out.println("无法识别的userId，不处理。");
                return;
            }
            ExpiresHeader expiresHeader = request.getExpires();
            int expires = 0;
            if (expiresHeader != null) {
                expires = request.getExpires().getExpires();
            }
            // 如果expires不等于0,则为注册，否则为注销。
            if (expires != 0 || contactHeader.getExpires() != 0) {//注册
                if (registedContactURI.containsKey(toUserId)) {//已经注册了
                    System.out.println("已经注册过了 user=" + toURI);
                } else {//不是注册成功状态
                    if (registingId.contains(toUserId)) {//是第二次注册
                        System.out.println("第二次注册 register user=" + toURI);
                        // 验证AuthorizationHeader摘要认证信息
                        AuthorizationHeader authorizationHeader = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
                        boolean authorizationResult = false;
                        if (null != authorizationHeader) {//验证
                            String username = authorizationHeader.getUsername();
                            String realm = authorizationHeader.getRealm();
                            String nonce = authorizationHeader.getNonce();
                            URI uri = authorizationHeader.getURI();
                            String res = authorizationHeader.getResponse();
                            String algorithm = authorizationHeader.getAlgorithm();
                            System.out.println("Authorization信息：username=" + username + ",realm=" + realm + ",nonce=" + nonce + ",uri=" + uri + ",response=" + res + ",algorithm=" + algorithm);
                            if (null == username || null == realm || null == nonce || null == uri || null == res || null == algorithm) {
                                System.out.println("Authorization信息不全，无法认证。");
                            } else {
                                // 比较Authorization信息正确性
                                String A1 = SecureUtil.md5(username + ":" + realm + ":12345678");
                                String A2 = SecureUtil.md5("REGISTER:sip:servername@10.96.2.190:5060");
                                String resStr = SecureUtil.md5(A1 + ":" + nonce + ":" + A2);
                                if (resStr.equals(res)) {
                                    //注册成功，标记true
                                    authorizationResult = true;
                                }
                            }
                        }
                        registingId.remove(toUserId);//不管第二次是否成功都移除，失败要从头再来
                        // 验证成功加入成功注册列表，失败不加入
                        if (authorizationResult) {//注册成功
                            System.out.println("注册成功？");
                            registedContactURI.put(toUserId, contactURI);
                            //返回成功
                            response = mf.createResponse(Response.OK, request);
                            DateHeader dateHeader = hf.createDateHeader(Calendar.getInstance());
                            response.addHeader(dateHeader);
                            System.out.println("返回注册结果 response是\n" + response.toString());

                            if (serverTransactionId == null) {
                                serverTransactionId = sipProvider.getNewServerTransaction(request);
                                serverTransactionId.sendResponse(response);
                                // serverTransactionId.terminate();
//								System.out.println("register serverTransaction: " + serverTransactionId);
                            } else {
                                System.out.println("processRequest serverTransactionId is null.");
                            }
                        } else {//注册失败
                            System.out.println("注册失败？");
                            //返回失败
                            response = mf.createResponse(Response.FORBIDDEN, request);
                            System.out.println("返回注册结果 response是\n" + response.toString());

                            if (serverTransactionId == null) {
                                serverTransactionId = sipProvider.getNewServerTransaction(request);
                                serverTransactionId.sendResponse(response);
                            } else {
                                System.out.println("processRequest serverTransactionId is null.");
                            }
                        }
                    } else {//是第一次注册
                        System.out.println("首次注册 user=" + toURI);
                        // 加入registing列表
                        registingId.add(toUserId);
                        //发送响应
                        response = mf.createResponse(Response.UNAUTHORIZED, request);
                        String realm = "zectec";
                        CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
                        String callId = callIdHeader.getCallId();
                        String nonce = SecureUtil.md5(callId + toUserId);
                        WWWAuthenticateHeader wwwAuthenticateHeader = hf.createWWWAuthenticateHeader("Digest realm=\"" + realm + "\",nonce=\"" + nonce + "\"");
                        response.setHeader(wwwAuthenticateHeader);
                        System.out.println("返回注册结果 response是\n" + response.toString());

                        if (serverTransactionId == null) {
                            serverTransactionId = sipProvider.getNewServerTransaction(request);
                            serverTransactionId.sendResponse(response);
                            // serverTransactionId.terminate();
//							System.out.println("register serverTransaction: " + serverTransactionId);
                        } else {
                            System.out.println("processRequest serverTransactionId is null.");
                        }

                    }
                }
            } else {//注销
                System.out.println("注销 user=" + toURI);
                //发送ok响应
                response = mf.createResponse(Response.OK, request);
                System.out.println("返回注销结果 response  : " + response.toString());
                if (serverTransactionId == null) {
                    serverTransactionId = sipProvider.getNewServerTransaction(request);
                    serverTransactionId.sendResponse(response);
                    // serverTransactionId.terminate();
                    System.out.println("register serverTransaction: " + serverTransactionId);
                } else {
                    System.out.println("processRequest serverTransactionId is null.");
                }
                //移除
                registingId.remove(toUserId);
                registedContactURI.remove(toUserId);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SipException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

}