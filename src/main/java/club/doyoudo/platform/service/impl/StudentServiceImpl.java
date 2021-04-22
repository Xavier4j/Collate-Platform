package club.doyoudo.platform.service.impl;

import club.doyoudo.platform.entity.CollateResult;
import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.mapper.StudentMapper;
import club.doyoudo.platform.service.ICollateResultService;
import club.doyoudo.platform.service.IStudentService;
import club.doyoudo.platform.vo.Task;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Resource
    ICollateResultService collateResultService;
    @Resource
    StudentMapper studentMapper;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    List<Student> studentList = new ArrayList<>();

    @Resource
    IStudentService studentService;


    @Override
    public void LoadStudentPhotos() {

        File admission_photos = new File("static/images/2020年新生照片4194人");
        File entrance_photos = new File("static/images/云南大学2020级新生入学照片4099人");

        if (!admission_photos.exists() || !entrance_photos.exists()) {
            return;
        }

        logger.info("下面是录取照片");
        System.out.println(admission_photos.getPath());
        traverseAdmissionPhotos(admission_photos);
        studentService.updateBatchById(studentList);

        logger.info("下面是入学照片");
        System.out.println(entrance_photos.getPath());
        traverseEntrancePhotos(entrance_photos);
    }

    @Override
    public Task getTask(Long userId) {
        return studentMapper.getTask(userId, null);
    }

    @Override
    public Task getLastTask(Long userId, Long currentStudentId) {
        CollateResult currentCollateResult = collateResultService.getById(currentStudentId);
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.orderByDesc("update_time");
        if (currentCollateResult != null) {
            collateResultQueryWrapper.lt("update_time", currentCollateResult.getUpdateTime());
        }
        CollateResult lastCollateResult = collateResultService.getOne(collateResultQueryWrapper);
        return studentMapper.getTask(userId, lastCollateResult.getId());
    }

    public void traverseAdmissionPhotos(File file) {
        File[] files = file.listFiles();
        for (File f : Objects.requireNonNull(files)) {
            if (f.isFile()) {
//                System.out.println(f.getName());
                Student student = new Student();
                student.setId(Long.parseLong(f.getName().split("\\.")[0]));
                student.setAdmissionPhotoUrl("http://localhost:8098/images/" + f.getPath().replaceAll("\\\\", "/").replace("static/images/", ""));
//                System.out.println(student.getId() + "--------AdmissionPhotoUrl--------" + student.getAdmissionPhotoUrl());
                studentList.add(student);
            } else if (f.isDirectory()) {
                traverseAdmissionPhotos(f);
            }
        }
    }

    public void traverseEntrancePhotos(File file) {
        File[] files = file.listFiles();
        for (File f : Objects.requireNonNull(files)) {
            if (f.isFile()) {
//                System.out.println(f.getName());
                Student student = new Student();
                student.setEntrancePhotoUrl("http://localhost:8098/images/" + f.getPath().replaceAll("\\\\", "/").replace("static/images/", ""));
                UpdateWrapper<Student> studentUpdateWrapper = new UpdateWrapper<>();
                studentUpdateWrapper.eq("id_card_number", f.getName().split("\\.")[0]);
//                System.out.println(f.getName().split("\\.")[0] + "--------EntrancePhotoUrl--------" + student.getEntrancePhotoUrl());
                studentMapper.update(student, studentUpdateWrapper);
            } else if (f.isDirectory()) {
                traverseEntrancePhotos(f);
            }
        }
    }
}
