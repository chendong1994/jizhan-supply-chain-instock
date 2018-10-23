package com.jizhangyl.application.security;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;

/**
 * @author 曲健磊
 * @date 2018年9月22日 下午5:33:29
 * @description 测试shiro的认证以及授权功能
 */
public class ShiroTest {

    /**
     * 每一个openid所对应的一个"伪密码"
     */
    public static final String OPEN_PWD = "JIZHANGYL";

    /**
     * 测试通过shiro默认的realm来进行主体身份认证
     */
    @Test
    public void testAuthenticationByDefaultRealm() {
        // 1.初始化SecurityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");

        // 2.创建SecurityManager对象
        SecurityManager securityManager = factory.getInstance();

        // 3.将SecurityManager对象添加到运行环境
        SecurityUtils.setSecurityManager(securityManager);

        // 4.创建主体,初始化身份信息和凭证信息
        // 4.1.从运行环境中获取一个主体(相当于一个模板)
        Subject subject = SecurityUtils.getSubject();

        // 4.2.创建一个身份令牌,用于存储用户的身份信息和凭证信息
        // 身份信息即openid,凭证信息即密码,由于在真实的环境中不需要验证密码,所以定义一个常量用作"伪密码"
        String openid = "oYAqI1BOwVet_Cjl6a-6lqhJUzlY";
        String fakePwd = OPEN_PWD;
        UsernamePasswordToken token = new UsernamePasswordToken(openid, fakePwd);

        // 5.主体进行身份认证
        try {
            System.out.println("<====通过默认的realm进行认证====>");
            subject.login(token);
        } catch (UnknownAccountException e) { // openid不存在
            System.out.println("openid不存在");
        } catch (IncorrectCredentialsException e) { // 密码不匹配
            System.out.println("密码不匹配");
        } catch (AuthenticationException e) { // others
            System.out.println("其他的认证错误");
            e.printStackTrace();
        }

        // 6.判断主体是否认证成功
        boolean isAuthenticated = false;
        isAuthenticated = subject.isAuthenticated();
        System.out.println("主体是否已经认证成功:" + isAuthenticated);

        // 7.主体退出登录
        subject.logout();
        System.out.println("主体已经退出登录...");

        // 8.再次判断主体是否认证成功
        isAuthenticated = subject.isAuthenticated();
        System.out.println("主体是否已经认证成功:" + isAuthenticated);
    }

    /**
     * 测试通过自定义的realm来进行主体身份认证
     */
    @Test
    public void testAuthenticationByDefinedRealm() {
        // 1.初始化SecurityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-realm.ini");

        // 2.创建SecurityManager对象
        SecurityManager securityManager = factory.getInstance();

        // 3.将SecurityManager对象添加到运行环境
        SecurityUtils.setSecurityManager(securityManager);

        // 4.创建主体,初始化身份信息和凭证信息
        // 4.1.从运行环境中获取一个主体(相当于一个模板)
        Subject subject = SecurityUtils.getSubject();

        // 4.2.创建一个身份令牌,用于存储用户的身份信息和凭证信息
        // 身份信息即openid,凭证信息即密码,由于在真实的环境中不需要验证密码,所以定义一个常量用作"伪密码"

        // <=====!!!!!!!!!!!!从前台传过来的openid!!!!!!!!!!!!!=====>
        // <=====!!!!!!!!!!!!从前台传过来的openid!!!!!!!!!!!!!=====>
        String openid = "oYAqI1BOwVet_Cjl6a-6lqhJUzlY";
        String fakePwd = OPEN_PWD;
        // <=====!!!!!!!!!!!!从前台传过来的openid!!!!!!!!!!!!!=====>
        // <=====!!!!!!!!!!!!从前台传过来的openid!!!!!!!!!!!!!=====>

        UsernamePasswordToken token = new UsernamePasswordToken(openid, fakePwd);

        // 5.主体进行身份认证
        try {
            System.out.println("<====通过自定义的realm进行认证====>");
            subject.login(token);
        } catch (UnknownAccountException e) { // openid不存在
            System.out.println("openid不存在");
        } catch (IncorrectCredentialsException e) { // 密码不匹配
            System.out.println("密码不匹配");
        } catch (AuthenticationException e) { // others
            System.out.println("其他的认证错误");
            e.printStackTrace();
        }

        // 6.判断主体是否认证成功
        boolean isAuthenticated = false;
        isAuthenticated = subject.isAuthenticated();
        System.out.println("主体是否已经认证成功:" + isAuthenticated);

        // 7.主体退出登录
        subject.logout();
        System.out.println("主体已经退出登录...");

        // 8.再次判断主体是否认证成功
        isAuthenticated = subject.isAuthenticated();
        System.out.println("主体是否已经认证成功:" + isAuthenticated);
    }

