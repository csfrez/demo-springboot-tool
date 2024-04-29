package com.csfrez.tool.mapstruct;


import com.csfrez.tool.entity.UserDO;
import com.csfrez.tool.entity.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        // convert 逻辑依赖 DateUtil 做日期转化
        uses = DateUtil.class
)
public interface UserConvertor {

    UserDTO toUserDTO(UserDO userDO);

}
