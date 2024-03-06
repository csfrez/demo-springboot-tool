package com.csfrez.tool.minio;

import lombok.Data;

@Data
public class PresignParam {
    // 桶名
    private String bucket;

    // 文件名
    private String filename;

}