package com.jizhangyl.application.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jizhangyl.application.enums.ResultEnum;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jizhangyl.application.dataobject.primary.OrderForRepository;
import com.jizhangyl.application.dto.AllDataDTO;
import com.jizhangyl.application.dto.BuyerSalesDTO;
import com.jizhangyl.application.dto.ShopSalesDTO;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.ExportService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 曲健磊
 * @date 2018年9月18日 上午11:12:16
 * @description exportService的实现类
 */
@Slf4j
@Service
public class ExportServiceImpl implements ExportService {

	/**
	 * 导出全站的数据
	 * @param allDataDTOList 全站数据的list集合
	 * @param startTime 开始日期
	 * @param endTime 截止日期
	 * @return excel文件
	 */
	@Override
	public ResponseEntity<byte[]> exportAllData(List<AllDataDTO> allDataDTOList, String startTime, String endTime) {
		HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;
        try {
            // 1. 创建 excel 文档
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 2. 创建文档摘要
            workbook.createInformationProperties();
            // 3. 设置文档的基本信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

            dsi.setCategory("全站数据");
            dsi.setManager("陈谦");
            dsi.setCompany("杭州集栈网络科技有限公司");

            SummaryInformation si = workbook.getSummaryInformation();
            si.setSubject("全站数据");
            si.setTitle("全站数据");
            si.setAuthor("杭州集栈网络科技有限公司");
            si.setComments("技术支持：杭州集栈网络科技有限公司");

            HSSFSheet sheet = workbook.createSheet("全站数据");

            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            sheet.setColumnWidth(0, 16 * 256);
            sheet.setColumnWidth(1, 16 * 256);
            sheet.setColumnWidth(2, 16 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 16 * 256);
            sheet.setColumnWidth(5, 16 * 256);
            sheet.setColumnWidth(5, 16 * 256);

            HSSFRow headerRow = sheet.createRow(0);
            
            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("日期");
            headCell0.setCellStyle(headerStyle);

            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("总货值");
            headCell1.setCellStyle(headerStyle);

            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("总关税");
            headCell2.setCellStyle(headerStyle);

            HSSFCell headCell3 = headerRow.createCell(3);
            headCell3.setCellValue("总货值+关税");
            headCell3.setCellStyle(headerStyle);

            HSSFCell headCell4 = headerRow.createCell(4);
            headCell4.setCellValue("总运费");
            headCell4.setCellStyle(headerStyle);

            HSSFCell headCell5 = headerRow.createCell(5);
            headCell5.setCellValue("总订单数");
            headCell5.setCellStyle(headerStyle);
            
            HSSFCell headCell6 = headerRow.createCell(6);
            headCell6.setCellValue("全站客单价");
            headCell6.setCellStyle(headerStyle);

            SimpleDateFormat sdfEndDate = new SimpleDateFormat("yyyy.MM.dd");
            for (int i = 0; i < allDataDTOList.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                AllDataDTO allDataDTO = allDataDTOList.get(i);
                row.createCell(0).setCellValue(allDataDTO.getDate());
                row.createCell(1).setCellValue(allDataDTO.getCost().doubleValue());
                row.createCell(2).setCellValue(allDataDTO.getTaxes().doubleValue());
                row.createCell(3).setCellValue(allDataDTO.getCostAndTaxes().doubleValue());
                row.createCell(4).setCellValue(allDataDTO.getFreight().doubleValue());
                row.createCell(5).setCellValue(allDataDTO.getOrderNums());
                row.createCell(6).setCellValue(allDataDTO.getCustomerPrice().doubleValue());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = sdf.format(new Date());

            String startTimeStr = sdf.format(sdfStandard.parse(startTime));
            String endTimeStr = sdf.format(sdfStandard.parse(endTime));

            String fileName = "全站数据统计" + "[" + startTimeStr + "-" + endTimeStr + "]" + dateStr + ".xls";

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
	 * 导出商品的销售额以及销量
	 * @param shopSalesDTOListMap 存储了时间段范围内的每一天商品的销售额以及销量的list集合
	 * key为日期格式的字符串"2018-09-19",value为对应那天的所有商品的销售额以及销量
	 * @param startTime 开始日期
	 * @param endTime 截止日期
	 * @return excel文件
	 */
	@Override
	public ResponseEntity<byte[]> exportShop(Map<String, List<ShopSalesDTO>> shopSalesDTOListMap, String startTime, String endTime) {
		HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;
        try {
            // 1. 创建 excel 文档
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 2. 创建文档摘要
            workbook.createInformationProperties();
            // 3. 设置文档的基本信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

            dsi.setCategory("商品的销售额以及销量");
            dsi.setManager("陈谦");
            dsi.setCompany("杭州集栈网络科技有限公司");

            SummaryInformation si = workbook.getSummaryInformation();
            si.setSubject("商品的销售额以及销量");
            si.setTitle("商品的销售额以及销量");
            si.setAuthor("杭州集栈网络科技有限公司");
            si.setComments("技术支持：杭州集栈网络科技有限公司");

            Set<Entry<String, List<ShopSalesDTO>>> entrySet = shopSalesDTOListMap.entrySet();

            // 只建立一张sheet
            HSSFSheet sheet = workbook.createSheet("商品的销售额以及销量统计");
            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            sheet.setColumnWidth(0, 12 * 256);
            sheet.setColumnWidth(1, 16 * 256);
            sheet.setColumnWidth(2, 80 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 16 * 256);
            sheet.setColumnWidth(5, 12 * 256);
            
            HSSFRow headerRow = sheet.createRow(0);
            
            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("日期");
            headCell0.setCellStyle(headerStyle);
            
            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("商品jan code");
            headCell1.setCellStyle(headerStyle);
            
            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("商品名称");
            headCell2.setCellStyle(headerStyle);
            
            HSSFCell headCell3 = headerRow.createCell(3);
            headCell3.setCellValue("商品销售额");
            headCell3.setCellStyle(headerStyle);
            
            HSSFCell headCell4 = headerRow.createCell(4);
            headCell4.setCellValue("商品出货量");
            headCell4.setCellStyle(headerStyle);
            
            HSSFCell headCell5 = headerRow.createCell(5);
            headCell5.setCellValue("当前库存");
            headCell5.setCellStyle(headerStyle);
            
            // 存储map中的所有list
            List<ShopSalesDTO> allList = new LinkedList<ShopSalesDTO>();
            
            // 把map中的每个list都放入allList中
            for (Entry<String, List<ShopSalesDTO>> entry : entrySet) {
                List<ShopSalesDTO> shopSalesDTOList = entry.getValue();
                
                Collections.sort(shopSalesDTOList, new Comparator<ShopSalesDTO>(){
    				@Override
    				public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
    					if (o1.getShopSales().doubleValue() > o2.getShopSales().doubleValue()) {
    						return -1;
    					} else if (o1.getShopSales().doubleValue() < o2.getShopSales().doubleValue()) {
    						return 1;
    					}
    					return 0;
    				}
    			});
                
                allList.addAll(shopSalesDTOList);
            }
            
            for (int i = 0; i < allList.size(); i++) {
                ShopSalesDTO shopSalesDTO = allList.get(i);
                HSSFRow row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(shopSalesDTO.getDate());
                row.createCell(1).setCellValue(shopSalesDTO.getShopJan());

                // 商品名称左对齐
                HSSFCell cell1 = row.createCell(2);
                cell1.setCellValue(shopSalesDTO.getShopName());

                // 保留小数点后两位小数,返回数字类型则在单元格中右对齐
                HSSFCell cell2 = row.createCell(3);
                HSSFCellStyle cellStyle2 = cell2.getCellStyle();
                cellStyle2.setAlignment(HorizontalAlignment.RIGHT);
                cell2.setCellStyle(cellStyle2);
                cell2.setCellValue(formatToNumber(shopSalesDTO.getShopSales()));

                row.createCell(4).setCellValue(shopSalesDTO.getShopSalesNum());
                row.createCell(5).setCellValue(shopSalesDTO.getInventory());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = sdf.format(new Date());

            String startTimeStr = sdf.format(sdfStandard.parse(startTime));
            String endTimeStr = sdf.format(sdfStandard.parse(endTime));

            String fileName = "商品的销售额以及销售数量统计" + "[" + startTimeStr + "-" + endTimeStr + "]" + dateStr + ".xls";

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
	
	/*
	@Override
	public ResponseEntity<byte[]> exportShop(Map<String, List<ShopSalesDTO>> shopSalesDTOListMap, String startTime, String endTime) {
	    HttpHeaders headers = null;
	    ByteArrayOutputStream baos = null;
	    try {
	        // 1. 创建 excel 文档
	        HSSFWorkbook workbook = new HSSFWorkbook();
	        // 2. 创建文档摘要
	        workbook.createInformationProperties();
	        // 3. 设置文档的基本信息
	        DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();
	        
	        dsi.setCategory("商品的销售额以及销量");
	        dsi.setManager("陈谦");
	        dsi.setCompany("杭州集栈网络科技有限公司");
	        
	        SummaryInformation si = workbook.getSummaryInformation();
	        si.setSubject("商品的销售额以及销量");
	        si.setTitle("商品的销售额以及销量");
	        si.setAuthor("杭州集栈网络科技有限公司");
	        si.setComments("技术支持：杭州集栈网络科技有限公司");
	        
	        // 有多少entry就创建多少张工作表
	        Set<Entry<String, List<ShopSalesDTO>>> entrySet = shopSalesDTOListMap.entrySet();
	        
	        for (Entry<String, List<ShopSalesDTO>> entry : entrySet) {
	            
	            HSSFSheet sheet = workbook.createSheet(entry.getKey().substring(0, 10));
	            
	            // 4. 创建日期显示格式 (备用)
	            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
	            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	            // 5. 创建标题的显示样式
	            HSSFCellStyle headerStyle = workbook.createCellStyle();
	            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
	            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	            headerStyle.setAlignment(HorizontalAlignment.CENTER);
	            
	            sheet.setColumnWidth(0, 12 * 256);
	            sheet.setColumnWidth(1, 16 * 256);
	            sheet.setColumnWidth(2, 80 * 256);
	            sheet.setColumnWidth(3, 16 * 256);
	            sheet.setColumnWidth(4, 16 * 256);
	            sheet.setColumnWidth(5, 12 * 256);
	            
	            HSSFRow headerRow = sheet.createRow(0);
	            
	            HSSFCell headCell0 = headerRow.createCell(0);
	            headCell0.setCellValue("日期");
	            headCell0.setCellStyle(headerStyle);
	            
	            HSSFCell headCell1 = headerRow.createCell(1);
	            headCell1.setCellValue("商品jan code");
	            headCell1.setCellStyle(headerStyle);
	            
	            HSSFCell headCell2 = headerRow.createCell(2);
	            headCell2.setCellValue("商品名称");
	            headCell2.setCellStyle(headerStyle);
	            
	            HSSFCell headCell3 = headerRow.createCell(3);
	            headCell3.setCellValue("商品销售额");
	            headCell3.setCellStyle(headerStyle);
	            
	            HSSFCell headCell4 = headerRow.createCell(4);
	            headCell4.setCellValue("商品出货量");
	            headCell4.setCellStyle(headerStyle);
	            
	            HSSFCell headCell5 = headerRow.createCell(5);
	            headCell5.setCellValue("当前库存");
	            headCell5.setCellStyle(headerStyle);
	            
	            List<ShopSalesDTO> shopSalesDTOList = entry.getValue();
	            Collections.sort(shopSalesDTOList, new Comparator<ShopSalesDTO>(){
	                @Override
	                public int compare(ShopSalesDTO o1, ShopSalesDTO o2) {
	                    if (o1.getShopSales().doubleValue() > o2.getShopSales().doubleValue()) {
	                        return -1;
	                    } else if (o1.getShopSales().doubleValue() < o2.getShopSales().doubleValue()) {
	                        return 1;
	                    }
	                    return 0;
	                }
	            });
	            for (int i = 0; i < shopSalesDTOList.size(); i++) {
	                ShopSalesDTO shopSalesDTO = shopSalesDTOList.get(i);
	                HSSFRow row = sheet.createRow(i + 1);
	                
	                row.createCell(0).setCellValue(shopSalesDTO.getDate());
	                row.createCell(1).setCellValue(shopSalesDTO.getShopJan());
	                
	                // 商品名称左对齐
	                HSSFCell cell1 = row.createCell(2);
	                cell1.setCellValue(shopSalesDTO.getShopName());
	                
	                // 保留小数点后两位小数,返回数字类型则在单元格中右对齐
	                HSSFCell cell2 = row.createCell(3);
	                HSSFCellStyle cellStyle2 = cell2.getCellStyle();
	                cellStyle2.setAlignment(HorizontalAlignment.RIGHT);
	                cell2.setCellStyle(cellStyle2);
	                cell2.setCellValue(formatToNumber(shopSalesDTO.getShopSales()));
	                
	                row.createCell(4).setCellValue(shopSalesDTO.getShopSalesNum());
	                row.createCell(5).setCellValue(shopSalesDTO.getInventory());
	            }
	        }
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
	        SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String dateStr = sdf.format(new Date());
	        
	        String startTimeStr = sdf.format(sdfStandard.parse(startTime));
	        String endTimeStr = sdf.format(sdfStandard.parse(endTime));
	        
	        String fileName = "商品的销售额以及销售数量统计" + "[" + startTimeStr + "-" + endTimeStr + "]" + dateStr + ".xls";
	        
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
	}*/
	
	/**
	 * 导出买手的销售额以及所下订单数
	 * @param buyerSalesDTOListMap 存储了时间段范围内的每一天买手的销售额以及所下订单数的map集合
	 * key为日期格式的字符串"2018-09-19",value为对应那天的所有买手的销售额以及所下订单数
	 * @param startTime 开始日期
	 * @param endTime 截止日期
	 * @return excel文件
	 */
	@Override
	public ResponseEntity<byte[]> exportBuyer(Map<String, List<BuyerSalesDTO>> buyerSalesDTOListMap, String startTime, String endTime) {
		HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;
        try {
            // 1. 创建 excel 文档
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 2. 创建文档摘要
            workbook.createInformationProperties();
            // 3. 设置文档的基本信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

            dsi.setCategory("买手的销售额以及所下订单数");
            dsi.setManager("陈谦");
            dsi.setCompany("杭州集栈网络科技有限公司");

            SummaryInformation si = workbook.getSummaryInformation();
            si.setSubject("买手的销售额以及所下订单数");
            si.setTitle("买手的销售额以及所下订单数");
            si.setAuthor("杭州集栈网络科技有限公司");
            si.setComments("技术支持：杭州集栈网络科技有限公司");

            Set<Entry<String, List<BuyerSalesDTO>>> entrySet = buyerSalesDTOListMap.entrySet();

            // 有多少entry就创建多少张工作表
            HSSFSheet sheet = workbook.createSheet("买手销售额以及所下订单数统计");
            
            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            sheet.setColumnWidth(0, 12 * 256);
            sheet.setColumnWidth(1, 15 * 256);
            sheet.setColumnWidth(2, 12 * 256);
            sheet.setColumnWidth(3, 12 * 256);
            sheet.setColumnWidth(4, 14 * 256);
            sheet.setColumnWidth(5, 14 * 256);
            
            HSSFRow headerRow = sheet.createRow(0);
            
            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("日期");
            headCell0.setCellStyle(headerStyle);
            
            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("买手邀请码");
            headCell1.setCellStyle(headerStyle);
            
            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("买手姓名");
            headCell2.setCellStyle(headerStyle);
            
            HSSFCell headCell3 = headerRow.createCell(3);
            headCell3.setCellValue("销售额");
            headCell3.setCellStyle(headerStyle);
            
            HSSFCell headCell4 = headerRow.createCell(4);
            headCell4.setCellValue("订单量");
            headCell4.setCellStyle(headerStyle);
            
            HSSFCell headCell5 = headerRow.createCell(5);
            headCell5.setCellValue("买手下游人数");
            headCell5.setCellStyle(headerStyle);
            
            // 存储所有买手销售信息的LinkedList
            List<BuyerSalesDTO> allList = new LinkedList<BuyerSalesDTO>();
            
            for (Entry<String, List<BuyerSalesDTO>> entry : entrySet) {
                List<BuyerSalesDTO> buyerSalesDTOList = entry.getValue();
                Collections.sort(buyerSalesDTOList, new Comparator<BuyerSalesDTO>(){
    				@Override
    				public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
    					if (o1.getBuyerSales().doubleValue() > o2.getBuyerSales().doubleValue()) {
    						return -1;
    					} else if (o1.getBuyerSales().doubleValue() < o2.getBuyerSales().doubleValue()) {
    						return 1;
    					}
    					return 0;
    				}
    			});
                
                allList.addAll(buyerSalesDTOList);
            }
            
            for (int i = 0; i < allList.size(); i++) {
                BuyerSalesDTO buyerSalesDTO = allList.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                
                row.createCell(0).setCellValue(buyerSalesDTO.getDate());
                row.createCell(1).setCellValue(buyerSalesDTO.getInviteCode());
                row.createCell(2).setCellValue(buyerSalesDTO.getBuyerName());
                
                // 保留小数点后两位小数,返回数字类型则在单元格中右对齐
                HSSFCell cell = row.createCell(3);
                HSSFCellStyle cellStyle = cell.getCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(formatToNumber(buyerSalesDTO.getBuyerSales()));
                
                row.createCell(4).setCellValue(buyerSalesDTO.getOrderNums());
                row.createCell(5).setCellValue(buyerSalesDTO.getDownStreamCount());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = sdf.format(new Date());

            String startTimeStr = sdf.format(sdfStandard.parse(startTime));
            String endTimeStr = sdf.format(sdfStandard.parse(endTime));

            String fileName = "买手的销售额以及订单数统计" + "[" + startTimeStr + "-" + endTimeStr + "]" + dateStr + ".xls";

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
	
	/*@Override
	public ResponseEntity<byte[]> exportBuyer(Map<String, List<BuyerSalesDTO>> buyerSalesDTOListMap, String startTime, String endTime) {
	    HttpHeaders headers = null;
	    ByteArrayOutputStream baos = null;
	    try {
	        // 1. 创建 excel 文档
	        HSSFWorkbook workbook = new HSSFWorkbook();
	        // 2. 创建文档摘要
	        workbook.createInformationProperties();
	        // 3. 设置文档的基本信息
	        DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();
	        
	        dsi.setCategory("买手的销售额以及所下订单数");
	        dsi.setManager("陈谦");
	        dsi.setCompany("杭州集栈网络科技有限公司");
	        
	        SummaryInformation si = workbook.getSummaryInformation();
	        si.setSubject("买手的销售额以及所下订单数");
	        si.setTitle("买手的销售额以及所下订单数");
	        si.setAuthor("杭州集栈网络科技有限公司");
	        si.setComments("技术支持：杭州集栈网络科技有限公司");
	        
	        // 有多少entry就创建多少张工作表
	        Set<Entry<String, List<BuyerSalesDTO>>> entrySet = buyerSalesDTOListMap.entrySet();
	        
	        for (Entry<String, List<BuyerSalesDTO>> entry : entrySet) {
	            HSSFSheet sheet = workbook.createSheet(entry.getKey().substring(0, 10));
	            
	            // 4. 创建日期显示格式 (备用)
	            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
	            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	            // 5. 创建标题的显示样式
	            HSSFCellStyle headerStyle = workbook.createCellStyle();
	            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
	            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	            
	            sheet.setColumnWidth(0, 12 * 256);
	            sheet.setColumnWidth(1, 15 * 256);
	            sheet.setColumnWidth(2, 12 * 256);
	            sheet.setColumnWidth(3, 12 * 256);
	            sheet.setColumnWidth(4, 14 * 256);
	            sheet.setColumnWidth(5, 14 * 256);
	            
	            HSSFRow headerRow = sheet.createRow(0);
	            
	            HSSFCell headCell0 = headerRow.createCell(0);
	            headCell0.setCellValue("日期");
	            headCell0.setCellStyle(headerStyle);
	            
	            HSSFCell headCell1 = headerRow.createCell(1);
	            headCell1.setCellValue("买手邀请码");
	            headCell1.setCellStyle(headerStyle);
	            
	            HSSFCell headCell2 = headerRow.createCell(2);
	            headCell2.setCellValue("买手姓名");
	            headCell2.setCellStyle(headerStyle);
	            
	            HSSFCell headCell3 = headerRow.createCell(3);
	            headCell3.setCellValue("销售额");
	            headCell3.setCellStyle(headerStyle);
	            
	            HSSFCell headCell4 = headerRow.createCell(4);
	            headCell4.setCellValue("订单量");
	            headCell4.setCellStyle(headerStyle);
	            
	            HSSFCell headCell5 = headerRow.createCell(5);
	            headCell5.setCellValue("买手下游人数");
	            headCell5.setCellStyle(headerStyle);
	            
	            List<BuyerSalesDTO> buyerSalesDTOList = entry.getValue();
	            Collections.sort(buyerSalesDTOList, new Comparator<BuyerSalesDTO>(){
	                @Override
	                public int compare(BuyerSalesDTO o1, BuyerSalesDTO o2) {
	                    if (o1.getBuyerSales().doubleValue() > o2.getBuyerSales().doubleValue()) {
	                        return -1;
	                    } else if (o1.getBuyerSales().doubleValue() < o2.getBuyerSales().doubleValue()) {
	                        return 1;
	                    }
	                    return 0;
	                }
	            });
	            
	            for (int i = 0; i < buyerSalesDTOList.size(); i++) {
	                BuyerSalesDTO buyerSalesDTO = buyerSalesDTOList.get(i);
	                HSSFRow row = sheet.createRow(i + 1);
	                
	                row.createCell(0).setCellValue(buyerSalesDTO.getDate());
	                row.createCell(1).setCellValue(buyerSalesDTO.getInviteCode());
	                row.createCell(2).setCellValue(buyerSalesDTO.getBuyerName());
	                
	                // 保留小数点后两位小数,返回数字类型则在单元格中右对齐
	                HSSFCell cell = row.createCell(3);
	                HSSFCellStyle cellStyle = cell.getCellStyle();
	                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
	                cell.setCellStyle(cellStyle);
	                cell.setCellValue(formatToNumber(buyerSalesDTO.getBuyerSales()));
	                
	                row.createCell(4).setCellValue(buyerSalesDTO.getOrderNums());
	                row.createCell(5).setCellValue(buyerSalesDTO.getDownStreamCount());
	            }
	        }
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
	        SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String dateStr = sdf.format(new Date());
	        
	        String startTimeStr = sdf.format(sdfStandard.parse(startTime));
	        String endTimeStr = sdf.format(sdfStandard.parse(endTime));
	        
	        String fileName = "买手的销售额以及订单数统计" + "[" + startTimeStr + "-" + endTimeStr + "]" + dateStr + ".xls";
	        
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
	}*/

	/**
	 * 保留小数点后两位小数,不足的补零
	 * @param obj
	 * @return
	 */
	private static String formatToNumber(BigDecimal obj) {
        DecimalFormat df = new DecimalFormat("#.00");
        if(obj.compareTo(BigDecimal.ZERO)==0) {
            return "0.00";
        }else if(obj.compareTo(BigDecimal.ZERO)>0&&obj.compareTo(new BigDecimal(1))<0){
            return "0"+df.format(obj).toString();
        }else {
            return df.format(obj).toString();
        }
    }

	/**
     * 导出指定的订单列表
     * @param orderForRepositoryList 待导出的订单列表
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @return excel文件
     */
    @Override
    public ResponseEntity<byte[]> exportOrder(List<OrderForRepository> orderForRepositoryList, Date startTime, Date endTime) {
        HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;

        try {
            // 1. 创建 excel 文档
            HSSFWorkbook workbook = new HSSFWorkbook();

            // 2. 创建文档摘要
            workbook.createInformationProperties();
            // 3. 设置文档的基本信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

            dsi.setCategory("订单导出（仓库打包）");
            dsi.setManager("陈谦");
            dsi.setCompany("杭州集栈网络科技有限公司");

            SummaryInformation si = workbook.getSummaryInformation();
            si.setSubject("订单导出（仓库打包）");
            si.setTitle("订单导出（仓库打包）");
            si.setAuthor("杭州集栈网络科技有限公司");
            si.setComments("技术支持：杭州集栈网络科技有限公司");

            HSSFSheet sheet = workbook.createSheet("订单导出（仓库打包）");

            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            sheet.setColumnWidth(0, 20 * 256);
            sheet.setColumnWidth(1, 20 * 256);
            sheet.setColumnWidth(2, 10 * 256);
            sheet.setColumnWidth(3, 14 * 256);
            sheet.setColumnWidth(4, 60 * 256);
            sheet.setColumnWidth(5, 12 * 256);
            sheet.setColumnWidth(6, 12 * 256);

            sheet.setColumnWidth(7, 15 * 256);
            sheet.setColumnWidth(8, 15 * 256);
            sheet.setColumnWidth(9, 15 * 256);
            sheet.setColumnWidth(10, 15 * 256);
            sheet.setColumnWidth(11, 14 * 256);     // 预计到达时间
            sheet.setColumnWidth(12, 10 * 256);
            sheet.setColumnWidth(13, 30 * 256);
            sheet.setColumnWidth(14, 22 * 256);
            sheet.setColumnWidth(15, 16 * 256);     // 海关商品分类编号
            sheet.setColumnWidth(16, 15 * 256);
            sheet.setColumnWidth(17, 20 * 256);
            sheet.setColumnWidth(18, 20 * 256);     // 商品报关售价总和
            sheet.setColumnWidth(19, 20 * 256);     // 商品报关税金总和
            sheet.setColumnWidth(20, 20 * 256);     // 商品打包重量总和
            sheet.setColumnWidth(21, 15 * 256);
            sheet.setColumnWidth(22, 15 * 256);
            sheet.setColumnWidth(23, 20 * 256);     // 订单实收包裹重量
            sheet.setColumnWidth(24, 15 * 256);     
            sheet.setColumnWidth(25, 15 * 256);     
            sheet.setColumnWidth(26, 15 * 256);     
            sheet.setColumnWidth(27, 15 * 256);

            HSSFRow headerRow = sheet.createRow(0);

            HSSFCell headCell35 = headerRow.createCell(0);
            headCell35.setCellValue("订单时间");
            headCell35.setCellStyle(headerStyle);
            
            HSSFCell headCell31 = headerRow.createCell(1);
            headCell31.setCellValue("订单状态");
            headCell31.setCellStyle(headerStyle);
            
            HSSFCell headCell1 = headerRow.createCell(2);
            headCell1.setCellValue("收件人名称");
            headCell1.setCellStyle(headerStyle);

            HSSFCell headCell3 = headerRow.createCell(3);
            headCell3.setCellValue("收件人电话");
            headCell3.setCellStyle(headerStyle);

            HSSFCell headCell4 = headerRow.createCell(4);
            headCell4.setCellValue("收货人地址");
            headCell4.setCellStyle(headerStyle);

            HSSFCell headCell5 = headerRow.createCell(5);
            headCell5.setCellValue("省份");
            headCell5.setCellStyle(headerStyle);

            HSSFCell headCell6 = headerRow.createCell(6);
            headCell6.setCellValue("城市");
            headCell6.setCellStyle(headerStyle);

            HSSFCell headCell12 = headerRow.createCell(7);
            headCell12.setCellValue("计算包裹净重");
            headCell12.setCellStyle(headerStyle);

            HSSFCell headCell13 = headerRow.createCell(8);
            headCell13.setCellValue("运单号");
            headCell13.setCellStyle(headerStyle);

            HSSFCell headCell14 = headerRow.createCell(9);
            headCell14.setCellValue("提运单号");
            headCell14.setCellStyle(headerStyle);

            HSSFCell headCell15 = headerRow.createCell(10);
            headCell15.setCellValue("航次");
            headCell15.setCellStyle(headerStyle);

            HSSFCell headCell16 = headerRow.createCell(11);
            headCell16.setCellValue("预计到达时间");
            headCell16.setCellStyle(headerStyle);

            HSSFCell headCell17 = headerRow.createCell(12);
            headCell17.setCellValue("重量");
            headCell17.setCellStyle(headerStyle);

            HSSFCell headCell19 = headerRow.createCell(13);
            headCell19.setCellValue("商品jan code");
            headCell19.setCellStyle(headerStyle);

            HSSFCell headCell20 = headerRow.createCell(14);
            headCell20.setCellValue("集栈订单号编号");
            headCell20.setCellStyle(headerStyle);

            HSSFCell headCell21 = headerRow.createCell(15);
            headCell21.setCellValue("海关商品分类编号");
            headCell21.setCellStyle(headerStyle);

            HSSFCell headCell22 = headerRow.createCell(16);
            headCell22.setCellValue("海关税则号列");
            headCell22.setCellStyle(headerStyle);

            HSSFCell headCell23 = headerRow.createCell(17);
            headCell23.setCellValue("商品简写识别码");
            headCell23.setCellStyle(headerStyle);

            HSSFCell headCell24 = headerRow.createCell(18);
            headCell24.setCellValue("商品报关售价总和");
            headCell24.setCellStyle(headerStyle);

            HSSFCell headCell25 = headerRow.createCell(19);
            headCell25.setCellValue("商品报关税金总和");
            headCell25.setCellStyle(headerStyle);

            HSSFCell headCell26 = headerRow.createCell(20);
            headCell26.setCellValue("商品打包重量总和");
            headCell26.setCellStyle(headerStyle);

            HSSFCell headCell27 = headerRow.createCell(21);
            headCell27.setCellValue("商品供货价总和");
            headCell27.setCellStyle(headerStyle);

            HSSFCell headCell28 = headerRow.createCell(22);
            headCell28.setCellValue("商品关税总和");
            headCell28.setCellStyle(headerStyle);

            HSSFCell headCell29 = headerRow.createCell(23);
            headCell29.setCellValue("订单实收包裹重量");
            headCell29.setCellStyle(headerStyle);

            HSSFCell headCell30 = headerRow.createCell(24);
            headCell30.setCellValue("订单实收运费");
            headCell30.setCellStyle(headerStyle);

            HSSFCell headCell32 = headerRow.createCell(25);
            headCell32.setCellValue("买手名字");
            headCell32.setCellStyle(headerStyle);

            HSSFCell headCell33 = headerRow.createCell(26);
            headCell33.setCellValue("买手手机号码");
            headCell33.setCellStyle(headerStyle);

            HSSFCell headCell34 = headerRow.createCell(27);
            headCell34.setCellValue("买手邀请码");
            headCell34.setCellStyle(headerStyle);

            

            for (int i = 0; i < orderForRepositoryList.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                OrderForRepository orderForRepository = orderForRepositoryList.get(i);

                row.createCell(0).setCellValue(orderForRepository.getOrderTime());
                row.createCell(1).setCellValue(orderForRepository.getOrderStatus());

                row.createCell(2).setCellValue(orderForRepository.getRecvName());

                row.createCell(3).setCellValue(orderForRepository.getRecvMobile());
                row.createCell(4).setCellValue(orderForRepository.getRecvAddr());
                row.createCell(5).setCellValue(orderForRepository.getProvince());
                row.createCell(6).setCellValue(orderForRepository.getCity());
                row.createCell(7).setCellValue(orderForRepository.getJWeight());
                row.createCell(8).setCellValue(orderForRepository.getExpressNumber());
                row.createCell(9).setCellValue("");
                row.createCell(10).setCellValue("");
                row.createCell(11).setCellValue("");
                row.createCell(12).setCellValue(orderForRepository.getNetWeight());
                row.createCell(13).setCellValue(orderForRepository.getProductJancode());
                row.createCell(14).setCellValue(orderForRepository.getOrderId());
                row.createCell(15).setCellValue("");
                row.createCell(16).setCellValue("");
                row.createCell(17).setCellValue(orderForRepository.getProductSimpleCode());

                row.createCell(18).setCellValue(orderForRepository.getTotalBcPrice());
                row.createCell(19).setCellValue(orderForRepository.getTotalTaxes());
                row.createCell(20).setCellValue(orderForRepository.getTotalPackWeight());
                row.createCell(21).setCellValue(orderForRepository.getTotalGPrice());

                row.createCell(22).setCellValue(orderForRepository.getTotalDuties());
                row.createCell(23).setCellValue(orderForRepository.getActualPackWeight());
                row.createCell(24).setCellValue(orderForRepository.getActualFreight());
                row.createCell(25).setCellValue(orderForRepository.getBuyerName());
                row.createCell(26).setCellValue(orderForRepository.getBuyerMobile());
                row.createCell(27).setCellValue(orderForRepository.getInviteCode());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            String dateStr = sdf.format(new Date());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startTimeStr = simpleDateFormat.format(startTime);
            String endTimeStr = simpleDateFormat.format(endTime);

            String fileName = "订单导出（仓库）" + "[" + startTimeStr + "-" + endTimeStr + "]" + dateStr + ".xls";

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
}
