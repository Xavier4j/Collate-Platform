package club.doyoudo.platform.mapper;

import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.vo.StudentWithAllResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-21
 */
public interface StudentMapper extends BaseMapper<Student> {
    IPage<StudentWithAllResult> searchAllStudentByPage(Page<?> page, String search);

    IPage<StudentWithAllResult> searchCompletedStudentByPage(Page<?> page, String search);

    IPage<StudentWithAllResult> searchUnCompletedStudentByPage(Page<?> page, String search);
}
