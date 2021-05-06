package club.doyoudo.platform.controller;


import club.doyoudo.platform.entity.User;
import club.doyoudo.platform.service.IStudentService;
import club.doyoudo.platform.service.IUserService;
import club.doyoudo.platform.vo.ResponseWrapper;
import club.doyoudo.platform.vo.StudentWithAllResult;
import club.doyoudo.platform.vo.StudentWithResult;
import club.doyoudo.platform.vo.Task;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-18
 */
@CrossOrigin
@RestController
@RequestMapping("/student")
public class StudentController {
    @Resource
    IStudentService studentService;
    @Resource
    IUserService userService;

    @ApiOperation(value = "查询总任务量", notes = "查询已经完成任务量", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/count-total")
    public ResponseWrapper countTotal(Long userId) {
        return new ResponseWrapper(true, 200, "查询完成！", studentService.getTotal());
    }

    @ApiOperation(value = "获取核对任务", notes = "获取核对任务，即一个学生的信息还有本人任务情况", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/get-task")
    public ResponseWrapper getTask(Long userId) {
        User user = userService.getById(userId);
        if (!user.getIsAuthorized()) {
            return new ResponseWrapper(true, 601, "当前用户不具有核对授权!", null);
        }
        Task task = studentService.getTask(userId);
        if (task != null) {
            return new ResponseWrapper(true, 200, "加载任务成功!", task);
        }
        return new ResponseWrapper(false, 500, "加载失败,系统异常,请稍后重试!", null);
    }

    @ApiOperation(value = "获取上一个任务", notes = "获取上一个任务，即一个学生的信息还有本人任务情况", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/get-last-task")
    public ResponseWrapper getLastTask(Long userId, Long currentStudentId) {
        User user = userService.getById(userId);
        if (!user.getIsAuthorized()) {
            return new ResponseWrapper(true, 601, "当前用户不具有核对授权!", null);
        }
        Task task = studentService.getLastTask(userId, currentStudentId);
        if (task != null) {
            return new ResponseWrapper(true, 200, "加载任务成功!", task);
        } else {
            return new ResponseWrapper(true, 602, "没有上一个了!", null);
        }
    }

    @ApiOperation(value = "获取下一个任务", notes = "获取下一个任务，即一个学生的信息还有本人任务情况", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/get-next-task")
    public ResponseWrapper getNextTask(Long userId, Long currentStudentId) {
        User user = userService.getById(userId);
        if (!user.getIsAuthorized()) {
            return new ResponseWrapper(true, 601, "当前用户不具有核对授权!", null);
        }
        Task task = studentService.getNextTask(userId, currentStudentId);
        if (task != null) {
            return new ResponseWrapper(true, 200, "加载任务成功!", task);
        } else {
            return new ResponseWrapper(true, 602, "没有下一个了!", null);
        }
    }

    @ApiOperation(value = "获取历史任务", notes = "获取历史任务，即一个学生的信息还有本人任务情况", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/get-history")
    public ResponseWrapper getHistory(Long userId, int current, int size) {
        User user = userService.getById(userId);
        if (!user.getIsAuthorized()) {
            return new ResponseWrapper(true, 601, "当前用户不具有核对授权!", null);
        }
        IPage<StudentWithResult> history = studentService.getHistoryByPage(userId, current, size);
        if (history != null) {
            return new ResponseWrapper(true, 200, "加载任务成功!", history);
        }
        return new ResponseWrapper(false, 500, "加载失败,系统异常,请稍后重试!", null);
    }

    @ApiOperation(value = "模糊查询所有学生列表(含核对结果)", notes = "供核对人员查询使用", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/search-with-result")
    public ResponseWrapper searchStudentWithResult(String search, Long userId, int current, int size) {
        User user = userService.getById(userId);
        if (!user.getIsAuthorized()) {
            return new ResponseWrapper(true, 601, "当前用户不具有核对授权!", null);
        }
        IPage<StudentWithResult> studentWithResultPage = studentService.searchStudentByPageForUser(search, userId, current, size);
        if (studentWithResultPage != null) {
            return new ResponseWrapper(true, 200, "获取学生列表成功!", studentWithResultPage);
        }
        return new ResponseWrapper(false, 500, "加载失败,系统异常,请稍后重试!", null);
    }

    @ApiOperation(value = "模糊查询所有学生列表(含所有核对人员的核对结果)", notes = "供管理员查询使用", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/search-with-all-result")
    public ResponseWrapper searchStudentWithAllResult(String search, int condition, int current, int size) {
        IPage<?> studentWithAllResultPage = studentService.searchStudentByPage(search, condition, current, size);
        if (studentWithAllResultPage != null) {
            return new ResponseWrapper(true, 200, "获取学生列表成功!", studentWithAllResultPage);
        }
        return new ResponseWrapper(false, 500, "加载失败,系统异常,请稍后重试!", null);
    }
}

