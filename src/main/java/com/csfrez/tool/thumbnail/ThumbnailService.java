package com.csfrez.tool.thumbnail;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class ThumbnailService {

    /**
     * 水印图片
     */
    private static File markIco = null;

    /**
     * 开机静态加载水印图片
     *
     */
    @PostConstruct
    public void init() {
        try {
            markIco = new File(new File("").getCanonicalPath() + "/icon.png");
            log.info("水印图片加载" + (markIco.exists() ? "成功" : "失败") + "==>" + markIco.getAbsolutePath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 加水印
     */
    public void photoMark(File sourceFile, File toFile) throws IOException {
        Thumbnails.of(sourceFile)
                .size(1920, 1080)//尺寸
                .watermark(Positions.BOTTOM_CENTER/*水印位置：中央靠下*/,
                        ImageIO.read(markIco), 0.7f/*质量，越大质量越高(1)*/)
                //.outputQuality(0.8f)
                .toFile(toFile);//保存为哪个文件
    }

    /**
     * 生成图片缩略图
     */
    public void photoSmaller(File sourceFile, File toFile) throws IOException {
        Thumbnails.of(sourceFile)
                .size(960, 540)//尺寸
                .outputQuality(0.4f)//缩略图质量
                .toFile(toFile);
    }
}
