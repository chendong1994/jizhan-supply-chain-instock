package com.jizhangyl.application.controller;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.primary.Flight;
import com.jizhangyl.application.dataobject.primary.FlightPackag;
import com.jizhangyl.application.dto.FlightDto;
import com.jizhangyl.application.enums.PackageStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.CustomsclearanceForm;
import com.jizhangyl.application.form.FlightArrivalForm;
import com.jizhangyl.application.form.FlightChargeForm;
import com.jizhangyl.application.form.FlightReservationForm;
import com.jizhangyl.application.form.PackPackageForm;
import com.jizhangyl.application.form.PackageToAirportForm;
import com.jizhangyl.application.service.FlightService;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.FileUploadUtil;
import com.jizhangyl.application.utils.ResultVOUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 包裹航班流程
 * @author 陈栋
 * @date 2018年9月26日  
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/flight")
public class FlightController {
	
	@Autowired
    private FlightService flightService;
	@Autowired
    private FileUploadUtil fileUploadUtil;
	
	
	
	/**
	 * 图片上传到阿里云云oss服务器，返回外网访问路径
	 * @param file
	 * @return
	 */
	@PostMapping("/imageUpload")
	public ResultVO imageUpload(@RequestParam("image") MultipartFile file){
		String outsideUrl = fileUploadUtil.upload(file);
		return ResultVOUtil.success(outsideUrl);
	}
	
    /**
     * 航班预定/修改
     * @param modle
     * @param bindingResult
     * @return
     */
	@PostMapping("/flightReservation")
	public ResultVO flightReservation(@Valid FlightReservationForm modle ,BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm = ", modle);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
		Flight fl = new  Flight();
		fl.setFlightId(modle.getFlightId());
		fl.setDeliveryNumber(modle.getDeliveryNumber());
		fl.setVoyage(modle.getVoyage());
//		Date flightGoTime = DateUtil.StringToDate(modle.getFlightGoTime());
		Date flightArriveTime = DateUtil.StringToDate(modle.getFlightArriveTime());
		if( flightArriveTime == null){
			 // TODO 时间格式错误
			 new GlobalException(ResultEnum.PARAM_EMPTY); 
		}
		fl.setFlightArriveTime(flightArriveTime);
		fl.setPackageStatus(PackageStatusEnum.FLIGHT_RESERVATION.getCode());//航班预定
		flightService.addFlight(fl,null);
		return ResultVOUtil.success();
	}
	
	/**
	 * 包裹打包/修改
	 * @param modle
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/packPackage")
	public ResultVO packPackage(@Valid PackPackageForm modle ,BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm = ", modle);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
		List<String>  list = flightService.addFlightPackag(modle.getExpressNums(),modle.getFlightId(), PackageStatusEnum.PACK_PACKAGE.getCode());
		return ResultVOUtil.success(list);
	}
	
	
	/**
	 * 包裹送机
	 * @param modle
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/packageToAirport")
	public ResultVO packageToAirport(@Valid PackageToAirportForm modle ,BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm = ", modle);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
		Flight fl = new  Flight();
		BeanUtils.copyProperties(modle, fl);
		fl.setPackageStatus(PackageStatusEnum.PACKAGE_TO_AIRPORT.getCode());
		flightService.addFlight(fl,modle.getDeliveryFlightUrl());
		return ResultVOUtil.success();
	}
	
	
	/**
	 * 收航空费
	 * @param modle
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/flightCharge")
	public ResultVO flightCharge(@Valid FlightChargeForm modle ,BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm = ", modle);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
		Flight fl = new  Flight();
		BeanUtils.copyProperties(modle, fl);
		fl.setPackageStatus(PackageStatusEnum.FLIGHT_CHARGE.getCode());
//		String url = modle.getFlightUrl().trim();
//		String[] urllist = url.split(",");
		flightService.addFlight(fl,modle.getFlightUrl());
		return ResultVOUtil.success();
	}
	
	/**
	 * 航班到港
	 * @param modle
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/flightArrival")
	public ResultVO flightArrival(@Valid FlightArrivalForm modle ,BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm = ", modle);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
		Flight fl = new  Flight();
		fl.setFlightId(modle.getFlightId());
		fl.setPackageStatus(PackageStatusEnum.FLIGHT_ARRIVAL.getCode());
		flightService.addFlight(fl,null);
		return ResultVOUtil.success();
	}
	
	/**
	 * 清关完毕
	 * @param modle
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/customsclearance")
	public ResultVO customsclearance(@Valid CustomsclearanceForm modle ,BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm = ", modle);
            throw new GlobalException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
		Flight fl = new  Flight();
		BeanUtils.copyProperties(modle, fl);
		fl.setPackageStatus(PackageStatusEnum.CUSTOMS_CLEARANCE.getCode());
//		String url = modle.getCustomsUrl().trim();
//		String[] urllist = url.split(",");
		flightService.addFlight(fl,modle.getCustomsUrl());
		return ResultVOUtil.success();
	}
	
	
    //===========================================后台包裹航班流程===========================================
	
	
	/**
	 * 后台列表
	 * @param page
	 * @param size
	 * @param status
	 * @return
	 */
	@GetMapping("/list")
	public ResultVO findAllFlightPackag(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
			                            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size
			                            ){
		
		PageRequest pageRequest = new PageRequest(page - 1, size);
		Page<FlightDto> pg = flightService.findAll( pageRequest);
		Map<String, Object> result = new HashMap<>();
        result.put("list1", pg.getContent());
        result.put("totalpage1", pg.getTotalPages());
        
		return ResultVOUtil.success(result);
	}
	
	
	
	
	@GetMapping("/exportForPackage")
	public ResponseEntity<byte[]> findAllFlightPackag(@RequestParam(value = "flightId") Integer flightId){
		List<FlightPackag> list = flightService.findAllFlightPackag(flightId);
		
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
            si.setSubject("物流单号");
            si.setTitle("物流单号");
            si.setAuthor("杭州集栈网络科技有限公司");
            si.setComments("技术支持：杭州集栈网络科技有限公司");

            HSSFSheet sheet = workbook.createSheet("物流单号列表");

            // 4. 创建日期显示格式 (备用)
            HSSFCellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
            // 5. 创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            sheet.setColumnWidth(0, 60 * 256);

            HSSFRow headerRow = sheet.createRow(0);

            HSSFCell headCell0 = headerRow.createCell(0);
            headCell0.setCellValue("订单编号");
            headCell0.setCellStyle(headerStyle);

            for (int i = 0; i < list.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                FlightPackag flightPackag = list.get(i);

                row.createCell(0).setCellValue(flightPackag.getExpNum());

            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH点mm分ss秒");
            String dateStr = sdf.format(new Date());

            String fileName = "包裹物流单号" + dateStr + ".xls";

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
	
    
	
	
	@GetMapping("/details")
	public ResultVO details(@RequestParam(value = "flightId") Integer flightId){
		if(flightId == null){
			throw new GlobalException(ResultEnum.PARAM_EMPTY);
		}
		Map<String,Object> map = flightService.findById(flightId);
		
		return ResultVOUtil.success(map);
	}
	
    
	

}
