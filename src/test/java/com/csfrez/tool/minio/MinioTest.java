package com.csfrez.tool.minio;

import com.csfrez.tool.DemoSpringbootToolApplicationTests;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class MinioTest extends DemoSpringbootToolApplicationTests {

    @Autowired
    private MinioClient minioClient;

    @Test
    public void policyTest() throws Exception {

        String bucketName = "mybucket";
        String fileName = "62de00a7773e49000a79e97d.jpeg";

        // Create new post policy for 'my-bucketname' with 7 days expiry from now.
        PostPolicy policy = new PostPolicy(bucketName, ZonedDateTime.now().plusDays(7));

        // Add condition that 'key' (object name) equals to 'my-objectname'.
        policy.addEqualsCondition("key", fileName);

        // Add condition that 'Content-Type' starts with 'image/'.
        policy.addStartsWithCondition("Content-Type", "image/");

        // Add condition that 'content-length-range' is between 64kiB to 10MiB.
        policy.addContentLengthRangeCondition(64 * 1024, 10 * 1024 * 1024);

        Map<String, String> formData = minioClient.getPresignedPostFormData(policy);

        // Upload an image using POST object with form-data.
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        multipartBuilder.setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        multipartBuilder.addFormDataPart("key", fileName);
        multipartBuilder.addFormDataPart("Content-Type", "image/jpeg");

        // "file" must be added at last.
        multipartBuilder.addFormDataPart(
                "file", fileName, RequestBody.create(new File("E:\\tmp\\jpg\\" + fileName), null));
        MultipartBody multipartBody = multipartBuilder.build();
        List<MultipartBody.Part> partList = multipartBody.parts();
        partList.forEach(part -> {
            System.out.println(part.headers() + "==>" + part.body());
        });

        Request request =
                new Request.Builder()
                        .url("http://10.11.0.136:9000/" + bucketName)
                        .post(multipartBody)
                        .build();
        OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            System.out.println(fileName + " is uploaded successfully using POST object");
        } else {
            System.out.println("Failed to upload " + fileName);
        }
    }
}
