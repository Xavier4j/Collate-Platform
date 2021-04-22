package club.doyoudo.platform.controller;


import club.doyoudo.platform.entity.CollateResult;
import club.doyoudo.platform.service.ICollateResultService;
import club.doyoudo.platform.vo.ResponseWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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
@RequestMapping("/collate")
public class CollateResultController {
    @Resource
    ICollateResultService collateResultService;

    @ApiOperation(value = "核对", notes = "传入学生id以及核对结果即可", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/collate")
    public ResponseWrapper collate(Long id, Long collator, boolean same) {
        CollateResult collateResult = new CollateResult();
        collateResult.setId(id);
        collateResult.setCollator(collator);
        collateResult.setSame(same);
        collateResult.setUpdateTime(LocalDateTime.now());
        if (collateResultService.save(collateResult)) {
            return new ResponseWrapper(true, 200, "核对完成！", null);
        }
        return new ResponseWrapper(false, 500, "系统异常，请稍后重试!", null);
    }
}

