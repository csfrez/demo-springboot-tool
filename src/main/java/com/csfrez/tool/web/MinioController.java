package com.csfrez.tool.web;

import com.csfrez.tool.minio.PresignParam;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.errors.MinioException;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/minio")
public class MinioController {

    @Autowired
    private MinioClient minioClient;

    @RequestMapping(value = "/presign", method = {RequestMethod.POST})
    public Map<String, String> presign(@RequestBody PresignParam presignParam) {
        // 如果前端不指定桶，那么给一个默认的
        if (StringUtils.isEmpty(presignParam.getBucket())) {
            presignParam.setBucket("demo");
        }

        // 前端不指定文件名称，就给一个UUID
        if (StringUtils.isEmpty(presignParam.getFilename())) {
            presignParam.setFilename(UUID.randomUUID().toString());
        }

        // 如果想要以子目录的方式保存，就在前面加上斜杠来表示
        //        presignParam.setFilename("/2023/" + presignParam.getFilename());

        // 设置凭证过期时间
        //ZonedDateTime expirationDate = ZonedDateTime.now().plusMinutes(10);
        ZonedDateTime expirationDate = ZonedDateTime.now().plusDays(7);
        // 创建一个凭证
        PostPolicy policy = new PostPolicy(presignParam.getBucket(), expirationDate);
        policy.addEqualsCondition("key", presignParam.getFilename());
        // 限制文件大小，单位是字节byte，也就是说可以设置如：只允许30M以内的文件上传
        policy.addContentLengthRangeCondition(1, 30 * 1024 * 1024);
        // 限制上传文件请求的ContentType
        //policy.addStartsWithCondition("Content-Type", "image/");

        try {
            // 生成凭证并返回
            //final Map<String, String> map = minioClient.presignedPostPolicy(policy);
            final Map<String, String> map = minioClient.getPresignedPostFormData(policy);
            map.put("bucket", presignParam.getBucket());
            map.put("key", presignParam.getFilename());
            System.out.println("===========================================================");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            return map;
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/merge")
    public void merge() {
        List<ComposeSource> sources = new ArrayList<>();
        // 分片数据放到另一个桶里面：mybucket
        sources.add(ComposeSource.builder()
                .bucket("mybucket")
                .object("02024.mp4")
                .build());
        sources.add(ComposeSource.builder()
                .bucket("mybucket")
                .object("12024.mp4")
                .build());
        sources.add(ComposeSource.builder()
                .bucket("mybucket")
                .object("22024.mp4")
                .build());
        sources.add(ComposeSource.builder()
                .bucket("mybucket")
                .object("32024.mp4")
                .build());
        sources.add(ComposeSource.builder()
                .bucket("mybucket")
                .object("42024.mp4")
                .build());
        final ComposeObjectArgs args = ComposeObjectArgs.builder()
                .bucket("mybucket")
                .object("2024.mp4")
                .sources(sources)
                .build();

        try {
            minioClient.composeObject(args);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public Map<String, String> upload(@RequestBody PresignParam presignParam) {



        return null;
    }
}
