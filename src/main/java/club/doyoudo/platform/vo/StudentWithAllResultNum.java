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
public class StudentWithAllResultNum extends Student {
    private int same;
    private int different;
    private int total;
}
