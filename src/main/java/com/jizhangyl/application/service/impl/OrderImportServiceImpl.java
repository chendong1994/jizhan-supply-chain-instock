package com.jizhangyl.application.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jizhangyl.application.VO.ShopExportVo;
import com.jizhangyl.application.dataobject.primary.OrderDetail;
import com.jizhangyl.application.dataobject.primary.OrderExport;
import com.jizhangyl.application.dataobject.primary.OrderForCustoms;
import com.jizhangyl.application.dataobject.primary.OrderForRepository;
import com.jizhangyl.application.dataobject.primary.OrderImportDetail;
import com.jizhangyl.application.dataobject.primary.OrderImportMaster;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.primary.Shop;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.dataobject.primary.WxuserSender;
import com.jizhangyl.application.dto.OrderDto;
import com.jizhangyl.application.dto.OrderImportDto;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.PayStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.repository.primary.OrderImportDetailRepository;
import com.jizhangyl.application.repository.primary.OrderImportMasterRepository;
import com.jizhangyl.application.service.AddressResolveService;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.ShopService;
import com.jizhangyl.application.service.WxuserAddrService;
import com.jizhangyl.application.service.WxuserSenderService;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.JsonUtil;
import com.jizhangyl.application.utils.KeyUtil;
import com.jizhangyl.application.utils.OrderExcelFieldCheckUtil;
import com.jizhangyl.application.utils.RegexUtil;
import com.jizhangyl.application.utils.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/13 19:42
 * @description 用户订单导入
 */
@Slf4j
@Service
public class OrderImportServiceImpl {

    @Autowired
    private OrderMasterService orderMasterService;

    @Autowired
    private AddressResolveService resolveService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private WxuserAddrService wxuserAddrService;

    @Autowired
    private WxuserSenderService wxuserSenderService;

    @Autowired
    private OrderImportMasterRepository orderImportMasterRepository;

    @Autowired
    private OrderImportDetailRepository orderImportDetailRepository;


