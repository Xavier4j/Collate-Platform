package club.doyoudo.platform.controller;


import club.doyoudo.platform.entity.CollateResult;
import club.doyoudo.platform.entity.User;
import club.doyoudo.platform.service.ICollateResultService;
import club.doyoudo.platform.service.IStudentService;
import club.doyoudo.platform.service.IUserService;
import club.doyoudo.platform.vo.ResponseWrapper;
import club.doyoudo.platform.vo.Task;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    ICollateResultService collateResultService;
    @Resource
    IUserService userService;

    @ApiOperation(value = "获取核对任务", notes = "获取核对任务，即一个学生的信息还有本人任务情况", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/get-task")
    public ResponseWrapper getTask(Long userId) {
        User user = userService.getById(userId);
        if (!user.getStatus()) {
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
    public ResponseWrapper getLastTask(Long userId, Long id) {
        User user = userService.getById(userId);
        if (!user.getStatus()) {
            return new ResponseWrapper(true, 601, "当前用户不具有核对授权!", null);
        }
        CollateResult currentCollateResult = collateResultService.getById(id);
        QueryWrapper<CollateResult> collateResultQueryWrapper = new QueryWrapper<>();
        collateResultQueryWrapper.orderByDesc("update_time");
        if (currentCollateResult != null) {
            collateResultQueryWrapper.lt("update_time", currentCollateResult.getUpdateTime());
        }
        CollateResult lastCollateResult = collateResultService.getOne(collateResultQueryWrapper);
        Task task = studentService.getTask(userId,lastCollateResult.getId());
        if (task != null) {
            return new ResponseWrapper(true, 200, "加载任务成功!", task);
        }
        return new ResponseWrapper(false, 500, "加载失败,系统异常,请稍后重试!", null);
    }
}

