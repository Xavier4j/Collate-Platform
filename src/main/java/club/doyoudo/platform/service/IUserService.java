package club.doyoudo.platform.service;

import club.doyoudo.platform.entity.User;
import club.doyoudo.platform.vo.ResponseWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
public interface IUserService extends IService<User> {
    List<User> selectCollatorList();

    ResponseWrapper signUpBatch(List<User> userList);
}
