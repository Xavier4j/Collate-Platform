package club.doyoudo.platform;

import club.doyoudo.platform.controller.UserController;
import club.doyoudo.platform.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class userTask {

    @Resource
    UserController userController;

    @Test
    public void logInTest() {
        User user = new User();
        user.setUsername("root");
        user.setPassword("123456");
        System.out.println(userController.logIn(user));
    }

    @Test
    public void signUpTest() {
        User user = new User();
        user.setUsername("user2");
        user.setPassword("123456");
        System.out.println(userController.signUp(user));
    }

    @Test
    public void updatePasswordTest() {
        System.out.println(userController.updatePassword(1386968857631297538L, "12356", "123456"));
    }

    @Test
    public void updateProfileTest() {
        User user = new User();
        user.setId(1386968857631297538L);
        user.setUsername("root");
        user.setPassword("123456");
        user.setRole(1);
        user.setAvatarUrl("https://s3.ax1x.com/2021/01/25/sLmiB6.jpg");
        user.setIsAuthorized(true);
        user.setName("董振威");
        user.setNote("我还是不想写...");
        System.out.println(userController.updateProfile(user));
    }

    @Test
    public void selectTest() {
        System.out.println(userController.select(1386968857631297538L));
    }

    @Test
    public void countCollatorTest() {
        System.out.println(userController.countCollator());
    }

}
