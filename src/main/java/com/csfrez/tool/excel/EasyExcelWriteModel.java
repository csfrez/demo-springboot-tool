package com.csfrez.tool.excel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class EasyExcelWriteModel {

    private String name;

    private String md5_16;

    private String md5_32;

    private String sha256;
}
