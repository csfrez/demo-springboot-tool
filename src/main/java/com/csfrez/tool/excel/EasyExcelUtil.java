package com.csfrez.tool.excel;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class EasyExcelUtil {

    /**
     * 最简单的读
     * <p>1. 创建excel对应的实体对象 参照{@link EasyExcelReadModel}
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link EasyExcelListener}
     * <p>3. 直接读即可
     */
    public static <T> void simpleRead(String pathName, Class<T> head, ReadListener readListener) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(pathName, head, readListener).sheet().doRead();
    }

    /**
     * 最简单的写
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link EasyExcelWriteModel}
     * <p>
     * 2. 直接写即可
     */
    public static <T> void simpleWrite(String pathName, String sheetName, Class<T> head, Collection<?> data) {
        // 注意 simpleWrite在数据量不大的情况下可以使用（5000以内，具体也要看实际情况），数据量大参照 重复多次写入
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(pathName, head).sheet(sheetName).doWrite(data);
    }

    public static void consumerData(List<EasyExcelReadModel> readModelList, ExcelWriter excelWriter){
        log.info("{}条数据，开始存储数据库！===>{}", readModelList.size(), excelWriter);
        List<EasyExcelWriteModel> writeModelList = new ArrayList<>();
        readModelList.forEach(readModel -> {
            EasyExcelWriteModel writeModel = new EasyExcelWriteModel();
            String md5_32 = SecureUtil.md5(readModel.getCertNo());
            String md5_16 = SecureUtil.md5().digestHex16(readModel.getCertNo());
            String name = readModel.getName().substring(0, 1);
            writeModel.setName(name);
            writeModel.setMd5_16(md5_16);
            writeModel.setMd5_32(md5_32);
            writeModelList.add(writeModel);
        });
        excelWriter.write(writeModelList, EasyExcel.writerSheet("Sheet1").head(EasyExcelWriteModel.class).build());
    }

    public static void main(String[] args) {
        String pathName = "E:\\tmp\\excel\\测试客户名单.xlsx";
        ExcelWriter excelWriter = EasyExcel.write(new File("E:\\tmp\\excel\\测试客户名单_md5.xlsx"), EasyExcelWriteModel.class).build();
        EasyExcelListener easyExcelListener = new EasyExcelListener(EasyExcelUtil::consumerData, excelWriter);
        EasyExcelUtil.simpleRead(pathName, EasyExcelReadModel.class, easyExcelListener);
        //数据写入完毕关闭资源
        excelWriter.finish();
    }



}
