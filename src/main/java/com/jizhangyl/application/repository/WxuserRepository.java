package com.jizhangyl.application.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jizhangyl.application.dataobject.Wxuser;

/**
 * @author 杨贤达
 * @date 2018/8/2 16:23
 * @description
 */
public interface WxuserRepository extends JpaRepository<Wxuser, Integer> {

    Wxuser findByOpenId(String openid);
    
    Wxuser findByUnionId(String unionid);

    Wxuser findByInviteCode(String inviteCode);

    /**
     * 查询出当前微信openid的下游人数
     * @param openId 微信openid
     * @return 当前微信openid的下游人数
     */
    @Query(value = "select count(*) downStreamNums from wxuser where parent_invite_code = ?1",
    		nativeQuery = true)
    Integer findDownStreamCount(String inviteCode);
    
    List<Wxuser> findByOpenIdIn(List<String> openidList);

    List<Wxuser> findByInviteCodeNotNull();
    
    /**
     * 根据用户上级邀请码和用户等级不为*,查询一段时间的下线用户量
     * @param parentInviteCode
     * @param userGrade
     * @param beginDate
     * @param endDate
     * @return
     */
    List<Wxuser> findByParentInviteCodeAndUserGradeNotAndCreateTimeBetween(String parentInviteCode,Integer userGrade, Date beginDate, Date endDate);
    
    /**
     * 根据用户上级邀请码和用户等级,查询一段时间的下线用户量
     * @param parentInviteCode
     * @param userGrade
     * @param beginDate
     * @param endDate
     * @return
     */
    List<Wxuser> findByParentInviteCodeAndUserGradeAndCreateTimeBetween(String parentInviteCode,Integer userGrade, Date beginDate, Date endDate);
    
    
    /**
     * 根据上级邀请码和创建时间和用户等级不为*，批量查询
     * @param parentInviteCodeList
     * @param userGrade
     * @param beginDate
     * @param endDate
     * @return
     */
    List<Wxuser> findByParentInviteCodeInAndUserGradeNotAndCreateTimeBetween(List<String> parentInviteCodeList,Integer userGrade, Date beginDate, Date endDate);
    
    /**
     * 根据上级邀请码和创建时间和用户等级，批量查询
     * @param parentInviteCodeList
     * @param userGrade
     * @param beginDate
     * @param endDate
     * @return
     */
    List<Wxuser> findByParentInviteCodeInAndUserGradeAndCreateTimeBetween(List<String> parentInviteCodeList,Integer userGrade, Date beginDate, Date endDate);
    
    /**
     * 分页查询用户(返点专用) 
     * @param parentInviteCode
     * @param beginDate
     * @param endDate
     * @param pageable
     * @return
     */
    Page<Wxuser> findByParentInviteCodeAndCreateTimeBetween(String parentInviteCode, Date beginDate, Date endDate,Pageable pageable);
    
    /**
     * 分页查询已经注册填写邀请码的用户
     * @param pageable
     * @return
     */
    List<Wxuser> findByInviteCodeNotNullOrderByCreateTimeDesc();
    /**
     * 模糊查询
     * @param inviteCode
     * @param nickName
     * @param pageable
     * @return
     */
    List<Wxuser> findByInviteCodeLikeOrNickNameLikeAndInviteCodeNotNullOrderByCreateTimeDesc(String inviteCode,String nickName);
    
    List<Wxuser> findByInviteCodeNotNullAndUserGradeNot(Integer userGrade);
    
    
    

}
