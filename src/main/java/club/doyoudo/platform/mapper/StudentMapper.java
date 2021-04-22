package club.doyoudo.platform.mapper;

import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.vo.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-21
 */
public interface StudentMapper extends BaseMapper<Student> {
    Task getTask(Long userId, Long studentId);
}
