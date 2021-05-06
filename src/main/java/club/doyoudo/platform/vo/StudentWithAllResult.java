package club.doyoudo.platform.vo;

import club.doyoudo.platform.entity.Student;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 包含所有评价人员的评价
 */
@Data
@ToString(callSuper = true)
public class StudentWithAllResult extends Student {
    private List<Long> collateSameCollatorIdList;
    private List<Long> collateDifferentCollatorIdList;
}
