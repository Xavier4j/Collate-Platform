package club.doyoudo.platform.service;

import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.vo.StudentWithAllResult;
import club.doyoudo.platform.vo.StudentWithResult;
import club.doyoudo.platform.vo.Task;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    int getTotal();

    Task getTask(Long userId);

    Task getLastTask(Long userId, Long currentStudentId);

    Task getNextTask(Long userId, Long currentStudentId);

    IPage<StudentWithResult> getHistoryByPage(Long userId, int current, int size);

    IPage<StudentWithResult> searchStudentByPageForUser(String search, Long userId, int current, int size);

    /**
     * 根据条件查询并筛选学生
     *
     * @param search
     * @param condition 0，代表全部学生，1代表核对成功学生，2代表核对失败学生
     * @param current
     * @param size
     * @return
     */
    IPage<?> searchStudentByPage(String search, int condition, int current, int size);

}
