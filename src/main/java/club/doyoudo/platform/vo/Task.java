package club.doyoudo.platform.vo;

import club.doyoudo.platform.entity.Student;
import lombok.Data;

@Data
public class Task {
    private Student student;
    private Boolean same;
    private int completed;
    private int total;
}
