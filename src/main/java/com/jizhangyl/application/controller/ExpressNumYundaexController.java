package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.primary.ExpressNumYundaex;
import com.jizhangyl.application.enums.ExpressNumStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.ExpressNumYundaexService;
import com.jizhangyl.application.service.ExpressService;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/25 10:36
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/express/yundaex")
public class ExpressNumYundaexController {

    @Autowired
    private ExpressNumYundaexService expressNumYundaexService;

    @Autowired
    private ExpressService expressService;

    @PostMapping("/import")
    public ResultVO importExpress(@RequestParam("myfile") MultipartFile file) {

        List<ExpressNumYundaex> expressNumList = new ArrayList<>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(file.getInputStream()));
            int numberOfSheets = workbook.getNumberOfSheets();
            // 遍历表格所有的 sheet, 我们一般仅适用 sheet1
            for (int i = 0; i < numberOfSheets; i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                // 遍历所有行
                for (int j = 0; j < physicalNumberOfRows; j++) {

                    ExpressNumYundaex expressNumYundaex = new ExpressNumYundaex();

                    // 取出来具体的一行
                    HSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    // 一行的实际列数
                    int physicalNumberOfCells = row.getPhysicalNumberOfCells();

                    // 遍历每行的所有列
                    for (int k = 0; k < physicalNumberOfCells; k++) {

                        // 每行的某一列的内容
                        HSSFCell cell = row.getCell(k);
                        switch (cell.getCellTypeEnum()) {
                            case STRING: {
                                String cellValue = StringUtils.trimToNull(cell.getStringCellValue());
                                switch (k) {
                                    case 0:
                                        if (!StringUtils.isEmpty(cellValue)) {
                                            expressNumYundaex.setExpNum(cellValue);
                                        }
                                        break;
                                }
                            }
                            break;
                            default: {
                            }
                            break;
                        }
                    }
                    expressNumList.add(expressNumYundaex);
                }
            }
        } catch (Exception e) {
            log.error("【Excel导入】单号导入异常: {}, 请检查Excel字段格式后重试.", e.getMessage());
            e.printStackTrace();
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(), e.getMessage()));
        }
        //导入数据去重
        List<ExpressNumYundaex> list1 =  new ArrayList<>();
        for  ( int  i  =   0 ; i  <  expressNumList.size()  -   1 ; i ++ )  {
            int aar = 0;
            for  ( int  j  =  expressNumList.size()  -   1 ; j  >  i; j -- )  {
                if  (expressNumList.get(j).getExpNum().equals(expressNumList.get(i).getExpNum()))  {
                    aar = 1;
                }
            }
            if(aar == 0){
                list1.add(expressNumList.get(i));
            }
        }
        List<ExpressNumYundaex> dbExpressNumList = expressNumYundaexService.findAll();
        List<ExpressNumYundaex> ls = new ArrayList<ExpressNumYundaex>();
        // 去除唯一索引后此法判断重复
        for (ExpressNumYundaex num : list1) {
           int i =0;
           for (ExpressNumYundaex expressNumYundaex : dbExpressNumList) {
                if (expressNumYundaex.getExpNum().equals(num.getExpNum())) {
//                  throw new GlobalException(ResultEnum.EXPRESS_NUM_IS_EXIST);
                	i =1;
                }
            }
           if(i == 0){
        	   ls.add(num);
           }
        }

        expressNumYundaexService.saveInBatch(ls);

        return ResultVOUtil.success();
    }

    @GetMapping("/getUnUsed")
    public ResultVO getUnUsed() {
        Integer num = expressNumYundaexService.countByStatus(ExpressNumStatusEnum.UNUSED.getCode());
        return ResultVOUtil.success(num);
    }
}
