package com.jizhangyl.application.service.impl;

import com.jizhangyl.application.dataobject.primary.WxuserAddr;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.form.WxuserAddrForm;
import com.jizhangyl.application.form.WxuserAddrUpdateForm;
import com.jizhangyl.application.repository.primary.WxuserAddrRepository;
import com.jizhangyl.application.service.WxuserAddrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/8/5 13:09
 * @description
 */
@Slf4j
@Service
public class WxuserAddrServiceImpl implements WxuserAddrService {

    @Autowired
    private WxuserAddrRepository wxuserAddrRepository;

//    @PersistenceContext
//    private EntityManager entityManager;

    @Override
    public List<WxuserAddr> findAll() {
        return wxuserAddrRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveBatch(List<WxuserAddr> wxuserAddrList) throws GlobalException {
        if (CollectionUtils.isEmpty(wxuserAddrList)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        int counter = 0;
        try {
            List<WxuserAddr> dataList = new ArrayList<>();
            for (WxuserAddr wxuserAddr : wxuserAddrList) {
                if (dataList.size() == 300) {
                    wxuserAddrRepository.save(dataList);
                    counter += dataList.size();
                    dataList.clear();
                }
                dataList.add(wxuserAddr);
            }
            if (!dataList.isEmpty()) {
                wxuserAddrRepository.save(dataList);
                counter += dataList.size();
            }
        } catch (Exception e) {
            log.error("【Excel导入】批量导入错误：{}", e);
            throw new GlobalException(ResultEnum.EXCEL_IMPORT_ERROR);
        }
        return counter;
    }

    @Override
    public Page<WxuserAddr> findAllByOpenid(String openid, Pageable pageable) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return wxuserAddrRepository.findAllByOpenid(openid, pageable);
    }

    @Override
    public List<WxuserAddr> findAllByOpenid(String openid) {
        if (StringUtils.isEmpty(openid)) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return wxuserAddrRepository.findAllByOpenidOrderByCreateTimeDesc(openid);
    }

    @Override
    public void delete(String openid, Integer id) {
        if (StringUtils.isEmpty(openid) || id == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        WxuserAddr wxuserAddr = wxuserAddrRepository.findOne(id);
        if (wxuserAddr == null) {
            throw new GlobalException(ResultEnum.DELETE_FAIL);
        }
        String dbOpenid = wxuserAddr.getOpenid();
        if (!StringUtils.isEmpty(dbOpenid) && dbOpenid.equals(openid)) {
            wxuserAddrRepository.delete(id);
        } else {
            throw new GlobalException(ResultEnum.ILLEGAL_OPERATION);
        }
    }

    @Override
    public WxuserAddr save(WxuserAddrForm wxuserAddrForm) {
        if (wxuserAddrForm == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        WxuserAddr wxuserAddr = new WxuserAddr();
        BeanUtils.copyProperties(wxuserAddrForm, wxuserAddr);

        WxuserAddr result = wxuserAddrRepository.save(wxuserAddr);
        return result;
    }

    @Override
    public WxuserAddr update(WxuserAddrUpdateForm wxuserAddrUpdateForm) {
        if (wxuserAddrUpdateForm == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        Integer id = wxuserAddrUpdateForm.getId();

        WxuserAddr wxuserAddr = wxuserAddrRepository.findOne(id);
        if (wxuserAddr == null) {
            throw new GlobalException(ResultEnum.UPDATE_FAIL);
        }

        String dbOpenid = wxuserAddr.getOpenid();
        if (!StringUtils.isEmpty(dbOpenid) && dbOpenid.equals(wxuserAddrUpdateForm.getOpenid())) {
            BeanUtils.copyProperties(wxuserAddrUpdateForm, wxuserAddr);
            return wxuserAddrRepository.save(wxuserAddr);
        } else {
            throw new GlobalException(ResultEnum.ILLEGAL_OPERATION);
        }
    }

    @Override
    public WxuserAddr findOne(Integer addrId) {
        if (addrId == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return wxuserAddrRepository.findOne(addrId);
    }

    @Override
    public WxuserAddr save(WxuserAddr wxuserAddr) {
        if (wxuserAddr == null) {
            throw new GlobalException(ResultEnum.PARAM_EMPTY);
        }
        return wxuserAddrRepository.save(wxuserAddr);
    }

    @Override
    public List<WxuserAddr> findByIdIn(List<Integer> idList) {
        return wxuserAddrRepository.findByIdIn(idList);
    }
}
