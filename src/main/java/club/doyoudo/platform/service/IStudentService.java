package club.doyoudo.platform.service;

import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.vo.Task;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
public interface IStudentService extends IService<Student> {
    void LoadStudentPhotos();

    Task getTask(Long userId, Long id);
}
