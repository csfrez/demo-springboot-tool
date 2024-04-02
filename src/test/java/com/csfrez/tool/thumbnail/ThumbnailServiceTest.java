package com.csfrez.tool.thumbnail;

import com.csfrez.tool.DemoSpringbootToolApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class ThumbnailServiceTest extends DemoSpringbootToolApplicationTests {

    @Autowired
    private ThumbnailService thumbnailService;

    @Test
    public void photoMarkTest() throws Exception{
        File sourceFile = new File("E:\\tmp\\image\\2024040101.jpg");
        File toFile = new File("E:\\tmp\\image\\2024040102.jpg");
        thumbnailService.photoMark(sourceFile, toFile);
    }

    @Test
    public void photoSmallerTest() throws Exception{
        File sourceFile = new File("E:\\tmp\\image\\594cc065da72f.jpg");
        File toFile = new File("E:\\tmp\\image\\2024040103.jpg");
        thumbnailService.photoSmaller(sourceFile, toFile);
    }

}
