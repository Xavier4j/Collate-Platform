package club.doyoudo.platform;

import club.doyoudo.platform.service.IStudentService;
import club.doyoudo.platform.vo.Task;
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
        Task task = studentService.getLastTask(1384379039043842050L,null);
        System.out.println(task);
    }

}
