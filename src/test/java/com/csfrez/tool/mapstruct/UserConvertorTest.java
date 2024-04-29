package com.csfrez.tool.mapstruct;

import com.csfrez.tool.DemoSpringbootToolApplicationTests;
import com.csfrez.tool.entity.UserDO;
import com.csfrez.tool.entity.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
public class UserConvertorTest extends DemoSpringbootToolApplicationTests {

    @Autowired
    private UserConvertor userConvertor;

    @Test
    public void testToUserDTO(){
        UserDO userDO = new UserDO();
        userDO.setName("张三");
        userDO.setAge(18);
        userDO.setBirthDay(new Date());
        UserDTO userDTO = userConvertor.toUserDTO(userDO);
        log.info("name={}, age={}, birthday={}", userDTO.getName(), userDTO.getAge(), userDTO.getBirthDay());
    }
}
