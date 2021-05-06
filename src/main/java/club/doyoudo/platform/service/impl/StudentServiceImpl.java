package club.doyoudo.platform.service.impl;

import club.doyoudo.platform.entity.CollateResult;
import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.mapper.StudentMapper;
import club.doyoudo.platform.service.ICollateResultService;
import club.doyoudo.platform.service.IStudentService;
import club.doyoudo.platform.vo.StudentWithAllResult;
import club.doyoudo.platform.vo.StudentWithResult;
import club.doyoudo.platform.vo.Task;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    StudentMapper studentMapper;
    @Resource
    ICollateResultService collateResultService;

    //导入照片所需变量
    Logger logger = LoggerFactory.getLogger(this.getClass());
    List<Student> studentList_ = new ArrayList<>();


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
        this.updateBatchById(studentList_);

        logger.info("下面是入学照片");
        System.out.println(entrance_photos.getPath());
        traverseEntrancePhotos(entrance_photos);
    }

    @Override
    public int getTotal() {
        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.isNotNull("admission_photo_url");
        studentQueryWrapper.isNotNull("entrance_photo_url");
        return this.count(studentQueryWrapper);
    }

    @Override
    public Task getTask(Long userId) {
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.eq("collator", userId);
        List<CollateResult> collateResultList = collateResultService.list(collateResultQueryWrapper);
        List<Long> studentIdList = collateResultList.stream().map(CollateResult::getStudentId).collect(Collectors.toList());
        System.out.println(studentIdList);
        //如果查询结果为空，在studentIdList中添加一个-1,防止后续notIn出现异常
        if (studentIdList.size() == 0) {
            studentIdList.add(-1L);
        }
        LambdaQueryChainWrapper<Student> studentLambdaQueryChainWrapper = lambdaQuery().notIn(Student::getId, studentIdList);
        studentLambdaQueryChainWrapper.last("limit 1");
        Student student = studentLambdaQueryChainWrapper.one();
        StudentWithResult studentWithResult = JSONObject.parseObject(JSONObject.toJSONString(student), StudentWithResult.class);
        Task task = new Task();
        task.setStudentWithResult(studentWithResult);
        task.setTotal(this.getTotal());
        task.setCompleted(collateResultService.getCompleted(userId));
        return task;
    }

    @Override
    public Task getLastTask(Long userId, Long currentStudentId) {
        //先查询到当前学生的任务，如果查询为空，说明当前任务还没核对，直接取出历史任务的最新一条即可
        CollateResult currentCollateResult = collateResultService.getByStudentId(userId, currentStudentId);
        //准备查询CollateResult表
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.eq("collator", userId);
        collateResultQueryWrapper.orderByDesc("update_time");
        //如果查询不为空，那就找出当前任务更新时间前的任务
        if (currentCollateResult != null) {
            collateResultQueryWrapper.lt("update_time", currentCollateResult.getUpdateTime());
        }
        //如果查询为空，说明当前任务还没核对，直接取出历史任务的最新一条即可
        collateResultQueryWrapper.last("limit 1");
        CollateResult lastCollateResult = collateResultService.getOne(collateResultQueryWrapper);
        if (lastCollateResult == null) {
            return null;
        }
        Student student = this.getById(lastCollateResult.getStudentId());
        StudentWithResult studentWithResult = JSONObject.parseObject(JSONObject.toJSONString(student), StudentWithResult.class);
        studentWithResult.setResult(lastCollateResult.getResult());
        Task task = new Task();
        task.setStudentWithResult(studentWithResult);
        task.setTotal(this.getTotal());
        task.setCompleted(collateResultService.getCompleted(userId));
        return task;
    }

    @Override
    public Task getNextTask(Long userId, Long currentStudentId) {
        //先查询到当前学生的任务，如果查询为空，说明当前任务还没核对，直接取出历史任务的最新一条即可
        CollateResult currentCollateResult = collateResultService.getByStudentId(userId, currentStudentId);
        //准备查询CollateResult表
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.eq("collator", userId);
        collateResultQueryWrapper.orderByAsc("update_time");
        //如果查询不为空，那就找出当前任务更新时间前的任务
        if (currentCollateResult != null) {
            collateResultQueryWrapper.gt("update_time", currentCollateResult.getUpdateTime());
        }
        //如果查询为空，说明当前任务还没核对，直接取出历史任务的最新一条即可
        collateResultQueryWrapper.last("limit 1");
        CollateResult lastCollateResult = collateResultService.getOne(collateResultQueryWrapper);
        if (lastCollateResult == null) {
            return null;
        }
        Student student = this.getById(lastCollateResult.getStudentId());
        StudentWithResult studentWithResult = JSONObject.parseObject(JSONObject.toJSONString(student), StudentWithResult.class);
        studentWithResult.setResult(lastCollateResult.getResult());
        Task task = new Task();
        task.setStudentWithResult(studentWithResult);
        task.setTotal(this.getTotal());
        task.setCompleted(collateResultService.getCompleted(userId));
        return task;
    }

    @Override
    public IPage<StudentWithResult> getHistoryByPage(Long userId, int current, int size) {
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.eq("collator", userId);
        collateResultQueryWrapper.orderByDesc("update_time");
        Page<CollateResult> collateResultPage = collateResultService.page(new Page<>(current, size), collateResultQueryWrapper);
        List<CollateResult> collateResultList = collateResultPage.getRecords();
//        List<Long> studentIdList = collateResultList.stream().map(CollateResult::getStudentId).collect(Collectors.toList());
//        List<Student> studentList = this.listByIds(studentIdList);
        List<Student> studentList = new ArrayList<>();
        for (CollateResult collateResult : collateResultList) {
            Student student = this.getById(collateResult.getStudentId());
            studentList.add(student);
        }
        return getStudentWithResultIPage(studentList, collateResultList, collateResultPage);
    }

    @Override
    public IPage<StudentWithResult> searchStudentByPageForUser(String search, Long userId, int current, int size) {
        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
        if (search != null && !search.equals("")) {
            studentQueryWrapper.or().like("name", search);
            studentQueryWrapper.or().like("id", search);
            studentQueryWrapper.or().like("id_card_number", search);
        }
        Page<Student> studentPage = this.page(new Page<>(current, size), studentQueryWrapper);
        List<Student> studentList = studentPage.getRecords();
        //如果查询结果未空，直接返回
        if (studentList.size() == 0) {
            Page<StudentWithResult> studentWithResultPage = new Page<>(current, size);
            studentWithResultPage.setRecords(new ArrayList<>());
            studentWithResultPage.setTotal(studentPage.getTotal());
            studentWithResultPage.setPages(studentPage.getPages());
            return studentWithResultPage;
        }
        List<Long> studentIdList = studentList.stream().map(Student::getId).collect(Collectors.toList());
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.eq("collator", userId);
        collateResultQueryWrapper.in("student_id", studentIdList);
        List<CollateResult> collateResultList = collateResultService.list(collateResultQueryWrapper);
        return getStudentWithResultIPage(studentList, collateResultList, studentPage);
    }

    /**
     * 将studentList封装成studentWithResultPage
     *
     * @param studentList       学生列表
     * @param collateResultList 核对结果列表
     * @param page              原查询到的Page,主要是需要拿到Total和Pages
     * @return studentWithResultPage
     */
    private IPage<StudentWithResult> getStudentWithResultIPage(List<Student> studentList, List<CollateResult> collateResultList, Page<?> page) {
        List<StudentWithResult> studentWithResultList = new ArrayList<>();
        for (Student student : studentList) {
            StudentWithResult studentWithResult = JSONObject.parseObject(JSONObject.toJSONString(student), StudentWithResult.class);
            CollateResult collateResult = collateResultList.stream().filter(item -> item.getStudentId().equals(student.getId())).findAny().orElse(null);
            if (collateResult != null) {
                studentWithResult.setResult(collateResult.getResult());
            }
            studentWithResultList.add(studentWithResult);
        }
        Page<StudentWithResult> studentWithResultPage = new Page<>(page.getCurrent(), page.getSize());
        studentWithResultPage.setRecords(studentWithResultList);
        studentWithResultPage.setTotal(page.getTotal());
        studentWithResultPage.setPages(page.getPages());
        return studentWithResultPage;
    }

    @Override
    public IPage<StudentWithAllResult> searchStudentByPage(String search, int condition, int current, int size) {
        if (condition == 0) {
            return  studentMapper.searchAllStudentByPage(new Page<>(current, size), search);
        } else if (condition == 1) {
            return  studentMapper.searchCompletedStudentByPage(new Page<>(current, size), search);
        } else {
            return  studentMapper.searchUnCompletedStudentByPage(new Page<>(current, size), search);
        }
//        //首先条件查询出学生列表
//        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
//        if (search != null && !search.equals("")) {
//            studentQueryWrapper.or().like("name", search);
//            studentQueryWrapper.or().like("id", search);
//            studentQueryWrapper.or().like("id_card_number", search);
//        }
//        Page<Student> studentPage = this.page(new Page<>(current, size), studentQueryWrapper);
//        List<Student> studentList = studentPage.getRecords();
//        //如果查询结果未空，直接返回
//        if (studentList.size() == 0) {
//            Page<StudentWithAllResult> studentWithResultPage = new Page<>(current, size);
//            studentWithResultPage.setRecords(new ArrayList<>());
//            studentWithResultPage.setTotal(studentPage.getTotal());
//            studentWithResultPage.setPages(studentPage.getPages());
//            return studentWithResultPage;
//        }
//        //In查询出以上所有学生的核对结果记录
//        List<Long> studentIdList = studentList.stream().map(Student::getId).collect(Collectors.toList());
//        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
//        collateResultQueryWrapper.in("student_id", studentIdList);
//        List<CollateResult> collateResultList = collateResultService.list(collateResultQueryWrapper);
//
//        //准备把StudentList封装成StudentWithAllResultList
//        List<StudentWithAllResult> studentWithAllResultList = new ArrayList<>();
//        for (Student student : studentList) {
//            StudentWithAllResult studentWithAllResult = JSONObject.parseObject(JSONObject.toJSONString(student), StudentWithAllResult.class);
//            studentWithAllResultList.add(studentWithAllResult);
//        }
//
//        //遍历collateResultList
//        for (CollateResult collateResult : collateResultList) {
//            //从学生中查找与当前核查结果相关的学生
//            StudentWithAllResult studentWithAllResult = studentWithAllResultList.stream().filter(item -> item.getId().equals(collateResult.getStudentId())).findAny().orElse(null);
//
//            //对每一个结果都插入到学生的AllResult中
//            assert studentWithAllResult != null;
//            if (collateResult.getResult()) {
//                if (studentWithAllResult.getCollateSameCollatorIdList() == null) {
//                    List<Long> collateSameCollatorIdList = new ArrayList<>();
//                    collateSameCollatorIdList.add(collateResult.getCollator());
//                    studentWithAllResult.setCollateSameCollatorIdList(collateSameCollatorIdList);
//                } else {
//                    studentWithAllResult.getCollateSameCollatorIdList().add(collateResult.getCollator());
//                }
//            } else {
//                if (studentWithAllResult.getCollateDifferentCollatorIdList() == null) {
//                    List<Long> collateDifferentCollatorIdList = new ArrayList<>();
//                    collateDifferentCollatorIdList.add(collateResult.getCollator());
//                    studentWithAllResult.setCollateDifferentCollatorIdList(collateDifferentCollatorIdList);
//                } else {
//                    studentWithAllResult.getCollateDifferentCollatorIdList().add(collateResult.getCollator());
//                }
//            }
//        }
//
//        Page<StudentWithAllResult> studentWithResultPage = new Page<>(current, size);
//        studentWithResultPage.setRecords(studentWithAllResultList);
//        studentWithResultPage.setTotal(studentPage.getTotal());
//        studentWithResultPage.setPages(studentPage.getPages());
//        return studentWithResultPage;
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
                studentList_.add(student);
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
