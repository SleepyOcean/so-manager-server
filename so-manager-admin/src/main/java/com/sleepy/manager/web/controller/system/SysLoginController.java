package com.sleepy.manager.web.controller.system;

import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.blog.common.UnionResponse;
import com.sleepy.manager.common.constant.Constants;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.domain.entity.SysMenu;
import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.common.core.domain.model.LoginBody;
import com.sleepy.manager.common.utils.SecurityUtils;
import com.sleepy.manager.framework.web.service.SysLoginService;
import com.sleepy.manager.framework.web.service.SysPermissionService;
import com.sleepy.manager.system.service.ISysMenuService;
import com.sleepy.manager.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author
 */
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysUserService userService;

    // auth 权限模块 自定义

    /**
     * 通过手机号密码登录
     *
     * @param loginBody
     * @return
     */
    @PostMapping("/loginByPwd")
    public UnionResponse loginByPwd(@RequestBody LoginBody loginBody) {
        SysUser user = userService.selectUserByPhone(loginBody.getPhone());
        if (ObjectUtils.isEmpty(user)) {
            return new UnionResponse.Builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("登录失败！该用户不存在").build();
        }
        String token = loginService.login(user.getUserName(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        return new UnionResponse.Builder()
                .status(HttpStatus.OK)
                .message("登录成功。请妥善保管token！")
                .data(new AssembledData.Builder()
                        .put("token", token)
                        .put("refreshToken", "")
                        .build())
                .build();
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getUserInfo")
    public UnionResponse getUserInfo() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        return new UnionResponse.Builder()
                .status(HttpStatus.OK)
                .data(new AssembledData.Builder()
                        .putAll(user)
                        .put("userName", user.getNickName())
                        .put("userPhone", user.getPhonenumber())
                        .put("userRole", String.join(",", roles))
                        .put("roles", roles)
                        .put("permissions", permissions)
                        .build())
                .build();
    }

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
