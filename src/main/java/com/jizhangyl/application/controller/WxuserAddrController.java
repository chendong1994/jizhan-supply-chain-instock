package com.jizhangyl.application.controller;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.dataobject.WxuserAddr;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.WxuserAddrForm;
import com.jizhangyl.application.form.WxuserAddrUpdateForm;
import com.jizhangyl.application.service.WxuserAddrService;
import com.jizhangyl.application.utils.PoiUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/5 11:26
 * @description
 */
@Controller
@RequestMapping("/addr")
public class WxuserAddrController {

    @Autowired
    private WxuserAddrService wxuserAddrService;

    @GetMapping("/exportAddr")
    public ResponseEntity<byte[]> export() {
        return PoiUtil.exportWxUserAddr2Excel(wxuserAddrService.findAll());
    }

    @ResponseBody
    @PostMapping("/importAddr")
    public ResultVO importAddr(@RequestParam("myfile") MultipartFile file) {
        List<WxuserAddr> addrList = PoiUtil.importWxUserAddr2List(file);
        if (wxuserAddrService.saveBatch(addrList) == addrList.size()) {
            return ResultVOUtil.success(ResultEnum.EXCEL_IMPORT_SUCCESS.getMessage());
        }
        return ResultVOUtil.error(ResultEnum.EXCEL_IMPORT_ERROR);
    }

    /**
     * 查找用户的所有收件人地址（去除分页）
     * @param openid
     * @return
     */
    @ResponseBody
    @GetMapping("/findAllByOpenid")
    public ResultVO findAllByOpenid(@RequestParam("openid") String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        List<WxuserAddr> wxuserAddrList = wxuserAddrService.findAllByOpenid(openid);
        return ResultVOUtil.success(wxuserAddrList);
    }

    @GetMapping("/delete")
    @ResponseBody
    public ResultVO delete(@RequestParam("openid") String openid,
                           @RequestParam("id") Integer id) {
        if (StringUtils.isEmpty(openid) || id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        wxuserAddrService.delete(openid, id);
        return ResultVOUtil.success();
    }

    @ResponseBody
    @PostMapping("/save")
    public ResultVO save(@Valid WxuserAddrForm wxuserAddrForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        WxuserAddr wxuserAddr = wxuserAddrService.save(wxuserAddrForm);

        return ResultVOUtil.success(wxuserAddr);
    }

    @ResponseBody
    @PostMapping("/update")
    public ResultVO update(@Valid WxuserAddrUpdateForm wxuserAddrUpdateForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        WxuserAddr wxuserAddr = wxuserAddrService.update(wxuserAddrUpdateForm);

        return ResultVOUtil.success(wxuserAddr);
    }

    @ResponseBody
    @GetMapping("/findById")
    public ResultVO findById(@RequestParam("id") Integer addrId) {
        if (addrId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return ResultVOUtil.success(wxuserAddrService.findOne(addrId));
    }
}
