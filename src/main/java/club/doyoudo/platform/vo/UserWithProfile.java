package club.doyoudo.platform.vo;

import club.doyoudo.platform.entity.Profile;
import club.doyoudo.platform.entity.User;
import lombok.Data;

@Data
public class UserWithProfile extends User {
    private Profile profile;
}
