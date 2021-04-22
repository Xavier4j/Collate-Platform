package club.doyoudo.platform.service.impl;

import club.doyoudo.platform.entity.Profile;
import club.doyoudo.platform.mapper.ProfileMapper;
import club.doyoudo.platform.service.IProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
@Service
public class ProfileServiceImpl extends ServiceImpl<ProfileMapper, Profile> implements IProfileService {
}
