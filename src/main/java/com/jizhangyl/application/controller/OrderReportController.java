package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.OrderExportVO;
import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.constant.CookieConstant;
import com.jizhangyl.application.constant.RedisConstant;
import com.jizhangyl.application.converter.OrderMaster2OrderExportConverter;
import com.jizhangyl.application.converter.OrderMaster2OrderForRepositoryConverter;
import com.jizhangyl.application.dataobject.primary.BuyerInfo;
import com.jizhangyl.application.dataobject.primary.ExpressNum;
import com.jizhangyl.application.dataobject.primary.OrderExport;
import com.jizhangyl.application.dataobject.primary.OrderForRepository;
import com.jizhangyl.application.dataobject.primary.OrderImportDetail;
import com.jizhangyl.application.dataobject.primary.OrderImportMaster;
import com.jizhangyl.application.dataobject.primary.OrderMaster;
import com.jizhangyl.application.dataobject.secondary.Wxuser;
import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.enums.OrderStatusEnum;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.service.BuyerService;
import com.jizhangyl.application.service.OrderMasterService;
import com.jizhangyl.application.service.RepositoryDeliveryService;
import com.jizhangyl.application.service.WxuserAddrService;
import com.jizhangyl.application.service.WxuserService;
import com.jizhangyl.application.service.impl.OrderImportServiceImpl;
import com.jizhangyl.application.utils.CookieUtil;
import com.jizhangyl.application.utils.DateUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import com.jizhangyl.application.utils.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/14 11:33
 * @description
 */
@Slf4j
@Controller
@RequestMapping("/order/report")
public class OrderReportController {

    @Autowired
    private OrderImportServiceImpl orderImportService;

    @Autowired
    private OrderMasterService orderService;

    @Autowired
    private WxuserAddrService wxuserAddrService;

    @Autowired
    private OrderMaster2OrderExportConverter orderMaster2OrderExportConverter;

    @Autowired
    private OrderMaster2OrderForRepositoryConverter orderMaster2OrderForRepositoryConverter;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RepositoryDeliveryService repositoryDeliveryService;

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private WxuserService wxuserService;

    @ResponseBody
    @PostMapping("/importFromBusiness")
    public ResultVO importFromBusiness(@RequestParam("myfile") MultipartFile file, @RequestParam("inviteCode") String inviteCode) {
        if (file.isEmpty() || StringUtils.isEmpty(inviteCode)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        Wxuser wxuser = wxuserService.findByInviteCode(inviteCode);
        if (wxuser == null) {
            log.error("【商家订单导入】邀请码 {} 不存在", inviteCode);
            throw new GlobalException(ResultEnum.INVITE_CODE_NOT_EXIST);
        }

        orderImportService.importOrder2List(file, wxuser.getOpenId());

        return ResultVOUtil.success();
    }

    @GetMapping("/exportForBusiness")
    public ResponseEntity<byte[]> exportForBusiness(@RequestParam(value = "date") String dateStr,
                                                    @RequestParam("inviteCode") String inviteCode) {

        if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(inviteCode)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(org.apache.commons.lang3.StringUtils.trimToNull(dateStr));

        } catch (Exception e) {
            throw new GlobalException(ResultEnum.DATE_FORMAT_ERROR);
        }

        try {

            Wxuser wxuser = wxuserService.findByInviteCode(inviteCode);
            if (wxuser == null) {
                log.error("【买手订单导出】邀请码 {} 不存在", inviteCode);
                throw new GlobalException(ResultEnum.INVITE_CODE_NOT_EXIST);
            }

            String openid = wxuser.getOpenId();

            OrderImportMaster orderImportMaster = orderImportService.findByBuyerOpenidAndImportDate(openid, date);

            List<OrderImportDetail> orderImportDetailList = orderImportService.findByMasterId(orderImportMaster.getId());

            List<String> orderMasterIdList = orderImportDetailList.stream().map(e -> e.getOrderMasterId()).collect(Collectors.toList());

            List<OrderMaster> orderMasterList = orderService.findByOrderIdIn(orderMasterIdList);

            List<Integer> recvAddrIdList = orderMasterList.stream().map(e -> e.getRecipientAddrId()).collect(Collectors.toList());

            List<WxuserAddr> wxuserAddrList = wxuserAddrService.findByIdIn(recvAddrIdList);

            List<OrderExport> orderExportList = orderMaster2OrderExportConverter.convert(orderMasterList, wxuserAddrList);

            log.info("orderExportList = {}", orderExportList);

            return orderImportService.exportOrder2Excel(orderExportList, orderImportMaster.getFileName());

        } catch (Exception e) {
            throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
        }
    }

