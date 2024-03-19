package com.csfrez.tool.stream;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class StreamHttpClientExample {

    public static void main(String[] args){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/stream/response")) // 替换为您的实际URL
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.fromLineSubscriber(new LineSubscriber()))
                .join();
    }

    static class LineSubscriber implements Subscriber<String> {
        private Subscription subscription;

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1); // 请求一个元素
        }

        @Override
        public void onNext(String item) {
            // 处理接收到的数据
            System.out.println(item);
            // 继续请求下一个元素
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("Stream completed");
        }
    }
}
