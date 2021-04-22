package club.doyoudo.platform.service.impl;

import club.doyoudo.platform.entity.User;
import club.doyoudo.platform.mapper.UserMapper;
import club.doyoudo.platform.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
