package com.csfrez.tool.tika;

import com.csfrez.tool.DemoSpringbootToolApplicationTests;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TikaTest extends DemoSpringbootToolApplicationTests {


    @Autowired
    private Tika tika;


    @Test
    public void parsePdfTest(){
        try {
            Path path = Paths.get("E:\\tmp\\pdf", "5fe99f33709a8c6c99968211.pdf");
            File file = path.toFile();
            String s = tika.parseToString(file);
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }
    }


}
