package com.jizhangyl.application.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.jizhangyl.application.dataobject.UserInfo;
import com.jizhangyl.application.enums.ShiroEnum;
import com.jizhangyl.application.service.UserInfoService;

/**
 * @author 曲健磊
 * @date 2018年9月22日 下午6:07:22
 * @description 自定义用户认证和用户授权
 */
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 用户鉴权(不直接从SecurityManager环境中取菜单列表,从redis中取菜单列表)
     * 
     * 1. 根据用户的unionId,从redis中获取对应的value(value应该是一个json格式的字符串),将其解析为一个对象
     * 2. 若对象不存在,从数据库中读取用户的菜单权限列表,将菜单列表和isChange=0状态放入redis中
     * 3. 对象存在,先判断isChange的值,为0说明当前用户的菜单权限没有被修改,直接从redis中读取菜单权限列表
     * 4. 为1说明该用户的菜单权限已经被修改,此时先从数据库中根据unionid读取该用户的菜单权限列表,然后放入redis中
     * 5. 将isChange值修改为0
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 1.获取用户的身份信息
//        UserInfo userInfo = (UserInfo) principals.getPrimaryPrincipal();
        // 2.获取用户所拥有的角色,然后找到每个角色拥有的菜单权限,然后通过set对所有的权限去重,最后放入authorizationInfo
        String perms1 = "shop:cate:list";
        String perms2 = "shop:cate:add";
        String perms3 = "shop:cate:delete";
        String perms4 = "shop:cate:update";
        Set<String> permissions = new HashSet<String>();
        permissions.add(perms1);
        permissions.add(perms2);
        permissions.add(perms3);
        permissions.add(perms4);
        authorizationInfo.addStringPermissions(permissions);
        
        // 因为redis中的数据是公共的,所以要加锁控制
        redisTemplate.execute(new SessionCallback<Object>() {

            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch("unionId");
                operations.multi();
                
                operations.opsForValue().set("", "");
                
                return operations.exec();
            }
            
        });
        
        return authorizationInfo;
    }
    
    /**
     * 用户认证
     * 
     * 认证后将用户的unionId作为key,将菜单列表(menuList)和状态(isChange)作为value存入redis中
     * 
     * 数据结构:{unionId : "{menuList:dsfks,isChange:0}"}
     * 
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 1.获取token
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        // 2.从token中获取openid
        String openid = upToken.getUsername();

        // 3.模拟从数据库中查询该openid是否存在
        String dbPwd = ShiroEnum.OPEN_PWD.getCode();

        UserInfo userInfo = userInfoService.findByOpenid(openid);
        if (userInfo == null) { // 认证不通过，直接返回
            return null;
        }
        
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userInfo, dbPwd, this.getName());
        return info;
    }

}