    /**
     * 测试通过shiro默认的realm来对主体进行权限认证
     */
    @Test
    public void testAuthorizationByDefaultRealm() {
        // 1.初始化SecurityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-permission.ini");

        // 2.创建SecurityManager对象
        SecurityManager securityManager = factory.getInstance();

        // 3.将SecurityManager对象添加到运行环境
        SecurityUtils.setSecurityManager(securityManager);

        // 4.创建主体,初始化身份信息和凭证信息
        // 4.1.从运行环境中获取一个主体(相当于一个模板)
        Subject subject = SecurityUtils.getSubject();

        // 4.2.创建一个身份令牌,用于存储用户的身份信息和凭证信息
        // 身份信息即openid,凭证信息即密码,由于在真实的环境中不需要验证密码,所以定义一个常量用作"伪密码"
        String openid = "oYAqI1BOwVet_Cjl6a-6lqhJUzlY";
        String fakePwd = OPEN_PWD;
        UsernamePasswordToken token = new UsernamePasswordToken(openid, fakePwd);

        // 5.主体进行身份认证
        try {
            System.out.println("<====通过默认的realm进行认证====>");
            subject.login(token);
        } catch (UnknownAccountException e) { // openid不存在
            System.out.println("openid不存在");
        } catch (IncorrectCredentialsException e) { // 密码不匹配
            System.out.println("密码不匹配");
        } catch (AuthenticationException e) { // others
            System.out.println("其他的认证错误");
            e.printStackTrace();
        }

        // 6.判断主体是否认证成功
        boolean isAuthenticated = false;
        isAuthenticated = subject.isAuthenticated();
        System.out.println("主体是否已经认证成功:" + isAuthenticated);

        // 7.===============基于角色的角度进行权限判断===============
        System.out.println("主体是否拥有role1角色:" + subject.hasRole("role1"));
        boolean[] flags = subject.hasRoles(Arrays.asList("role1", "role2"));
        System.out.println("主体是否同时拥有role1和role2两个角色:");
        for (boolean flag : flags) {
            System.out.println(flag);
        }
        System.out.println("主体是否拥有role3角色:" + subject.hasRole("role3"));

        // 8.==============基于更细粒度的资源的角度进行权限判断===========
        System.out.println("主体是否拥有user:add资源权限:" + subject.isPermitted("user:add"));
        flags = subject.isPermitted("user:add:", "user:update:", "user:delete:", "user:add:001:");
        System.out.println("主体是否拥有user:add:,user:update:,user:delete:,user:add:001:资源权限:" + Arrays.toString(flags));
        System.out.println("主体是否拥有user:view资源权限:" + subject.isPermitted("user:view"));
    }

    /**
     * 测试shiro通过自定义的realm来对进行主体的权限认证
     */
    @Test
    public void testAuthorizationByDefinedRealm() {
        // 1.初始化SecurityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-realm.ini");

        // 2.创建SecurityManager对象
        SecurityManager securityManager = factory.getInstance();

        // 3.将SecurityManager对象添加到运行环境
        SecurityUtils.setSecurityManager(securityManager);

        // 4.创建主体,初始化身份信息和凭证信息
        // 4.1.从运行环境中获取一个主体(相当于一个模板)
        Subject subject = SecurityUtils.getSubject();

        // 4.2.创建一个身份令牌,用于存储用户的身份信息和凭证信息
        // 身份信息即openid,凭证信息即密码,由于在真实的环境中不需要验证密码,所以定义一个常量用作"伪密码"
        String openid = "oYAqI1BOwVet_Cjl6a-6lqhJUzlY";
        String fakePwd = OPEN_PWD;
        UsernamePasswordToken token = new UsernamePasswordToken(openid, fakePwd);

        // 5.主体进行身份认证
        try {
            System.out.println("<====通过自定义的realm进行认证====>");
            subject.login(token);
        } catch (UnknownAccountException e) { // openid不存在
            System.out.println("openid不存在");
        } catch (IncorrectCredentialsException e) { // 密码不匹配
            System.out.println("密码不匹配");
        } catch (AuthenticationException e) { // others
            System.out.println("其他的认证错误");
            e.printStackTrace();
        }

        // 6.判断主体是否认证成功
        boolean isAuthenticated = false;
        isAuthenticated = subject.isAuthenticated();
        System.out.println("主体是否已经认证成功:" + isAuthenticated);

        // 7.===============基于角色的角度进行权限判断===============
        System.out.println("主体是否拥有role1角色:" + subject.hasRole("role1"));
        boolean[] flags = subject.hasRoles(Arrays.asList("role1", "role2"));
        System.out.println("主体是否同时拥有role1和role2两个角色:");
        for (boolean flag : flags) {
            System.out.println(flag);
        }
        System.out.println("主体是否拥有role3角色:" + subject.hasRole("role3"));

        // 8.==============基于更细粒度的资源的角度进行权限判断===========
        System.out.println("主体是否拥有user:add资源权限:" + subject.isPermitted("user:add"));
        flags = subject.isPermitted("user:add", "user:update", "user:delete");
        System.out.println("主体是否拥有user:add,user:update,user:delete资源权限:" + Arrays.toString(flags));
        System.out.println("主体是否拥有user:view资源权限:" + subject.isPermitted("user:view"));
    }
}
