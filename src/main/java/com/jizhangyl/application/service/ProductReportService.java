package com.jizhangyl.application.service;

import com.jizhangyl.application.dataobject.Shop;
import com.jizhangyl.application.dto.ShopDto;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/5 12:10
 * @description 商品报表形式导出
 */
@Slf4j
@Service
public class ProductReportService {

    @Autowired
    private ShopService shopService;

    /**
     * 导出至 excel
     * @param shopDtoList
     * @return
     */
    public ResponseEntity<byte[]> exportProductList2Excel(List<ShopDto> shopDtoList) {
        HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;

        try {
            // 1. 创建 excel 文档
            HSSFWorkbook workbook = new HSSFWorkbook();

            // 2. 创建文档摘要
            workbook.createInformationProperties();
            // 3. 设置文档的基本信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

            dsi.setCategory("集栈供应链商品列表");
            dsi.setManager("陈谦");
            dsi.setCompany("杭州集栈网络科技有限公司");

            SummaryInformation si = workbook.getSummaryInformation();
            si.setSubject("商品列表");
            si.setTitle("商品列表");
            si.setAuthor("杭州集栈网络科技有限公司");
            si.setComments("技术支持：杭州集栈网络科技有限公司");

            HSSFSheet sheet = workbook.createSheet("集栈供应链商品列表");

            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            sheet.setColumnWidth(0, 60 * 256);
            sheet.setColumnWidth(1, 12 * 256);
            sheet.setColumnWidth(2, 35 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 10 * 256);
            sheet.setColumnWidth(5, 20 * 256);
            sheet.setColumnWidth(6, 12 * 256);

            sheet.setColumnWidth(7, 8 * 256);
            sheet.setColumnWidth(8, 15 * 256);
            sheet.setColumnWidth(9, 15 * 256);
            sheet.setColumnWidth(10, 15 * 256);
            sheet.setColumnWidth(11, 10 * 256);
            sheet.setColumnWidth(12, 15 * 256);
            sheet.setColumnWidth(13, 10 * 256);
            sheet.setColumnWidth(14, 10 * 256);
            sheet.setColumnWidth(15, 10 * 256);
            sheet.setColumnWidth(16, 15 * 256);
            sheet.setColumnWidth(17, 15 * 256);
            sheet.setColumnWidth(18, 15 * 256);
            sheet.setColumnWidth(19, 60 * 256);

            sheet.setColumnWidth(20, 15 * 256);
            sheet.setColumnWidth(21, 15 * 256);
            sheet.setColumnWidth(22, 15 * 256);
            sheet.setColumnWidth(23, 15 * 256);
            sheet.setColumnWidth(24, 15 * 256);


            HSSFRow headerRow = sheet.createRow(0);

            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("商品名称");
            headCell0.setCellStyle(headerStyle);

            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("商品库存");
            headCell1.setCellStyle(headerStyle);

            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("商品图片地址");
            headCell2.setCellStyle(headerStyle);

            HSSFCell headCell3 = headerRow.createCell(3);
            headCell3.setCellValue("商品JAN_CODE");
            headCell3.setCellStyle(headerStyle);

            HSSFCell headCell4 = headerRow.createCell(4);
            headCell4.setCellValue("类别");
            headCell4.setCellStyle(headerStyle);


            HSSFCell headCell5 = headerRow.createCell(5);
            headCell5.setCellValue("品牌");
            headCell5.setCellStyle(headerStyle);

            HSSFCell headCell6 = headerRow.createCell(6);
            headCell6.setCellValue("商品每箱入数");
            headCell6.setCellStyle(headerStyle);

            HSSFCell headCell7 = headerRow.createCell(7);
            headCell7.setCellValue("商品规格");
            headCell7.setCellStyle(headerStyle);

            HSSFCell headCell8 = headerRow.createCell(8);
            headCell8.setCellValue("是否抛货");
            headCell8.setCellStyle(headerStyle);

            HSSFCell headCell9 = headerRow.createCell(9);
            headCell9.setCellValue("商品净重量");
            headCell9.setCellStyle(headerStyle);

            HSSFCell headCell10 = headerRow.createCell(10);
            headCell10.setCellValue("商品打包重量");
            headCell10.setCellStyle(headerStyle);

            HSSFCell headCell11 = headerRow.createCell(11);
            headCell11.setCellValue("商品颜色");
            headCell11.setCellStyle(headerStyle);

            HSSFCell headCell12 = headerRow.createCell(12);
            headCell12.setCellValue("商品长*宽*高");
            headCell12.setCellStyle(headerStyle);

            HSSFCell headCell13 = headerRow.createCell(13);
            headCell13.setCellValue("商品体积");
            headCell13.setCellStyle(headerStyle);

            HSSFCell headCell14 = headerRow.createCell(14);
            headCell14.setCellValue("商品供货价");
            headCell14.setCellStyle(headerStyle);


            HSSFCell headCell15 = headerRow.createCell(15);
            headCell15.setCellValue("市场零售价");
            headCell15.setCellStyle(headerStyle);


            HSSFCell headCell16 = headerRow.createCell(16);
            headCell16.setCellValue("BC件报关售价");
            headCell16.setCellStyle(headerStyle);


            HSSFCell headCell17 = headerRow.createCell(17);
            headCell17.setCellValue("BC件报关税率");
            headCell17.setCellStyle(headerStyle);


            HSSFCell headCell18 = headerRow.createCell(18);
            headCell18.setCellValue("BC件报关税金");
            headCell18.setCellStyle(headerStyle);

            HSSFCell headCell19 = headerRow.createCell(19);
            headCell19.setCellValue("朋友圈文案");
            headCell19.setCellStyle(headerStyle);

            HSSFCell headCell20 = headerRow.createCell(20);
            headCell20.setCellValue("商品状态");
            headCell20.setCellStyle(headerStyle);

            HSSFCell headCell21 = headerRow.createCell(21);
            headCell21.setCellValue("海关类目编号");
            headCell21.setCellStyle(headerStyle);

            HSSFCell headCell22 = headerRow.createCell(22);
            headCell22.setCellValue("海关税则号列");
            headCell22.setCellStyle(headerStyle);

            HSSFCell headCell23 = headerRow.createCell(23);
            headCell23.setCellValue("海关商品唯一码");
            headCell23.setCellStyle(headerStyle);

            HSSFCell headCell24 = headerRow.createCell(24);
            headCell24.setCellValue("仓库打包识别码");
            headCell24.setCellStyle(headerStyle);
            

            for (int i = 0; i < shopDtoList.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                ShopDto shopDto = shopDtoList.get(i);

                row.createCell(0).setCellValue(shopDto.getShopName());
                row.createCell(1).setCellValue(shopDto.getShopCount());
                row.createCell(2).setCellValue(shopDto.getShopImage());
                row.createCell(3).setCellValue(shopDto.getShopJan());
                row.createCell(4).setCellValue(shopDto.getCateName());
                row.createCell(5).setCellValue(shopDto.getBrandName());
                row.createCell(6).setCellValue(shopDto.getShopXcount());

                row.createCell(7).setCellValue(shopDto.getShopFormat());
                row.createCell(8).setCellValue(shopDto.getIsPaogoods());
                row.createCell(9).setCellValue(shopDto.getShopJweight());
                row.createCell(10).setCellValue(shopDto.getShopDweight());
                row.createCell(11).setCellValue(shopDto.getShopColor());
                row.createCell(12).setCellValue(shopDto.getShopWhg());
                row.createCell(13).setCellValue(shopDto.getShopVolume());
                row.createCell(14).setCellValue(shopDto.getShopGprice().toString());
                row.createCell(15).setCellValue(shopDto.getShopLprice().toString());
                row.createCell(16).setCellValue(shopDto.getWenan());
                row.createCell(17).setCellValue(shopDto.getShopStatus());
                row.createCell(18).setCellValue(shopDto.getPackCode());

            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            String dateStr = sdf.format(new Date());
            String fileName = "集栈供应链商品列表" + dateStr + ".xls";

            headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("utf-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            baos = new ByteArrayOutputStream();
            workbook.write(baos);

        } catch (Exception e) {
            log.error("【Excel导出】导出异常：{}", e);
            throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
        }
        return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.CREATED);
    }

    /**
     * 订单数据解析成 List<OrderDto>
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Shop> importShop2List(MultipartFile file) {
        List<ShopDto> shopDtoList = new ArrayList<>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(file.getInputStream()));
            int numberOfSheets = workbook.getNumberOfSheets();
            // 遍历表格所有的 sheet, 我们一般仅适用 sheet1
            for (int i = 0; i < numberOfSheets; i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                // 遍历所有行
                for (int j = 0; j < physicalNumberOfRows; j++) {
                    // 标题行
                    if (j == 0) {
                        continue;
                    }
                    // 从第二行开始，每行封装成一个 shopDto
                    ShopDto shopDto = new ShopDto();

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
                                String cellValue = org.apache.commons.lang3.StringUtils.trimToNull(cell.getStringCellValue());
                                switch (k) {
                                    // 设置买家指定的订单编号到预留字段1
                                    case 0:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopName(cellValue);
                                        }
                                        break;
                                    case 1:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopCount(Integer.parseInt(cellValue));
                                        }
                                        break;
                                    case 2:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            // TODO 图片地址暂时不传
                                        }
                                        break;
                                    case 3:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopJan(cellValue);
                                        }
                                        break;
                                    case 4:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setCateName(cellValue);
                                        }
                                        break;
                                    // 商品一的 orderDetail
                                    case 5:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setBrandName(cellValue);
                                        }
                                        break;
                                    case 6:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopXcount(Integer.parseInt(cellValue));
                                        }
                                        break;
                                    case 7:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopFormat(cellValue);
                                        }
                                        break;
                                    case 8:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setIsPaogoods(Integer.parseInt(cellValue));
                                        }
                                        break;
                                    case 9:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopJweight(Integer.parseInt(cellValue));
                                        }
                                        break;
                                    case 10:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopDweight(Integer.parseInt(cellValue));
                                        }
                                        break;
                                    // 商品四的 orderDetail
                                    case 11:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopColor(cellValue);
                                        }
                                        break;
                                    case 12:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopWhg(cellValue);
                                        }
                                        break;
                                    // 商品五的 orderDetail
                                    case 13:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopVolume(cellValue);
                                        }
                                        break;
                                    case 14:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopGprice(new BigDecimal(cellValue));
                                        }
                                        break;
                                    // 商品六的 orderDetail
                                    case 15:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setShopLprice(new BigDecimal(cellValue));
                                        }
                                        break;
                                    case 16:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
//                                            shopDto.setBcPrice(new BigDecimal(cellValue));
                                        }
                                        break;
                                    // 商品七的 orderDetail
                                    case 17:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
//                                            shopDto.setBcCess(Double.parseDouble(cellValue));
                                        }
                                        break;
                                    case 18:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
//                                            shopDto.setBcCprice(new BigDecimal(cellValue));
                                        }
                                        break;
                                    // 商品八的 orderDetail
                                    case 19:
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(cellValue)) {
                                            shopDto.setWenan(cellValue);
                                        }
                                        break;
                                }
                            }
                            break;
                            default: {
                                double cellValue = cell.getNumericCellValue();
                                switch (k) {
                                    case 1:
                                        shopDto.setShopCount(Integer.parseInt(new DecimalFormat("0").format(cellValue)));
                                        break;
                                    case 6:
                                        shopDto.setShopXcount(Integer.parseInt(new DecimalFormat("0").format(cellValue)));
                                        break;
                                    case 8:
                                        shopDto.setIsPaogoods(Integer.parseInt(new DecimalFormat("0").format(cellValue)));
                                        break;
                                    case 9:
                                        shopDto.setShopJweight(Integer.parseInt(new DecimalFormat("0").format(cellValue)));
                                        break;
                                    case 10:
                                        shopDto.setShopDweight(Integer.parseInt(new DecimalFormat("0").format(cellValue)));
                                        break;
                                    case 13:
                                        shopDto.setShopVolume(new DecimalFormat("0").format(cellValue));
                                        break;
                                    case 14:
                                        shopDto.setShopGprice(new BigDecimal(cellValue));
                                        break;
                                    // 商品六的 orderDetail
                                    case 15:
                                        shopDto.setShopLprice(new BigDecimal(cellValue));
                                        break;
                                    case 16:
//                                        shopDto.setBcPrice(new BigDecimal(cellValue));
                                        break;
                                    // 商品七的 orderDetail
                                    case 17:
//                                        shopDto.setBcCess(cellValue);
                                        break;
                                    case 18:
//                                        shopDto.setBcCprice(new BigDecimal(cellValue));
                                        break;
//                                    case 0:
//                                        Double cellValue = cell.getNumericCellValue();
//                                        Integer wxUserId = cellValue.intValue();
//                                        wxuserAddr.setOpenid(wxUserId);
//                                        break;
                                    // 考虑传递过来的手机号码为 Numeric 类型
                                    /*case 2:
                                        Double phone = cell.getNumericCellValue();
                                        wxuserAddr.setPhone(String.valueOf(new BigDecimal(phone)));
                                        break;*/
                                }
                            }
                            break;
                        }
                    }
                    shopDtoList.add(shopDto);
                }
            }
        } catch (Exception e) {
            log.error("【Excel导入】订单导入异常: {}, 请检查Excel字段格式后重试.", e.getMessage());
            e.printStackTrace();
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(), e.getMessage()));
        }
        List<Shop> shopList = shopDtoList2Db(shopDtoList);

        return shopList;
    }

    /**
     * 导入的shopDtoList入库
     * @param shopDtoList
     * @return
     */
    public List<Shop> shopDtoList2Db(List<ShopDto> shopDtoList) {
        return shopService.saveInBatch(shopDtoList);
    }
}