    @ResponseBody
    @GetMapping("/findImportList")
    public ResultVO findImportList(@RequestParam("inviteCode") String inviteCode) {
        if (StringUtils.isEmpty(inviteCode)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        Wxuser wxuser = wxuserService.findByInviteCode(inviteCode);
        if (wxuser == null) {
            log.error("【获取导入列表】邀请码 {} 不存在", inviteCode);
            throw new GlobalException(ResultEnum.INVITE_CODE_NOT_EXIST);
        }

        String openid = wxuser.getOpenId();

        if (!StringUtils.isEmpty(openid)) {
            List<OrderImportMaster> orderImportMasterList = orderImportService.findByBuyerOpenid(openid);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<OrderExportVO> dateList = orderImportMasterList.stream()
                    .map(e -> {
                        OrderExportVO orderExportVO = new OrderExportVO();
                        Date date = e.getImportDate();
                        String formatDate = sdf.format(date);
                        orderExportVO.setImportDate(formatDate);
                        orderExportVO.setFileName(e.getFileName());
                        return orderExportVO;
                    })
                    .collect(Collectors.toList());

            return ResultVOUtil.success(dateList);
        }
        return ResultVOUtil.success();
    }


    /**
     * 导出至仓库
     * （仓库打包导出订单）
     * @param startTimeStr
     * @param endTimeStr
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/exportForRepository")
    public ResponseEntity<byte[]> exportForRepository(@RequestParam(value = "startTime") String startTimeStr,
                                                   @RequestParam(value = "endTime") String endTimeStr,
                                                   HttpServletRequest request) {
        if (StringUtils.isEmpty(startTimeStr) || StringUtils.isEmpty(endTimeStr)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }

        String openid = null;
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie != null && !StringUtils.isEmpty(cookie.getValue())) {
            openid = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startTime = sdf.parse(org.apache.commons.lang3.StringUtils.trimToNull(startTimeStr));
            Date endTime = sdf.parse(org.apache.commons.lang3.StringUtils.trimToNull(endTimeStr));

            List<OrderMaster> orderMasterList = orderService.findByCreateTimeBetween(startTime, DateUtil.add(endTime, 1));

            orderMasterList = orderMasterList.stream().filter(e -> {
                if (e.getOrderStatus().equals(OrderStatusEnum.PAID.getCode())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            List<Integer> recvAddrIdList = orderMasterList.stream().map(e -> e.getRecipientAddrId()).collect(Collectors.toList());

            List<WxuserAddr> wxuserAddrList = wxuserAddrService.findByIdIn(recvAddrIdList);

            BuyerInfo buyerInfo = buyerService.findBuyerInfo(orderMasterList);

            List<OrderForRepository> orderForRepositoryList = orderMaster2OrderForRepositoryConverter.convert(orderMasterList, wxuserAddrList, buyerInfo);

            log.info("orderForRepositoryList = {}", orderForRepositoryList);

            return orderImportService.exportOrder2RepositoryExcel(orderForRepositoryList, startTime, endTime);

        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResultEnum.EXCEL_EXPORT_ERROR);
        }
    }

    /**
     * 仓库导入需要发货的单号完成发货操作
     * @param file
     * @return
     */
    @PostMapping("/importFromRepository")
    @ResponseBody
    public ResultVO importFromRepository(@RequestParam("orderExcel") MultipartFile file) {
        try {

            InputStream is = file.getInputStream();
            ExcelUtil<ExpressNum> excelUtil = new ExcelUtil<>(ExpressNum.class);
            List<ExpressNum> expressNumList = excelUtil.importExcel("仓库发货", is);

            log.info("expressNumList = {}", expressNumList);

            repositoryDeliveryService.delivery(expressNumList);

        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                throw new GlobalException(ResultEnum.FILE_EMPTY);
            } else if (e instanceof IOException) {
                throw new GlobalException(ResultEnum.IO_ERROR);
            } else {
                throw new GlobalException(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
            }
        }
        return ResultVOUtil.success();
    }


    public static void main(String[] args) {
        ExpressNum e1 = new ExpressNum();
        e1.setId(1);
        e1.setStatus(0);
        e1.setExpNum("111111");

        ExpressNum e2 = new ExpressNum();
        e2.setId(2);
        e2.setStatus(0);
        e2.setExpNum("222222");

        ExpressNum e3 = new ExpressNum();
        e3.setId(3);
        e3.setStatus(0);
        e3.setExpNum("333333");

        ExpressNum e4 = new ExpressNum();
        e4.setId(4);
        e4.setStatus(0);
        e4.setExpNum("444444");

        ExpressNum e5 = new ExpressNum();
        e5.setId(5);
        e5.setStatus(0);
        e5.setExpNum("555555");

        List<ExpressNum> expressNumList = new ArrayList<>();
        expressNumList.add(e1);
        expressNumList.add(e2);
        expressNumList.add(e3);
        expressNumList.add(e4);
        expressNumList.add(e5);

        System.out.println(expressNumList);

        expressNumList = expressNumList.stream().filter(e -> {
            if (e.getExpNum().equals("333333")) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        System.out.println(expressNumList);
    }
}