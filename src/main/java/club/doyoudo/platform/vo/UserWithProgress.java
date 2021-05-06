package club.doyoudo.platform.vo;

import club.doyoudo.platform.entity.User;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class UserWithProgress extends User {
    private int complete;
    private int total;
}
