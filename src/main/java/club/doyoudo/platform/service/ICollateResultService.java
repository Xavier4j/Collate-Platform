package club.doyoudo.platform.service;

import club.doyoudo.platform.entity.CollateResult;
import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.vo.StudentWithAllResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
public interface ICollateResultService extends IService<CollateResult> {

    int getCompleted(Long userId);

    CollateResult getByStudentId(Long userId,Long studentId);

    List<StudentWithAllResult> exportStudentResult();
}
