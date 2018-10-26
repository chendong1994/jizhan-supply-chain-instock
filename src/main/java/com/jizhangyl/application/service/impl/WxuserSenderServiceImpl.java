package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.WxuserSender;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.enums.SenderStatusEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.WxuserSenderForm;
import com.jizhangyl.application.form.WxuserSenderUpdateForm;
import com.jizhangyl.application.repository.primary.WxuserSenderRepository;
import com.jizhangyl.application.service.WxuserSenderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杨贤达
 * @date 2018/8/7 16:50
 * @description
 */
@Service
public class WxuserSenderServiceImpl implements WxuserSenderService {

    @Autowired
    private WxuserSenderRepository wxuserSenderRepository;

    @Override
    public Page<WxuserSender> findAllByOpenid(String openid, Pageable pageable) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return wxuserSenderRepository.findAllByOpenid(openid, pageable);
    }

    @Override
    public List<WxuserSender> findAllByOpenid(String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return wxuserSenderRepository.findAllByOpenidOrderByCreateTimeDesc(openid);
    }

    @Override
    public void delete(String openid, Integer id) {
        if (StringUtils.isEmpty(openid) || id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
//        WxuserSender sender = wxuserSenderRepository.findOne(id);
        WxuserSender sender = wxuserSenderRepository.getOne(id);
        if (sender == null) {
            throw new GlobalException(ResultEnum.DELETE_FAIL);
        }
        String dbOpenid = sender.getOpenid();
        if (!StringUtils.isEmpty(dbOpenid) && dbOpenid.equals(openid)) {
//            wxuserSenderRepository.delete(id);
            wxuserSenderRepository.deleteById(id);
        } else {
            throw new GlobalException(ResultEnum.ILLEGAL_OPERATION);
        }

    }

    @Override
    public WxuserSender save(WxuserSenderForm wxuserSenderForm) {
        if (wxuserSenderForm == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        WxuserSender wxuserSender = new WxuserSender();
        BeanUtils.copyProperties(wxuserSenderForm, wxuserSender);

        return wxuserSenderRepository.save(wxuserSender);
    }

    @Override
    public WxuserSender update(WxuserSenderUpdateForm wxuserSenderUpdateForm) {
        if (wxuserSenderUpdateForm == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        Integer id = wxuserSenderUpdateForm.getId();
//        WxuserSender sender = wxuserSenderRepository.findOne(id);
        WxuserSender sender = wxuserSenderRepository.getOne(id);
        if (sender == null) {
            throw new GlobalException(ResultEnum.UPDATE_FAIL);
        }

        String dbOpenid = sender.getOpenid();
        if (!StringUtils.isEmpty(dbOpenid) && dbOpenid.equals(wxuserSenderUpdateForm.getOpenid())) {
            BeanUtils.copyProperties(wxuserSenderUpdateForm, sender);
            return wxuserSenderRepository.save(sender);
        } else {
            throw new GlobalException(ResultEnum.ILLEGAL_OPERATION);
        }

    }

    @Override
    public void setDefaultSender(String openid, Integer id) {
        if (StringUtils.isEmpty(openid) || id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
//        WxuserSender sender = wxuserSenderRepository.findOne(id);
        WxuserSender sender = wxuserSenderRepository.getOne(id);
        if (sender == null) {
            throw new GlobalException(ResultEnum.SENDER_NOT_EXIST);
        }

        String dbOpenid = sender.getOpenid();
        if (!StringUtils.isEmpty(dbOpenid) && dbOpenid.equals(openid)) {
            // 判断是否已是默认地址
            if (sender.getIsDefault().equals(SenderStatusEnum.DEFAULT.getCode())) {
                throw new GlobalException(ResultEnum.SET_DEFAULT_ERROR);
            }

            // 如果该用户所有地址中存在已经是默认状态的地址，置为非默认
            List<WxuserSender> wxuserSenderList = wxuserSenderRepository.findByOpenid(openid);
            // 修改该用户的其他默认地址状态为非默认
            wxuserSenderList.stream().filter(e -> {
                if (e.getIsDefault().equals(SenderStatusEnum.DEFAULT.getCode())) {
                    e.setIsDefault(SenderStatusEnum.NOT_DEFAULT.getCode());
                }
                return true;
            }).collect(Collectors.toList());


            sender.setIsDefault(SenderStatusEnum.DEFAULT.getCode());
            wxuserSenderRepository.save(sender);
        } else {
            throw new GlobalException(ResultEnum.ILLEGAL_OPERATION);
        }
    }

    @Override
    public WxuserSender findOne(Integer senderId) {
        if (senderId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
//        return wxuserSenderRepository.findOne(senderId);
        return wxuserSenderRepository.getOne(senderId);
    }

    @Override
    public WxuserSender findSenderAddrByOpenid(String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        WxuserSender wxuserSender = wxuserSenderRepository.findByOpenidAndIsDefault(openid, SenderStatusEnum.DEFAULT.getCode());
        if (wxuserSender == null) {
            throw new GlobalException(ResultEnum.SENDER_NOT_EXIST);
        }
        return wxuserSender;
    }


    @Override
    public WxuserSender findDefault(String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        WxuserSender wxuserSender = wxuserSenderRepository.findByOpenidAndIsDefault(openid, SenderStatusEnum.DEFAULT.getCode());
        if (wxuserSender == null) {
            List<WxuserSender> wxuserSenderList = wxuserSenderRepository.findByOpenid(openid);
            if (wxuserSenderList.size() == 0) {
                throw new GlobalException(ResultEnum.SENDER_NOT_EXIST);
            } else {
                return wxuserSenderList.get(wxuserSenderList.size() - 1);
            }
        }
        return wxuserSender;
    }

    @Override
    public List<WxuserSender> findByOpenidIn(List<String> openidList) {
        return wxuserSenderRepository.findByOpenidIn(openidList);
    }
}
