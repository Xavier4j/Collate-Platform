package club.doyoudo.platform;

import club.doyoudo.platform.service.IStudentService;
import club.doyoudo.platform.vo.Task;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class StudentTask {

    @Resource
    IStudentService studentService;

    @Test
    public void LoadStudentTest() {
        studentService.LoadStudentPhotos();
    }

    @Test
    public void getTaskTest() {
        Task task = studentService.getTask(1384379039043842050L);
        System.out.println(task);
    }

    @Test
    public void getLastTaskTest() {
        Task task = studentService.getLastTask(1384379039043842050L, null);
        System.out.println(task);
    }

    @Test
    public void getHistoryTest() {
        studentService.getHistoryByPage(1386970596677795842L, 1, 100);
    }

    @Test
    public void searchStudentByPageForUserTest() {
        studentService.searchStudentByPageForUser("", 1386970596677795842L, 1, 100);
    }

    @Test
    public void searchStudentByPageTest() {
        studentService.searchStudentByPage("", 0, 1, 100);
    }
}
