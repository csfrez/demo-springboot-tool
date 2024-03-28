package com.csfrez.tool.web;


import com.github.linyuzai.download.core.annotation.Download;
import com.github.linyuzai.download.core.annotation.SourceCache;
import com.github.linyuzai.download.core.source.reflect.SourceModel;
import com.github.linyuzai.download.core.source.reflect.SourceName;
import com.github.linyuzai.download.core.source.reflect.SourceObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/download")
public class DownloadController {

    /**
     * classpath下的文件。
     */
    @Download(source = "classpath:/download/README.txt")
    @GetMapping("/classpath")
    public void classpath() {
    }

    /**
     * 指定路径的文件。可以指定文件夹，会自动压缩整个文件夹。
     */
    @Download(source = "file:/E:/tmp/yaml")
    @GetMapping("/file")
    public void file() {
    }

    /**
     * 指定下载地址。可以通过配置缓存下载文件，后面不需要重复下载。
     */
    @Download(source = "https://i1-073img.777lala.com/150413/17598486_143936_1.jpg")
    @GetMapping("/http")
    public void http() {
    }

    @Download(filename = "压缩包.zip")
    @GetMapping("/list")
    public List<Object> list() {
        List<Object> list = new ArrayList<>();
        list.add(new File("E:/tmp/yaml"));
        list.add(new ClassPathResource("/download/README.txt"));
        list.add("http://127.0.0.1/download/image.jpg");
        return list;
    }

    @Download(filename = "压缩包.zip")
    @SourceCache(group = "source")
    @GetMapping("/source-cache")
    public String[] sourceCache() {
        return new String[]{
                "http://127.0.0.1/download/text.txt",
                "http://127.0.0.1/download/image.jpg",
                "http://127.0.0.1/download/video.mp4"
        };
    }

    @Download
    @GetMapping("/business-model")
    public List<BusinessModel> businessModel() {
        List<BusinessModel> businessModels = new ArrayList<>();
        businessModels.add(new BusinessModel("BusinessModel.txt", "http://127.0.0.1/download/text.txt"));
        businessModels.add(new BusinessModel("BusinessModel.jpg", "http://127.0.0.1/download/image.jpg"));
        businessModels.add(new BusinessModel("BusinessModel.mp4", "http://127.0.0.1/download/video.mp4"));
        return businessModels;
    }

    @Data
    @SourceModel
    @AllArgsConstructor
    public static class BusinessModel {

        @SourceName
        private String name;

        @SourceObject
        private String url;
    }
}