    /**
     * 订单数据解析成 List<OrderDto>
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<OrderDto> importOrder2List(MultipartFile file, String openid) {
        List<OrderDto> orderDtoList = new ArrayList<>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new POIFSFileSystem(file.getInputStream()));
            int numberOfSheets = workbook.getNumberOfSheets();
            // 遍历表格所有的 sheet, 我们一般仅适用 sheet1
//            for (int i = 0; i < numberOfSheets; i++) {
            for (int i = 0; i < 1; i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                // 遍历所有行
                for (int j = 0; j < physicalNumberOfRows; j++) {
                    // 标题行
                    if (j == 0) {
                        continue;
                    }
                    // 从第二行开始，每行封装成一个 orderDto
                    OrderDto orderDto = new OrderDto();

                    // 取出来具体的一行
                    HSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    // 一行的实际列数
                    // int physicalNumberOfCells = row.getPhysicalNumberOfCells();
                    int physicalNumberOfCells = row.getLastCellNum();

                    // 一行数据封装成一个 orderDto 对象
                    orderDto = new OrderDto();
                    List<OrderDetail> orderDetailList = new ArrayList<>();

                    // 用于接收2,3列的收件人姓名和地址
                    WxuserAddr wxuserAddr = null;
                    // 用来接收orderDetail对象
                    OrderDetail orderDetail = null;

                    // 遍历每行的所有列
                    for (int k = 0; k < physicalNumberOfCells; k++) {

                        // 每行的某一列的内容
                        HSSFCell cell = row.getCell(k);
                        if (cell == null) {
                            continue;
                        }
                        switch (cell.getCellTypeEnum()) {
                            case STRING: {
                                String cellValue = StringUtils.trimToNull(cell.getStringCellValue()).replace("'", "");
                                Shop shop = null;
                                switch (k) {
                                    // 设置买家指定的订单编号到预留字段1
                                    case 0:
                                        if (StringUtils.isEmpty(cellValue)) {
                                            throw new GlobalException(ResultEnum.BUYER_ORDER_ID_EMPTY);
                                        } else {
                                            String tbOrderid = replaceStr(cellValue);
                                            orderDto.setBuyerOrderId(tbOrderid);
                                        }
                                        break;
                                    case 1:
                                        if (!StringUtils.isEmpty(cellValue)) {
                                            orderDto.setCustomerName(cellValue);
                                        }
                                        break;
                                    case 2:
                                        if (!StringUtils.isEmpty(cellValue)) {

                                            if (!RegexUtil.isZhName(cellValue)) {
                                                throw new GlobalException(ResultEnum.RECEIVER_NAME_ERROR.getCode(),
                                                        String.format(ResultEnum.RECEIVER_NAME_ERROR.getMessage(), j));
                                            }

                                            wxuserAddr = new WxuserAddr();
                                            wxuserAddr.setReceiver(cellValue);
                                        }
                                        break;
                                    case 3:
                                        if (!StringUtils.isEmpty(cellValue)) {
                                            JSONObject json = resolveService.resolve(cellValue);
                                            List<Map<String, String>> list = (List<Map<String, String>>) json.get("data");
                                            Map<String, String> map = list.get(0);
                                            String provinceName = map.get("province_name");
                                            String cityName = map.get("city_name");
                                            String countyName = map.get("county_name");
                                            String detail = map.get("detail");
                                            String name = map.get("name");
                                            wxuserAddr.setProvince(provinceName);
                                            wxuserAddr.setCity(cityName);
                                            wxuserAddr.setArea(countyName);
                                            wxuserAddr.setDetailAddr(detail + " " + name);
                                        }
                                        break;
                                    case 4:
                                        if (!StringUtils.isEmpty(cellValue)) {
                                            if (!ValidatorUtil.isMobile(cellValue)) {
                                                throw new GlobalException(ResultEnum.PHONE_FORMAT_ERROR.getCode(),
                                                        String.format(ResultEnum.PHONE_FORMAT_ERROR.getMessage(), j));
                                            }
                                            wxuserAddr.setPhone(replaceStr(cellValue));
                                            WxuserAddr result = wxuserAddrService.save(wxuserAddr);
                                            orderDto.setRecipientAddrId(result.getId());
                                            wxuserAddr = null;
                                        }
                                        break;
                                    // 商品 1-10 的 orderDetail
                                    case 5:
                                    case 7:
                                    case 9:
                                    case 11:
                                    case 13:
                                    case 15:
                                    case 17:
                                    case 19:
                                    case 21:
                                    case 23:
                                        if (!StringUtils.isEmpty(cellValue)) {
                                            shop = shopService.findByShopJan(replaceStr(cellValue));
                                            if (shop == null) {
                                                log.error("【订单导入】解析出错, 商品不存在, Jancode = {}", cellValue);
                                                throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(), "商品不存在, Jancode = " + cellValue));
                                            }
                                            orderDetail = new OrderDetail();
                                            orderDetail.setProductId(String.valueOf(shop.getId()));
                                        }
                                        break;
                                }
                            }
                            break;

                            case NUMERIC: {
                                // 考虑传递过来的参数为 Numeric 类型并获取其参数值
                                double cellValue = cell.getNumericCellValue();
                                switch (k) {
                                    case 6:
                                    case 8:
                                    case 10:
                                    case 12:
                                    case 14:
                                    case 16:
                                    case 18:
                                    case 20:
                                    case 22:
                                    case 24:
                                        Integer quantity = OrderExcelFieldCheckUtil.fieldToInt(cellValue);
                                        if (!OrderExcelFieldCheckUtil.checkValueRound(quantity)) {
                                            throw new GlobalException(ResultEnum.NUM_FIELD_FORMAT_ERROR);
                                        } else {
                                            orderDetail.setProductQuantity(quantity);
                                            orderDetailList.add(orderDetail);
                                        }
                                        break;
                                }
                            }
                            break;

                            case BLANK: {

                            }
                            break;

                            default: {

                            }
                            break;
                        }
                    }

                    if (!CollectionUtils.isEmpty(orderDetailList)) {
                        orderDto.setOrderDetailList(orderDetailList);
                        orderDto.setPayStatus(PayStatusEnum.WAIT.getCode());
                        orderDto.setOrderStatus(OrderStatusEnum.NEW.getCode());

                        orderDtoList.add(orderDto);
                    }
                }
            }
        } catch (Exception e) {
            log.error("【Excel导入】订单导入异常: {}, 请检查Excel字段格式后重试.", e.getMessage());
            e.printStackTrace();
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), String.format(ResultEnum.PARAM_ERROR.getMessage(), e.getMessage()));
        }

        List<String> outerOrderIdList = orderDtoList.stream().map(e -> e.getBuyerOrderId()).collect(Collectors.toList());

        List<OrderMaster> orderMasterList = orderMasterService.findByBuyerOrderIdIn(outerOrderIdList);
        // 判断是否重复操作
        if (!CollectionUtils.isEmpty(orderMasterList)) {
            for (OrderMaster orderMaster : orderMasterList) {
                if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.CANCELED.getCode())) {
                    throw new GlobalException(ResultEnum.BUYER_ORDER_EXISTED.getCode(),
                            String.format(ResultEnum.BUYER_ORDER_EXISTED.getMessage(), orderMasterList.get(0).getBuyerOrderId()));
                }
            }
        }

        order2Db(orderDtoList, openid, file.getOriginalFilename());

        return orderDtoList;
    }


    /**
     * 计算总价，包括运费，货值，关税等
     * @param orderDto
     * @return
     */
    private static BigDecimal computePrice(OrderDto orderDto) {
        return null;
    }

