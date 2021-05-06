package club.doyoudo.platform.vo;

import club.doyoudo.platform.entity.Student;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class StudentWithResult extends Student {
    private Boolean result;
}
