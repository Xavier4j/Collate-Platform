package club.doyoudo.platform.vo;

import lombok.Data;

@Data
public class Task {
    private StudentWithResult studentWithResult;
    private int completed;
    private int total;
}