    /**
     * 所有订单入库
     * @param orderDtoList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void order2Db(List<OrderDto> orderDtoList, String openid, String fileName) {

        log.info("【订单导入】orderDtoList = {}", orderDtoList);

        // 记录导入数据到 order_import_master 表
        OrderImportDto orderImportDto = new OrderImportDto();
        orderImportDto.setBuyerOpenid(openid);
        orderImportDto.setImportDate(new Date());
        orderImportDto.setFileName(fileName);

        // 导入订单的 detail 记录列表
        List<OrderImportDetail> orderImportDetailList = new ArrayList<>();

        WxuserSender wxuserSender = wxuserSenderService.findDefault(openid);
        if (wxuserSender == null) {
            throw new GlobalException(ResultEnum.SENDER_ADDR_NOT_EXIST);
        }
        orderDtoList = orderDtoList.stream().filter(e -> {
            e.setBuyerOpenid(openid);
            e.setBuyerAddrId(wxuserSender.getId());
            log.info("【订单导入】e = {}", JsonUtil.toJson(e));
            return true;
        }).collect(Collectors.toList());

        for (OrderDto orderDto : orderDtoList) {
            if (!CollectionUtils.isEmpty(orderDto.getOrderDetailList())) {
                OrderDto resultDto = orderMasterService.create(orderDto, true);

                OrderImportDetail orderImportDetail = new OrderImportDetail();
                orderImportDetail.setOrderMasterId(resultDto.getOrderId());
                orderImportDetailList.add(orderImportDetail);

            }
        }

        orderImportDto.setOrderImportDetailList(orderImportDetailList);

        // 所有导入记录的 淘宝订单号信息入库
        this.save(orderImportDto);
    }

    private String replaceStr(String str) {
        if (str.contains("'")) {
            return str.replace("'", "");
        }
        return str;
    }

    /**
     * 保存导入订单的信息
     * @param orderImportDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderImportDto save(OrderImportDto orderImportDto) {
        if (CollectionUtils.isEmpty(orderImportDto.getOrderImportDetailList())) {
            log.error("【Excel导入】导入失败, 原因: 用户单号列数据为空");
            throw new GlobalException(ResultEnum.EXCEL_IMPORT_ERROR);
        }

        // 一个 order_import_master 记录对应三个 order_master. 记录
        // order_import_master 的 id
        String masterId = KeyUtil.genUniqueKey();
        List<OrderImportDetail> orderImportDetailList = orderImportDto.getOrderImportDetailList().stream().filter(e -> {
            e.setId(KeyUtil.genUniqueKey());
            e.setMasterId(masterId);
            return true;
        }).collect(Collectors.toList());

        OrderImportMaster orderImportMaster = new OrderImportMaster();
        BeanUtils.copyProperties(orderImportDto, orderImportMaster);
        orderImportMaster.setId(masterId);

        // 导入详情入库
        List<OrderImportDetail> resultList = orderImportDetailRepository.save(orderImportDetailList);
        OrderImportMaster result = orderImportMasterRepository.save(orderImportMaster);
        if (CollectionUtils.isEmpty(resultList) || result == null) {
            log.error("【Excel导入】异常, orderImportDto = {}", orderImportDto);
            throw new GlobalException(ResultEnum.EXCEL_IMPORT_ERROR);
        }
        return orderImportDto;
    }

    public OrderImportMaster findByBuyerOpenidAndImportDate(String buyerOpenid, Date importDate) {
        return orderImportMasterRepository.findByBuyerOpenidAndImportDate(buyerOpenid, importDate);
    }

    public List<OrderImportDetail> findByMasterId(String masterId) {
        return orderImportDetailRepository.findByMasterId(masterId);
    }



    /**
     * @description 订单导出至 excel (to buyer)
     * @param orderExportList
     * @return
     */
    public ResponseEntity<byte[]> exportOrder2Excel(List<OrderExport> orderExportList, String originFileName) {

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
            si.setSubject("订单列表");
            si.setTitle("订单列表");
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
            sheet.setColumnWidth(20, 60 * 256);
            sheet.setColumnWidth(21, 60 * 256);
            sheet.setColumnWidth(22, 60 * 256);
            sheet.setColumnWidth(23, 60 * 256);
            sheet.setColumnWidth(24, 60 * 256);
            sheet.setColumnWidth(25, 60 * 256);
            sheet.setColumnWidth(26, 60 * 256);
            sheet.setColumnWidth(27, 60 * 256);
            // 物流品牌
            sheet.setColumnWidth(27, 60 * 256);


            HSSFRow headerRow = sheet.createRow(0);

            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("订单编号");
            headCell0.setCellStyle(headerStyle);

            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("买家会员名");
            headCell1.setCellStyle(headerStyle);

            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("收货人姓名");
            headCell2.setCellStyle(headerStyle);

            HSSFCell headCell3 = headerRow.createCell(3);
            headCell3.setCellValue("收货地址");
            headCell3.setCellStyle(headerStyle);

            HSSFCell headCell4 = headerRow.createCell(4);
            headCell4.setCellValue("联系手机");
            headCell4.setCellStyle(headerStyle);


            HSSFCell headCell5 = headerRow.createCell(5);
            headCell5.setCellValue("商品1Jan Code");
            headCell5.setCellStyle(headerStyle);

            HSSFCell headCell6 = headerRow.createCell(6);
            headCell6.setCellValue("商品1数量");
            headCell6.setCellStyle(headerStyle);

            HSSFCell headCell7 = headerRow.createCell(7);
            headCell7.setCellValue("商品2Jan Code");
            headCell7.setCellStyle(headerStyle);

            HSSFCell headCell8 = headerRow.createCell(8);
            headCell8.setCellValue("商品2数量");
            headCell8.setCellStyle(headerStyle);

            HSSFCell headCell9 = headerRow.createCell(9);
            headCell9.setCellValue("商品3Jan Code");
            headCell9.setCellStyle(headerStyle);

            HSSFCell headCell10 = headerRow.createCell(10);
            headCell10.setCellValue("商品3重量");
            headCell10.setCellStyle(headerStyle);

            HSSFCell headCell11 = headerRow.createCell(11);
            headCell11.setCellValue("商品4Jan Code");
            headCell11.setCellStyle(headerStyle);

            HSSFCell headCell12 = headerRow.createCell(12);
            headCell12.setCellValue("商品4数量");
            headCell12.setCellStyle(headerStyle);

            HSSFCell headCell13 = headerRow.createCell(13);
            headCell13.setCellValue("商品5Jan Code");
            headCell13.setCellStyle(headerStyle);

            HSSFCell headCell14 = headerRow.createCell(14);
            headCell14.setCellValue("商品5数量");
            headCell14.setCellStyle(headerStyle);


            HSSFCell headCell15 = headerRow.createCell(15);
            headCell15.setCellValue("商品6Jan Code");
            headCell15.setCellStyle(headerStyle);


            HSSFCell headCell16 = headerRow.createCell(16);
            headCell16.setCellValue("商品6数量");
            headCell16.setCellStyle(headerStyle);


            HSSFCell headCell17 = headerRow.createCell(17);
            headCell17.setCellValue("商品7Jan Code");
            headCell17.setCellStyle(headerStyle);


            HSSFCell headCell18 = headerRow.createCell(18);
            headCell18.setCellValue("商品7数量");
            headCell18.setCellStyle(headerStyle);

            HSSFCell headCell19 = headerRow.createCell(19);
            headCell19.setCellValue("商品8Jan Code");
            headCell19.setCellStyle(headerStyle);

            HSSFCell headCell20 = headerRow.createCell(20);
            headCell20.setCellValue("商品8数量");
            headCell20.setCellStyle(headerStyle);

            HSSFCell headCell21 = headerRow.createCell(21);
            headCell21.setCellValue("商品9Jan Code");
            headCell21.setCellStyle(headerStyle);

            HSSFCell headCell22 = headerRow.createCell(22);
            headCell22.setCellValue("商品9数量");
            headCell22.setCellStyle(headerStyle);

            HSSFCell headCell23 = headerRow.createCell(23);
            headCell23.setCellValue("商品10Jan Code");
            headCell23.setCellStyle(headerStyle);

            HSSFCell headCell24 = headerRow.createCell(24);
            headCell24.setCellValue("物流单号");
            headCell24.setCellStyle(headerStyle);

            HSSFCell headCell25 = headerRow.createCell(25);
            headCell25.setCellValue("集栈订单编号");
            headCell25.setCellStyle(headerStyle);

            HSSFCell headCell26 = headerRow.createCell(26);
            headCell26.setCellValue("订单状态");
            headCell26.setCellStyle(headerStyle);

            HSSFCell headCell27 = headerRow.createCell(27);
            headCell27.setCellValue("订单时间");
            headCell27.setCellStyle(headerStyle);

            HSSFCell headCell28 = headerRow.createCell(28);
            headCell28.setCellValue("快递公司");
            headCell28.setCellStyle(headerStyle);

            for (int i = 0; i < orderExportList.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                OrderExport orderExport = orderExportList.get(i);

                row.createCell(0).setCellValue(orderExport.getTbOrderId());
                row.createCell(1).setCellValue(orderExport.getBuyerName());
                row.createCell(2).setCellValue(orderExport.getRecvName());
                row.createCell(3).setCellValue(orderExport.getRecvAddr());
                row.createCell(4).setCellValue(orderExport.getRecvMobile());


                int startColumn = 5;
                List<ShopExportVo> shopExportVoList = orderExport.getShopExportVoList();
                for (ShopExportVo shopExportVo : shopExportVoList) {
                    row.createCell(startColumn++).setCellValue(shopExportVo.getJancode());
                    row.createCell(startColumn++).setCellValue(shopExportVo.getQuantity());
                }

                row.createCell(24).setCellValue(orderExport.getExpressNumber());
                row.createCell(25).setCellValue(orderExport.getJizhanOrderId());
                row.createCell(26).setCellValue(orderExport.getOrderStatus());
                row.createCell(27).setCellValue(orderExport.getOrderTime());

                String expressNumber = orderExport.getExpressNumber();
                String expressCompany = StringUtils.isEmpty(expressNumber) ? ""
                        : (expressNumber.startsWith("BS") && expressNumber.endsWith("CN") ? "EMS" : "日本邮政");
                row.createCell(28).setCellValue(expressCompany);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            String dateStr = sdf.format(new Date());

            if (!StringUtils.isEmpty(originFileName)) {
                originFileName = originFileName.substring(0, originFileName.lastIndexOf("."));
            }
            String fileName = originFileName + dateStr + ".xls";

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


    public List<OrderImportMaster> findByBuyerOpenid(String buyerOpenid) {
        return orderImportMasterRepository.findByBuyerOpenidOrderByImportDateDesc(buyerOpenid);
    }




    /**
     * @description 商品导出至(海关) excel
     * @param orderForCustomsList
     * @return
     */
    public ResponseEntity<byte[]> exportOrder2CustomsExcel(List<OrderForCustoms> orderForCustomsList,
                                                           Date startTime, Date endTime) {

        HttpHeaders headers = null;
        ByteArrayOutputStream baos = null;

        try {
            // 1. 创建 excel 文档
            HSSFWorkbook workbook = new HSSFWorkbook();

            // 2. 创建文档摘要
            workbook.createInformationProperties();
            // 3. 设置文档的基本信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();

            dsi.setCategory("订单导出（海关备案）");
            dsi.setManager("陈谦");
            dsi.setCompany("杭州集栈网络科技有限公司");

            SummaryInformation si = workbook.getSummaryInformation();
            si.setSubject("订单导出（海关备案）");
            si.setTitle("订单导出（海关备案）");
            si.setAuthor("杭州集栈网络科技有限公司");
            si.setComments("技术支持：杭州集栈网络科技有限公司");

            HSSFSheet sheet = workbook.createSheet("订单导出（海关备案）");

            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            sheet.setColumnWidth(0, 30 * 256);
            sheet.setColumnWidth(1, 12 * 256);
            sheet.setColumnWidth(2, 30 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 60 * 256);
            sheet.setColumnWidth(5, 12 * 256);
            sheet.setColumnWidth(6, 12 * 256);

            sheet.setColumnWidth(7, 15 * 256);
            sheet.setColumnWidth(8, 15 * 256);
            sheet.setColumnWidth(9, 15 * 256);
            sheet.setColumnWidth(10, 15 * 256);
            sheet.setColumnWidth(11, 10 * 256);
//            sheet.setColumnWidth(12, 15 * 256);
            sheet.setColumnWidth(12, 10 * 256);
            sheet.setColumnWidth(13, 10 * 256);
            sheet.setColumnWidth(14, 10 * 256);
            sheet.setColumnWidth(15, 15 * 256);
            sheet.setColumnWidth(16, 15 * 256);
            sheet.setColumnWidth(17, 15 * 256);
            sheet.setColumnWidth(18, 15 * 256);
            sheet.setColumnWidth(19, 15 * 256);
            sheet.setColumnWidth(20, 15 * 256);
            sheet.setColumnWidth(21, 15 * 256);
            sheet.setColumnWidth(22, 15 * 256);
            sheet.setColumnWidth(23, 15 * 256);
            sheet.setColumnWidth(24, 15 * 256);
            sheet.setColumnWidth(25, 15 * 256);
            sheet.setColumnWidth(26, 15 * 256);
            sheet.setColumnWidth(27, 15 * 256);
            sheet.setColumnWidth(28, 15 * 256);
            sheet.setColumnWidth(29, 15 * 256);
            sheet.setColumnWidth(30, 15 * 256);
            sheet.setColumnWidth(31, 15 * 256);
            sheet.setColumnWidth(32, 15 * 256);


            HSSFRow headerRow = sheet.createRow(0);

            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("订单编号");
            headCell0.setCellStyle(headerStyle);

            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("收件人名称");
            headCell1.setCellStyle(headerStyle);

            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("购买人证件号");
            headCell2.setCellStyle(headerStyle);

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

            HSSFCell headCell7 = headerRow.createCell(7);
            headCell7.setCellValue("邮编");
            headCell7.setCellStyle(headerStyle);

            HSSFCell headCell8 = headerRow.createCell(8);
            headCell8.setCellValue("订购人注册号");
            headCell8.setCellStyle(headerStyle);

            HSSFCell headCell9 = headerRow.createCell(9);
            headCell9.setCellValue("商品序号");
            headCell9.setCellStyle(headerStyle);

            HSSFCell headCell10 = headerRow.createCell(10);
            headCell10.setCellValue("SKU(电商平台具体商品编号)");
            headCell10.setCellStyle(headerStyle);

            HSSFCell headCell11 = headerRow.createCell(11);
            headCell11.setCellValue("商品数量");
            headCell11.setCellStyle(headerStyle);

            HSSFCell headCell12 = headerRow.createCell(12);
            headCell12.setCellValue("运单号");
            headCell12.setCellStyle(headerStyle);

            HSSFCell headCell13 = headerRow.createCell(13);
            headCell13.setCellValue("提运单号");
            headCell13.setCellStyle(headerStyle);

            HSSFCell headCell14 = headerRow.createCell(14);
            headCell14.setCellValue("航次");
            headCell14.setCellStyle(headerStyle);


            HSSFCell headCell15 = headerRow.createCell(15);
            headCell15.setCellValue("预计到达时间");
            headCell15.setCellStyle(headerStyle);


            HSSFCell headCell16 = headerRow.createCell(16);
            headCell16.setCellValue("重量");
            headCell16.setCellStyle(headerStyle);


            HSSFCell headCell17 = headerRow.createCell(17);
            headCell17.setCellValue("发货国家");
            headCell17.setCellStyle(headerStyle);


            HSSFCell headCell18 = headerRow.createCell(18);
            headCell18.setCellValue("商品jan code");
            headCell18.setCellStyle(headerStyle);

            HSSFCell headCell19 = headerRow.createCell(19);
            headCell19.setCellValue("海关商品分类编号");
            headCell19.setCellStyle(headerStyle);

            HSSFCell headCell20 = headerRow.createCell(20);
            headCell20.setCellValue("海关税则号列");
            headCell20.setCellStyle(headerStyle);

            HSSFCell headCell21 = headerRow.createCell(21);
            headCell21.setCellValue("商品简写识别码");
            headCell21.setCellStyle(headerStyle);

            HSSFCell headCell22 = headerRow.createCell(22);
            headCell22.setCellValue("商品报关售价总和");
            headCell22.setCellStyle(headerStyle);

            HSSFCell headCell23 = headerRow.createCell(23);
            headCell23.setCellValue("商品报关税金总和");
            headCell23.setCellStyle(headerStyle);

            HSSFCell headCell24 = headerRow.createCell(24);
            headCell24.setCellValue("商品打包重量总和");
            headCell24.setCellStyle(headerStyle);

            HSSFCell headCell25 = headerRow.createCell(25);
            headCell25.setCellValue("商品供货价总和");
            headCell25.setCellStyle(headerStyle);

            HSSFCell headCell26 = headerRow.createCell(26);
            headCell26.setCellValue("商品关税总和");
            headCell26.setCellStyle(headerStyle);

            HSSFCell headCell27 = headerRow.createCell(27);
            headCell27.setCellValue("买手名字");
            headCell27.setCellStyle(headerStyle);

            HSSFCell headCell28 = headerRow.createCell(28);
            headCell28.setCellValue("买手手机号码");
            headCell28.setCellStyle(headerStyle);

            HSSFCell headCell29 = headerRow.createCell(29);
            headCell29.setCellValue("买手邀请码");
            headCell29.setCellStyle(headerStyle);

            HSSFCell headCell30 = headerRow.createCell(30);
            headCell30.setCellValue("订单状态");
            headCell30.setCellStyle(headerStyle);

            HSSFCell headCell31 = headerRow.createCell(31);
            headCell31.setCellValue("订单时间");
            headCell31.setCellStyle(headerStyle);

            HSSFCell headCell32 = headerRow.createCell(32);
            headCell32.setCellValue("快递公司");
            headCell32.setCellStyle(headerStyle);

            for (int i = 0; i < orderForCustomsList.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                OrderForCustoms orderForCustoms = orderForCustomsList.get(i);

                row.createCell(0).setCellValue(orderForCustoms.getOrderId());
                row.createCell(1).setCellValue(orderForCustoms.getRecvName());
                row.createCell(2).setCellValue(orderForCustoms.getBuyerIdNum());
                row.createCell(3).setCellValue(orderForCustoms.getRecvMobile());
                row.createCell(4).setCellValue(orderForCustoms.getRecvAddr());
                row.createCell(5).setCellValue(orderForCustoms.getProvince());
                row.createCell(6).setCellValue(orderForCustoms.getCity());
                row.createCell(7).setCellValue(orderForCustoms.getPostCode());
                row.createCell(8).setCellValue("");
                row.createCell(9).setCellValue("");
                row.createCell(10).setCellValue(orderForCustoms.getCustomsProductId());
                row.createCell(11).setCellValue(orderForCustoms.getProductQuantity());
//                row.createCell(12).setCellValue(orderForCustoms.getJWeight());
                row.createCell(12).setCellValue(orderForCustoms.getExpressNumber());
                if(orderForCustoms.getDeliveryNumber() != null){
                	row.createCell(13).setCellValue(orderForCustoms.getDeliveryNumber());
                }else{
                	row.createCell(13).setCellValue("");
                }
                if(orderForCustoms.getVoyage() != null){
                	row.createCell(14).setCellValue(orderForCustoms.getVoyage());
                }else{
                	row.createCell(14).setCellValue("");
                }
                if(orderForCustoms.getExpectedArrivalTime() != null){
                	row.createCell(15).setCellValue(DateUtil.dateToString(orderForCustoms.getExpectedArrivalTime()));
                }else{
                	row.createCell(15).setCellValue("");
                }
                row.createCell(16).setCellValue(orderForCustoms.getNetWeight());
                row.createCell(17).setCellValue("");
                row.createCell(18).setCellValue(orderForCustoms.getProductJancode());
                row.createCell(19).setCellValue(orderForCustoms.getCustomsCateType());
                row.createCell(20).setCellValue(orderForCustoms.getCustomsTariffLine());
                row.createCell(21).setCellValue(orderForCustoms.getProductSimpleCode());
                row.createCell(22).setCellValue(orderForCustoms.getTotalBcPrice());

                row.createCell(23).setCellValue(orderForCustoms.getTotalTaxes());
                row.createCell(24).setCellValue(orderForCustoms.getTotalPackWeight());
                row.createCell(25).setCellValue(orderForCustoms.getTotalGPrice());
                row.createCell(26).setCellValue(orderForCustoms.getTotalDuties());
                row.createCell(27).setCellValue(orderForCustoms.getBuyerName());
                row.createCell(28).setCellValue(orderForCustoms.getBuyerMobile());
                row.createCell(29).setCellValue(orderForCustoms.getInviteCode());
                row.createCell(30).setCellValue(orderForCustoms.getOrderStatus());
                row.createCell(31).setCellValue(orderForCustoms.getOrderTime());

                String expressNumber = orderForCustoms.getExpressNumber();
                String expressCompany = StringUtils.isEmpty(expressNumber) ? ""
                        : (expressNumber.startsWith("BS") && expressNumber.endsWith("CN") ? "EMS" : "日本邮政");
                row.createCell(32).setCellValue(expressCompany);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            String dateStr = sdf.format(new Date());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startTimeStr = simpleDateFormat.format(startTime);
            String endTimeStr = simpleDateFormat.format(endTime);

            String fileName = "订单导出（海关）" + "[" + startTimeStr + "-" + endTimeStr + "]" + dateStr + ".xls";

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
     * 导出至仓库
     * @param orderForRepositoryList
     * @return
     */
    public ResponseEntity<byte[]> exportOrder2RepositoryExcel(List<OrderForRepository> orderForRepositoryList, Date startTime, Date endTime) {

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

            sheet.setColumnWidth(0, 30 * 256);
            sheet.setColumnWidth(1, 12 * 256);
            sheet.setColumnWidth(2, 30 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 60 * 256);
            sheet.setColumnWidth(5, 12 * 256);
            sheet.setColumnWidth(6, 12 * 256);

            sheet.setColumnWidth(7, 15 * 256);
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
            sheet.setColumnWidth(19, 15 * 256);
            sheet.setColumnWidth(20, 15 * 256);
            sheet.setColumnWidth(21, 15 * 256);
            sheet.setColumnWidth(22, 15 * 256);
            sheet.setColumnWidth(23, 15 * 256);
            sheet.setColumnWidth(24, 15 * 256);
            sheet.setColumnWidth(25, 15 * 256);
            sheet.setColumnWidth(26, 15 * 256);
            sheet.setColumnWidth(27, 15 * 256);

            sheet.setColumnWidth(28, 15 * 256);
            sheet.setColumnWidth(29, 15 * 256);
            sheet.setColumnWidth(30, 15 * 256);
            sheet.setColumnWidth(31, 15 * 256);
            sheet.setColumnWidth(32, 15 * 256);
            sheet.setColumnWidth(33, 15 * 256);
            sheet.setColumnWidth(34, 15 * 256);
            sheet.setColumnWidth(35, 15 * 256);
            sheet.setColumnWidth(36, 15 * 256);


            HSSFRow headerRow = sheet.createRow(0);

            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("订单编号");
            headCell0.setCellStyle(headerStyle);

            HSSFCell headCell1 = headerRow.createCell(1);
            headCell1.setCellValue("收件人名称");
            headCell1.setCellStyle(headerStyle);

            HSSFCell headCell2 = headerRow.createCell(2);
            headCell2.setCellValue("购买人证件号");
            headCell2.setCellStyle(headerStyle);

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

            HSSFCell headCell7 = headerRow.createCell(7);
            headCell7.setCellValue("邮编");
            headCell7.setCellStyle(headerStyle);

            HSSFCell headCell8 = headerRow.createCell(8);
            headCell8.setCellValue("订购人注册号");
            headCell8.setCellStyle(headerStyle);

            HSSFCell headCell9 = headerRow.createCell(9);
            headCell9.setCellValue("商品序号");
            headCell9.setCellStyle(headerStyle);

            HSSFCell headCell10 = headerRow.createCell(10);
            headCell10.setCellValue("SKU(电商平台具体商品编号)");
            headCell10.setCellStyle(headerStyle);

            HSSFCell headCell11 = headerRow.createCell(11);
            headCell11.setCellValue("商品数量");
            headCell11.setCellStyle(headerStyle);

            HSSFCell headCell12 = headerRow.createCell(12);
            headCell12.setCellValue("计算包裹净重");
            headCell12.setCellStyle(headerStyle);

            HSSFCell headCell13 = headerRow.createCell(13);
            headCell13.setCellValue("运单号");
            headCell13.setCellStyle(headerStyle);

            HSSFCell headCell14 = headerRow.createCell(14);
            headCell14.setCellValue("提运单号");
            headCell14.setCellStyle(headerStyle);


            HSSFCell headCell15 = headerRow.createCell(15);
            headCell15.setCellValue("航次");
            headCell15.setCellStyle(headerStyle);


            HSSFCell headCell16 = headerRow.createCell(16);
            headCell16.setCellValue("预计到达时间");
            headCell16.setCellStyle(headerStyle);


            HSSFCell headCell17 = headerRow.createCell(17);
            headCell17.setCellValue("重量");
            headCell17.setCellStyle(headerStyle);


            HSSFCell headCell18 = headerRow.createCell(18);
            headCell18.setCellValue("发货国家");
            headCell18.setCellStyle(headerStyle);

            HSSFCell headCell19 = headerRow.createCell(19);
            headCell19.setCellValue("商品jan code");
            headCell19.setCellStyle(headerStyle);

            HSSFCell headCell20 = headerRow.createCell(20);
            headCell20.setCellValue("集栈订单号编号");
            headCell20.setCellStyle(headerStyle);

            HSSFCell headCell21 = headerRow.createCell(21);
            headCell21.setCellValue("海关商品分类编号");
            headCell21.setCellStyle(headerStyle);

            HSSFCell headCell22 = headerRow.createCell(22);
            headCell22.setCellValue("海关税则号列");
            headCell22.setCellStyle(headerStyle);

            HSSFCell headCell23 = headerRow.createCell(23);
            headCell23.setCellValue("商品简写识别码");
            headCell23.setCellStyle(headerStyle);

            HSSFCell headCell24 = headerRow.createCell(24);
            headCell24.setCellValue("商品报关售价总和");
            headCell24.setCellStyle(headerStyle);

            HSSFCell headCell25 = headerRow.createCell(25);
            headCell25.setCellValue("商品报关税金总和");
            headCell25.setCellStyle(headerStyle);

            HSSFCell headCell26 = headerRow.createCell(26);
            headCell26.setCellValue("商品打包重量总和");
            headCell26.setCellStyle(headerStyle);

            HSSFCell headCell27 = headerRow.createCell(27);
            headCell27.setCellValue("商品供货价总和");
            headCell27.setCellStyle(headerStyle);

            HSSFCell headCell28 = headerRow.createCell(28);
            headCell28.setCellValue("商品关税总和");
            headCell28.setCellStyle(headerStyle);

            HSSFCell headCell29 = headerRow.createCell(29);
            headCell29.setCellValue("订单实收包裹重量");
            headCell29.setCellStyle(headerStyle);

            HSSFCell headCell30 = headerRow.createCell(30);
            headCell30.setCellValue("订单实收运费");
            headCell30.setCellStyle(headerStyle);

            HSSFCell headCell31 = headerRow.createCell(31);
            headCell31.setCellValue("订单状态");
            headCell31.setCellStyle(headerStyle);

            HSSFCell headCell32 = headerRow.createCell(32);
            headCell32.setCellValue("买手名字");
            headCell32.setCellStyle(headerStyle);

            HSSFCell headCell33 = headerRow.createCell(33);
            headCell33.setCellValue("买手手机号码");
            headCell33.setCellStyle(headerStyle);

            HSSFCell headCell34 = headerRow.createCell(34);
            headCell34.setCellValue("买手邀请码");
            headCell34.setCellStyle(headerStyle);

            HSSFCell headCell35 = headerRow.createCell(35);
            headCell35.setCellValue("订单时间");
            headCell35.setCellStyle(headerStyle);

            HSSFCell headCell36 = headerRow.createCell(36);
            headCell36.setCellValue("快递公司");
            headCell36.setCellStyle(headerStyle);

            for (int i = 0; i < orderForRepositoryList.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                OrderForRepository orderForRepository = orderForRepositoryList.get(i);

                row.createCell(0).setCellValue(orderForRepository.getOrderId());
                row.createCell(1).setCellValue(orderForRepository.getRecvName());
                row.createCell(2).setCellValue(orderForRepository.getBuyerIdNum());
                row.createCell(3).setCellValue(orderForRepository.getRecvMobile());
                row.createCell(4).setCellValue(orderForRepository.getRecvAddr());
                row.createCell(5).setCellValue(orderForRepository.getProvince());
                row.createCell(6).setCellValue(orderForRepository.getCity());
                row.createCell(7).setCellValue(orderForRepository.getPostCode());
                row.createCell(8).setCellValue("");
                row.createCell(9).setCellValue("");
                row.createCell(10).setCellValue("");
                row.createCell(11).setCellValue("");
                row.createCell(12).setCellValue(orderForRepository.getJWeight());
                row.createCell(13).setCellValue(orderForRepository.getExpressNumber());
                row.createCell(14).setCellValue("");
                row.createCell(15).setCellValue("");
                row.createCell(16).setCellValue("");
                row.createCell(17).setCellValue(orderForRepository.getNetWeight());
                row.createCell(18).setCellValue("");
                row.createCell(19).setCellValue(orderForRepository.getProductJancode());
                row.createCell(20).setCellValue(orderForRepository.getOrderId());
                row.createCell(21).setCellValue("");
                row.createCell(22).setCellValue("");
                row.createCell(23).setCellValue(orderForRepository.getProductSimpleCode());

                row.createCell(24).setCellValue(orderForRepository.getTotalBcPrice());
                row.createCell(25).setCellValue(orderForRepository.getTotalTaxes());
                row.createCell(26).setCellValue(orderForRepository.getTotalPackWeight());
                row.createCell(27).setCellValue(orderForRepository.getTotalGPrice());

                row.createCell(28).setCellValue(orderForRepository.getTotalDuties());
                row.createCell(29).setCellValue(orderForRepository.getActualPackWeight());
                row.createCell(30).setCellValue(orderForRepository.getActualFreight());
                row.createCell(31).setCellValue(orderForRepository.getOrderStatus());
                row.createCell(32).setCellValue(orderForRepository.getBuyerName());
                row.createCell(33).setCellValue(orderForRepository.getBuyerMobile());
                row.createCell(34).setCellValue(orderForRepository.getInviteCode());
                row.createCell(35).setCellValue(orderForRepository.getOrderTime());

                String expressNumber = orderForRepository.getExpressNumber();
                String expressCompany = StringUtils.isEmpty(expressNumber) ? ""
                        : (expressNumber.startsWith("BS") && expressNumber.endsWith("CN") ? "EMS" : "日本邮政");
                row.createCell(36).setCellValue(expressCompany);
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