package com.csfrez.tool.web;

import cn.hutool.core.util.IdUtil;
import com.csfrez.tool.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@EnableAsync
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("上传业务图片")
    @PostMapping("/push/photo/{id}/{name}")
    public ResponseEntity<Long> pushPhoto(
            @ApiParam("SourceId") @PathVariable Integer id,
            @ApiParam("图片名称不约束，可不填则使用原名，可使用随机码或原名称，但必须带扩展名") @PathVariable(required = false) String name,
            @RequestParam MultipartFile file) throws InterruptedException, ExecutionException, IOException {
        Long startTime = System.currentTimeMillis();
        String fileName = file.getOriginalFilename();
        String ext = StringUtils.substring(fileName, fileName.lastIndexOf('.'), fileName.length());
        String simpleUUID = IdUtil.fastSimpleUUID();
        File tempPhoto = File.createTempFile(simpleUUID, ext);
        //转储临时文件
        file.transferTo(tempPhoto);
        fileService.pushPhoto(id, name, tempPhoto);
        Long endTime = System.currentTimeMillis();
        Long duration = endTime - startTime;
        log.info("请求耗时={}ms", duration);
        return ResponseEntity.ok(duration);
    }

    @PostMapping("/uploadMultipleFiles")
    public ResponseEntity<List<String>> uploadMultipleFiles(MultipartHttpServletRequest request)
            throws IOException, ExecutionException, InterruptedException {
        Long startTime = System.currentTimeMillis();
        Map<String, MultipartFile> fileMap = request.getFileMap();
        List<String> idList = Lists.newArrayList();
        int index = 1;
        for (MultipartFile file : fileMap.values()) {
            String fileName = file.getOriginalFilename();
            String ext = StringUtils.substring(fileName, fileName.lastIndexOf('.'), fileName.length());
            String id = IdUtil.getSnowflakeNextIdStr();
            idList.add(id);
            File tempDir = new File("E:\\tmp\\image");
            File tempPhoto = File.createTempFile(id, ext, tempDir);
            //转储临时文件
            file.transferTo(tempPhoto);
            fileService.pushPhoto(index, fileName, tempPhoto);
            index++;
        }
        Long endTime = System.currentTimeMillis();
        Long duration = endTime - startTime;
        log.info("请求耗时={}ms", duration);
        return ResponseEntity.ok(idList);
    }

    @PostMapping("/uploadMultipleFiles2")
    public ResponseEntity<List<String>> uploadMultipleFiles(@RequestParam MultipartFile [] multipartFiles) throws IOException, ExecutionException, InterruptedException {
        Long startTime = System.currentTimeMillis();
        List<String> idList = Lists.newArrayList();
        for(int i = 0; i < multipartFiles.length; i++) {
            MultipartFile file = multipartFiles[i];
            String fileName = file.getOriginalFilename();
            String ext = StringUtils.substring(fileName, fileName.lastIndexOf('.'), fileName.length());
            String id = IdUtil.getSnowflakeNextIdStr();
            idList.add(id);
            File tempDir = new File("E:\\tmp\\image");
            File tempPhoto = File.createTempFile(id, ext, tempDir);
            //转储临时文件
            file.transferTo(tempPhoto);
            fileService.pushPhoto(i, fileName, tempPhoto);
        }
        Long endTime = System.currentTimeMillis();
        Long duration = endTime - startTime;
        log.info("请求耗时={}ms", duration);
        return ResponseEntity.ok(idList);
    }

}
