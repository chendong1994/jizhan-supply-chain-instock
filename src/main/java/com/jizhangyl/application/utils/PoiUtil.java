package com.jizhangyl.application.utils;

import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.enums.AddrTypeEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/5 12:10
 * @description Poi 工具类
 */
@Slf4j
public class PoiUtil {

    /**
     * 导出至 excel
     * @param addrList
     * @return
     */
    public static ResponseEntity<byte[]> exportWxUserAddr2Excel(List<WxuserAddr> addrList) {
        HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;

        try {
            // 1. 创建 excel 文档
            HSSFWorkbook workbook = new HSSFWorkbook();

            // 2. 创建文档摘要
            workbook.createInformationProperties();
            // 3. 设置文档的基本信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

            dsi.setCategory("用户地址");
            dsi.setManager("杨贤达");
            dsi.setCompany("杭州集栈网路科技有限公司");

            SummaryInformation si = workbook.getSummaryInformation();
            si.setSubject("用户地址表");
            si.setTitle("用户地址");
            si.setAuthor("杭州集栈网路科技有限公司");
            si.setComments("技术支持：杭州集栈网路科技有限公司");

            HSSFSheet sheet = workbook.createSheet("集栈供应链用户地址表");

            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            sheet.setColumnWidth(0, 12 * 256);
            sheet.setColumnWidth(1, 12 * 256);
            sheet.setColumnWidth(2, 15 * 256);
            sheet.setColumnWidth(3, 20 * 256);
            sheet.setColumnWidth(4, 50 * 256);
            sheet.setColumnWidth(5, 12 * 256);
            sheet.setColumnWidth(6, 12 * 256);

            HSSFRow headerRow = sheet.createRow(0);

            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("用户编号");
            headCell0.setCellStyle(headerStyle);

            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("收件人姓名");
            headCell1.setCellStyle(headerStyle);

            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("手机号码");
            headCell2.setCellStyle(headerStyle);

            HSSFCell headCell3 = headerRow.createCell(3);
            headCell3.setCellValue("所在地区");
            headCell3.setCellStyle(headerStyle);

            HSSFCell headCell4 = headerRow.createCell(4);
            headCell4.setCellValue("详细地址");
            headCell4.setCellStyle(headerStyle);


            HSSFCell headCell5 = headerRow.createCell(5);
            headCell5.setCellValue("地址标签");
            headCell5.setCellStyle(headerStyle);

            HSSFCell headCell6 = headerRow.createCell(6);
            headCell6.setCellValue("地址类型");
            headCell6.setCellStyle(headerStyle);
            

            for (int i = 0; i < addrList.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                WxuserAddr wxuserAddr = addrList.get(i);

                row.createCell(0).setCellValue(wxuserAddr.getOpenid());
                row.createCell(1).setCellValue(wxuserAddr.getReceiver());
                row.createCell(2).setCellValue(wxuserAddr.getPhone());
                row.createCell(3).setCellValue(wxuserAddr.getArea());
                row.createCell(4).setCellValue(wxuserAddr.getDetailAddr());
                row.createCell(5).setCellValue(wxuserAddr.getAddrLabel());
                row.createCell(6).setCellValue(
                        wxuserAddr.getIsDefault().equals(AddrTypeEnum.DEFAULT_ADDRESS.getCode().shortValue())
                                ? AddrTypeEnum.DEFAULT_ADDRESS.getMsg()
                                : AddrTypeEnum.OTHER_ADDRESS.getMsg());
            }
            
            headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", new String("用户地址表.xls".getBytes("utf-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            baos = new ByteArrayOutputStream();
            workbook.write(baos);

        } catch (Exception e) {
            log.error("【Excel导出】导出异常：{}", e);
            throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
        }
        return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.CREATED);
    }

    public static List<WxuserAddr> importWxUserAddr2List(MultipartFile file) {
        List<WxuserAddr> addrList = new ArrayList<>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(file.getInputStream()));
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                WxuserAddr wxuserAddr = new WxuserAddr();
                for (int j = 0; j < physicalNumberOfRows; j++) {
                    // 标题行
                    if (j == 0) {
                        continue;
                    }
                    HSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    int physicalNumberOfCells = row.getPhysicalNumberOfCells();
                    wxuserAddr = new WxuserAddr();
                    for (int k = 0; k < physicalNumberOfCells; k++) {
                        HSSFCell cell = row.getCell(k);
                        switch (cell.getCellTypeEnum()) {

                            case STRING: {
                                String cellValue = handlerString(cell.getStringCellValue());
                                switch (k) {
                                    // 考虑传递过来的 id 不是数值类型
                                    case 0:
                                        wxuserAddr.setOpenid(cellValue);
                                        break;
                                    case 1:
                                        wxuserAddr.setReceiver(cellValue);
                                        break;
                                    case 2:
                                        wxuserAddr.setPhone(cellValue);
                                        break;
                                    case 3:
                                        wxuserAddr.setArea(cellValue);
                                        break;
                                    case 4:
                                        wxuserAddr.setDetailAddr(cellValue);
                                        break;
                                    case 5:
                                        wxuserAddr.setAddrLabel(cellValue);
                                        break;
                                    case 6:
                                        wxuserAddr.setIsDefault(cellValue.trim().equals(AddrTypeEnum.DEFAULT_ADDRESS.getMsg()) ? AddrTypeEnum.DEFAULT_ADDRESS.getCode().shortValue()
                                                : AddrTypeEnum.OTHER_ADDRESS.getCode().shortValue());
                                        break;
                                }
                            }
                            break;
                            default: {
                                switch (k) {
//                                    case 0:
//                                        Double cellValue = cell.getNumericCellValue();
//                                        Integer wxUserId = cellValue.intValue();
//                                        wxuserAddr.setOpenid(wxUserId);
//                                        break;
                                    // 考虑传递过来的手机号码为 Numeric 类型
                                    case 2:
                                        Double phone = cell.getNumericCellValue();
                                        wxuserAddr.setPhone(String.valueOf(new BigDecimal(phone)));
                                        break;
                                }
                            }
                            break;
                        }
                    }
                    addrList.add(wxuserAddr);
                }
            }
        } catch (Exception e) {
            log.error("【Excel导入】导入异常：{}", e);
            throw new GlobalException(ResultEnum.EXCEL_IMPORT_ERROR);
        }
        return addrList;

    }

    private static String handlerString(String string) {
        if (StringUtils.isEmpty(string)) {
            return "";
        } else {
            return string.trim();
        }
    }
}
