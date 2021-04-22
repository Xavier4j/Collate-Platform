package club.doyoudo.platform.controller;


import club.doyoudo.platform.entity.Profile;
import club.doyoudo.platform.entity.User;
import club.doyoudo.platform.service.IProfileService;
import club.doyoudo.platform.service.IUserService;
import club.doyoudo.platform.vo.ResponseWrapper;
import club.doyoudo.platform.vo.UserWithProfile;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    IUserService userService;
    @Resource
    IProfileService profileService;

    @ApiOperation(value = "登录", notes = "只需填写username与password", produces = "application/json", httpMethod = "POST")
    @RequestMapping("/login")
    public ResponseWrapper logIn(@RequestBody User user) {
        //创建用户查询条件构造器
        QueryWrapper<User> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("username", user.getUsername());
        adminQueryWrapper.eq("password", user.getPassword());
        //根据账号密码查询用户
        User selectedUser = userService.getOne(adminQueryWrapper);
        if (selectedUser != null) {
            //将密码置为空，防止泄露
            selectedUser.setPassword(null);
            UserWithProfile userWithProfile = JSONObject.parseObject(JSONObject.toJSONString(selectedUser), UserWithProfile.class);
            userWithProfile.setProfile(profileService.getById(selectedUser.getId()));
            if (selectedUser.getRole() == 0) {
                return new ResponseWrapper(true, 200, "登录成功,角色为核对人员", userWithProfile);
            } else {
                return new ResponseWrapper(true, 201, "登录成功,角色为管理员！", userWithProfile);
            }
        } else {
            return new ResponseWrapper(false, 600, "登录失败，账号或者密码有误!", null);
        }
    }

    @ApiOperation(value = "注册", notes = "只需填写username与password", produces = "application/json", httpMethod = "POST")
    @RequestMapping("/signup")
    public ResponseWrapper signUp(@RequestBody User user) {
        //创建用户查询条件构造器
        QueryWrapper<User> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("username", user.getUsername());
        //根据账号查询用户,若已经存在,则注册失败
        if (userService.getOne(adminQueryWrapper) != null) {
            return new ResponseWrapper(false, 600, "注册失败，当前用户名已经存在!", null);
        }
        //如果注册账号合法，则进行插入操作
        //只接受username和password,将其他参数
        user.setId(null);
        //只能注册为核对人员
        user.setRole(0);
        if (userService.save(user)) {
            //插入profile
            Profile profile = new Profile();
            profile.setId(user.getId());
            profileService.save(profile);
            //将user的密码置空
            user.setPassword(null);
            UserWithProfile userWithProfile = JSONObject.parseObject(JSONObject.toJSONString(user), UserWithProfile.class);
            userWithProfile.setProfile(profile);
            return new ResponseWrapper(true, 200, "注册成功", userWithProfile);
        } else {
            return new ResponseWrapper(false, 500, "系统异常，请稍后重试!", null);
        }
    }

    @ApiOperation(value = "修改密码", notes = "只需填写username、旧密码、新密码", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/update")
    public ResponseWrapper update(Long userId, String oldPassword, String newPassword) {
        //创建用户查询条件构造器
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        userQueryWrapper.eq("password", oldPassword);
        //根据账号密码查询用户
        User selectedUser = userService.getOne(userQueryWrapper);
        if (selectedUser == null) {
            return new ResponseWrapper(true, 600, "旧密码输入不正确！", null);
        } else {
            User user = new User();
            user.setId(userId);
            user.setPassword(newPassword);
            if (userService.updateById(user)) {
                return new ResponseWrapper(true, 200, "修改成功！", null);
            }
        }
        return new ResponseWrapper(false, 601, "修改失败，请稍后重试!", null);
    }
}

