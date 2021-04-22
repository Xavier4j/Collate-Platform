package club.doyoudo.platform.controller;


import club.doyoudo.platform.entity.Profile;
import club.doyoudo.platform.service.IProfileService;
import club.doyoudo.platform.vo.ResponseWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/profile")
public class ProfileController {
    @Resource
    IProfileService profileService;

    @ApiOperation(value = "查询个人信息", notes = "根据用户id查询个人信息", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/select")
    public ResponseWrapper select(Long userId) {
        return new ResponseWrapper(true, 200, "查询成功!", profileService.getById(userId));
    }

    @ApiOperation(value = "修改个人信息", notes = "根据课程id修改课程信息", produces = "application/json", httpMethod = "POST")
    @RequestMapping("/update")
    public ResponseWrapper update(@RequestBody Profile profile) {
        UpdateWrapper<Profile> profileUpdateWrapper = new UpdateWrapper<>();
        profileUpdateWrapper.eq("id", profile.getId());
        if (profileService.update(profile, profileUpdateWrapper)) {
            return new ResponseWrapper(true, 200, "修改成功", null);
        }
        return new ResponseWrapper(false, 500, "系统异常，请稍后重试!", null);
    }
}

