package club.doyoudo.platform.controller;


import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.entity.User;
import club.doyoudo.platform.service.ICollateResultService;
import club.doyoudo.platform.service.IStudentService;
import club.doyoudo.platform.service.IUserService;
import club.doyoudo.platform.vo.ResponseWrapper;
import club.doyoudo.platform.vo.StudentWithResult;
import club.doyoudo.platform.vo.UserWithProgress;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    IStudentService studentService;
    @Resource
    ICollateResultService collateResultService;

    @ApiOperation(value = "登录", notes = "只需填写username与password", produces = "application/json", httpMethod = "POST")
    @RequestMapping("/login")
    public ResponseWrapper logIn(@RequestBody User user) {
        //创建用户查询条件构造器
        QueryWrapper<User> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("username", user.getUsername());
        adminQueryWrapper.eq("password", user.getPassword());
        //不查询密码，防止泄露
        adminQueryWrapper.select(User.class, u -> !u.getColumn().equals("password"));
        //根据账号密码查询用户
        User selectedUser = userService.getOne(adminQueryWrapper);
        if (selectedUser != null) {
            if (selectedUser.getRole() == 0) {
                return new ResponseWrapper(true, 200, "登录成功,角色为核对人员", selectedUser);
            } else {
                return new ResponseWrapper(true, 201, "登录成功,角色为管理员！", selectedUser);
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
            //根据账号密码查询用户
            User selectedUser = userService.getById(user.getId());
            selectedUser.setPassword(null);
            return new ResponseWrapper(true, 200, "注册成功", selectedUser);
        } else {
            return new ResponseWrapper(false, 500, "系统异常，请稍后重试!", null);
        }
    }

    @ApiOperation(value = "批量注册", notes = "传入多个账户", produces = "application/json", httpMethod = "POST")
    @RequestMapping("/signup-batch")
    public ResponseWrapper signUpBatch(@RequestBody List<User> userList) {
        return userService.signUpBatch(userList);
    }

    @ApiOperation(value = "修改密码", notes = "只需填写username、旧密码、新密码", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/update-password")
    public ResponseWrapper updatePassword(Long userId, String oldPassword, String newPassword) {
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

    @ApiOperation(value = "修改个人信息", notes = "根据课程id修改课程信息", produces = "application/json", httpMethod = "POST")
    @RequestMapping("/update-profile")
    public ResponseWrapper updateProfile(@RequestBody User user) {
        System.out.println(user);
        //修改信息时候不能触碰账号名、密码和角色
        user.setUsername(null);
        user.setPassword(null);
        user.setRole(null);
        if (userService.updateById(user)) {
            return new ResponseWrapper(true, 200, "修改成功", null);
        }
        return new ResponseWrapper(false, 500, "系统异常，请稍后重试!", null);
    }

    @ApiOperation(value = "查询个人信息", notes = "根据用户id查询个人信息", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/select")
    public ResponseWrapper select(Long userId) {
        //根据账号密码查询用户
        User selectedUser = userService.getById(userId);
        selectedUser.setPassword(null);
        return new ResponseWrapper(true, 200, "查询成功!", selectedUser);
    }

    @ApiOperation(value = "查询有效核对人员总数", notes = "查询有效核对人员总数", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/count-collator")
    public ResponseWrapper countCollator() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("role", 0);
        userQueryWrapper.eq("is_authorized", true);
        return new ResponseWrapper(true, 200, "查询成功!", userService.count(userQueryWrapper));
    }

    @ApiOperation(value = "查询核对人员列表", notes = "条件查询核对人员列表", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/search")
    public ResponseWrapper select(String search, int current, int size) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (search != null && !search.equals("")) {
            userQueryWrapper.like("name", search);
        }
        userQueryWrapper.eq("role", 0);
        Page<User> userPage = userService.page(new Page<>(current, size), userQueryWrapper);
        List<User> userList = userPage.getRecords();
        List<UserWithProgress> userWithProgressList = new ArrayList<>();
        for (User user : userList) {
            UserWithProgress userWithProgress = JSONObject.parseObject(JSONObject.toJSONString(user), UserWithProgress.class);
            userWithProgress.setTotal(studentService.getTotal());
            userWithProgress.setComplete(collateResultService.getCompleted(userWithProgress.getId()));
            userWithProgressList.add(userWithProgress);
        }
        Page<UserWithProgress> userWithProgressPage = new Page<>(current, size);
        userWithProgressPage.setRecords(userWithProgressList);
        userWithProgressPage.setPages(userPage.getPages());
        userWithProgressPage.setTotal(userPage.getTotal());
        return new ResponseWrapper(true, 200, "查询成功!", userWithProgressPage);
    }
}

