package com.csfrez.tool.service;

import cn.hutool.core.util.IdUtil;
import com.csfrez.tool.thumbnail.ThumbnailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Slf4j
public class FileService {

    @Autowired
    private ThumbnailService thumbnailService;

    @Async
    public void pushPhoto(Integer id, String name, File file) throws InterruptedException, ExecutionException, IOException {
        Long startTime = System.currentTimeMillis();
        Integer[] numb = this.upLoadPhoto(StringUtils.isBlank(name) ? file.getName() : name, file).get();
        //Arrays.stream(numb).forEach(num -> log.info("num={}", num));
        Long endTime = System.currentTimeMillis();
        log.info("source [ " + id + " ] 绑定图片 [ " + name + " ] 成功,内部处理耗时 [" + (endTime - startTime) + "ms ]");
    }

    public Future<Integer[]> upLoadPhoto(String fileName, MultipartFile file) throws IOException {
        String ext = StringUtils.substring(fileName, fileName.lastIndexOf('.'));
        //创建临时文件
        File sourcePhoto = File.createTempFile(IdUtil.fastSimpleUUID(), ext);
        file.transferTo(sourcePhoto);
        return upLoadPhoto(fileName, sourcePhoto);
    }

    public Future<Integer[]> upLoadPhoto(String fileName, File sourcePhoto) throws IOException {
        String ext = StringUtils.substring(fileName, fileName.lastIndexOf('.'));
        File tempDir = new File("E:\\tmp\\image");
        //创建临时文件
        File markedPhoto = File.createTempFile(IdUtil.getSnowflakeNextIdStr(), ext, tempDir);
        File smallerPhoto = File.createTempFile(IdUtil.getSnowflakeNextIdStr(), ext, tempDir);
        //加水印 缩图
        thumbnailService.photoMark(sourcePhoto, markedPhoto);
        thumbnailService.photoSmaller(markedPhoto, smallerPhoto);
        //上传
//        Integer markedPhotoNumber = upLoadPhotoCtrl(fileName, markedPhoto);
//        Integer smallerPhotoNumber = upLoadPhotoCtrl("mini_" + fileName, smallerPhoto);
        //删除临时文件
//        sourcePhoto.delete();
//        markedPhoto.delete();
//        smallerPhoto.delete();

        Integer[] res = new Integer[]{1, 2};
        return new AsyncResult(res);
    }
}
