package club.doyoudo.platform.service.impl;

import club.doyoudo.platform.entity.User;
import club.doyoudo.platform.mapper.UserMapper;
import club.doyoudo.platform.service.IUserService;
import club.doyoudo.platform.vo.ResponseWrapper;
import club.doyoudo.platform.vo.UserWithProgress;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public List<User> selectCollatorList() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("role", 0);
        userQueryWrapper.select(User.class, u -> !u.getColumn().equals("password"));
        return this.list(userQueryWrapper);
    }

    @Override
    @Transactional
    public ResponseWrapper signUpBatch(List<User> userList) {
        for (User user : userList) {
            //创建用户查询条件构造器
            QueryWrapper<User> adminQueryWrapper = new QueryWrapper<>();
            adminQueryWrapper.eq("username", user.getUsername());
            //根据账号查询用户,若已经存在,则注册失败
            if (this.getOne(adminQueryWrapper) != null) {
                return new ResponseWrapper(false, 600, "注册失败，用户名\"" + user.getUsername() + "\"已经存在!", null);
            }
            //如果注册账号合法，则继续
            //只接受username和password,将其他参数
            user.setId(null);
            //只能注册为核对人员
            user.setRole(0);
        }
        if (this.saveBatch(userList)) {
            return new ResponseWrapper(true, 200, "批量注册成功", null);
        }
        return new ResponseWrapper(false, 500, "系统异常，请稍后重试!", null);
    }
}
