package com.jizhangyl.application.VO;

import lombok.Data;

@Data
public class RepositoryProductVO {

    private Integer id;

    private Integer productId;

    private String productName;

    private Integer productStock;

    private Integer wayStock;

    private Integer yestOutNum;

    private Integer weekAvgOutNum;

    private Integer todoOutNum;

    private String productImage;

    private String productJancode;

    private Integer boxNum;

    private String packCode;
}
