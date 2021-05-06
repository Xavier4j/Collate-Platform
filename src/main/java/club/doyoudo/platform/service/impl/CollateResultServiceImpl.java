package club.doyoudo.platform.service.impl;

import club.doyoudo.platform.entity.CollateResult;
import club.doyoudo.platform.entity.Student;
import club.doyoudo.platform.mapper.CollateResultMapper;
import club.doyoudo.platform.service.ICollateResultService;
import club.doyoudo.platform.service.IStudentService;
import club.doyoudo.platform.vo.StudentWithAllResult;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
@Service
public class CollateResultServiceImpl extends ServiceImpl<CollateResultMapper, CollateResult> implements ICollateResultService {
    @Resource
    IStudentService studentService;

    @Override
    public int getCompleted(Long userId) {
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.isNotNull("result");
        collateResultQueryWrapper.eq("collator", userId);
        return this.count(collateResultQueryWrapper);
    }

    @Override
    public CollateResult getByStudentId(Long userId,Long studentId) {
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.eq("student_id",studentId);
        collateResultQueryWrapper.eq("collator", userId);
        return this.getOne(collateResultQueryWrapper);
    }

    @Override
    public List<StudentWithAllResult> exportStudentResult() {
        //首先查询出学生列表
        List<Student> studentList = studentService.list();
        //如果查询结果未空，直接返回
        if (studentList.size() == 0) {
            return new ArrayList<>();
        }
        //In查询出以上所有学生的核对结果记录
        List<Long> studentIdList = studentList.stream().map(Student::getId).collect(Collectors.toList());
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.in("student_id", studentIdList);
        List<CollateResult> collateResultList = this.list(collateResultQueryWrapper);

        //准备把StudentList封装成StudentWithAllResultList
        List<StudentWithAllResult> studentWithAllResultList = new ArrayList<>();
        for (Student student : studentList) {
            StudentWithAllResult studentWithAllResult = JSONObject.parseObject(JSONObject.toJSONString(student), StudentWithAllResult.class);
            studentWithAllResultList.add(studentWithAllResult);
        }

        //遍历collateResultList
        for (CollateResult collateResult : collateResultList) {
            //从学生中查找与当前核查结果相关的学生
            StudentWithAllResult studentWithAllResult = studentWithAllResultList.stream().filter(item -> item.getId().equals(collateResult.getStudentId())).findAny().orElse(null);

            //对每一个结果都插入到学生的AllResult中
            assert studentWithAllResult != null;
            if (collateResult.getResult()) {
                if (studentWithAllResult.getCollateSameCollatorIdList() == null) {
                    List<Long> collateSameCollatorIdList = new ArrayList<>();
                    collateSameCollatorIdList.add(collateResult.getCollator());
                    studentWithAllResult.setCollateSameCollatorIdList(collateSameCollatorIdList);
                } else {
                    studentWithAllResult.getCollateSameCollatorIdList().add(collateResult.getCollator());
                }
            } else {
                if (studentWithAllResult.getCollateDifferentCollatorIdList() == null) {
                    List<Long> collateDifferentCollatorIdList = new ArrayList<>();
                    collateDifferentCollatorIdList.add(collateResult.getCollator());
                    studentWithAllResult.setCollateDifferentCollatorIdList(collateDifferentCollatorIdList);
                } else {
                    studentWithAllResult.getCollateDifferentCollatorIdList().add(collateResult.getCollator());
                }
            }
        }
        return studentWithAllResultList;
    }
}