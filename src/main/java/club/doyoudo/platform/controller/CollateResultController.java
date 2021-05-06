package club.doyoudo.platform.controller;


import club.doyoudo.platform.entity.CollateResult;
import club.doyoudo.platform.entity.User;
import club.doyoudo.platform.service.ICollateResultService;
import club.doyoudo.platform.service.IUserService;
import club.doyoudo.platform.vo.ResponseWrapper;
import club.doyoudo.platform.vo.StudentWithAllResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
@RequestMapping("/collate")
public class CollateResultController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    // 项目根路径下的目录  -- SpringBoot static 目录相当于是根路径下（SpringBoot 默认）
    public final static String EXPORT_PATH_PREFIX = "/export/";

    @Resource
    ICollateResultService collateResultService;
    @Resource
    IUserService userService;

    @ApiOperation(value = "核对", notes = "传入学生id以及核对结果即可", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/collate")
    public ResponseWrapper collate(Long studentId, Long collator, boolean result) {
        CollateResult collateResult = new CollateResult();
        collateResult.setStudentId(studentId);
        collateResult.setCollator(collator);
        collateResult.setResult(result);
        collateResult.setUpdateTime(LocalDateTime.now());
        UpdateWrapper<CollateResult> collateResultUpdateWrapper = new UpdateWrapper<>();
        collateResultUpdateWrapper.eq("collator", collator);
        collateResultUpdateWrapper.eq("student_id", studentId);
        if (collateResultService.saveOrUpdate(collateResult, collateResultUpdateWrapper)) {
            return new ResponseWrapper(true, 200, "核对完成！", null);
        }
        return new ResponseWrapper(false, 500, "系统异常，请稍后重试!", null);
    }

    @ApiOperation(value = "查询已经完成任务量", notes = "查询已经完成任务量", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/count-completed")
    public ResponseWrapper countCompleted(Long userId) {
        return new ResponseWrapper(true, 200, "查询完成！", collateResultService.getCompleted(userId));
    }

    @ApiOperation(value = "导出核对结果", notes = "导出核对结果为Excel", produces = "application/json", httpMethod = "GET")
    @RequestMapping("/export")
    public ResponseWrapper exportResult(HttpServletRequest request) {
        List<StudentWithAllResult> studentWithAllResultList = collateResultService.exportStudentResult();
        System.out.println(studentWithAllResultList.size());
        List<User> userList = userService.selectCollatorList();
        //新建excel报表
        HSSFWorkbook excel = new HSSFWorkbook();
        //添加一个sheet，名字叫"我的POI之旅"
        HSSFSheet hssfSheet = excel.createSheet("核对结果");
        //往excel表格创建一行，excel的行号是从0开始的
        HSSFRow hssfRow = hssfSheet.createRow(0);
        //第一行创建第一个单元格
        HSSFCell hssfCell = hssfRow.createCell(0);
        //设置第一个单元格的值
        hssfCell.setCellValue("学号");
        //设置核对人员姓名
        for (int i = 0; i < userList.size(); i++) {
            //第一行创建第i个单元格
            hssfCell = hssfRow.createCell(i + 1);
            //设置第二个单元格的值
            hssfCell.setCellValue(userList.get(i).getName());
        }
        for (int i = 0; i < studentWithAllResultList.size(); i++) {
            StudentWithAllResult studentWithAllResult = studentWithAllResultList.get(i);
            hssfRow = hssfSheet.createRow(i + 1);
            hssfCell = hssfRow.createCell(0);
            hssfCell.setCellValue(studentWithAllResult.getId());
            for (int j = 0; j < userList.size(); j++) {
                //第一行创建第i个单元格
                hssfCell = hssfRow.createCell(j + 1);
                //设置第二个单元格的值
                if (studentWithAllResult.getCollateSameCollatorIdList() != null && studentWithAllResult.getCollateSameCollatorIdList().contains(userList.get(j).getId())) {
                    hssfCell.setCellValue("核对一致");
                } else if (studentWithAllResult.getCollateDifferentCollatorIdList() != null && studentWithAllResult.getCollateDifferentCollatorIdList().contains(userList.get(j).getId())) {
                    hssfCell.setCellValue("核对不一致");
                } else {
                    hssfCell.setCellValue("未核对");
                }
            }
        }
        //导出文件存储
        FileOutputStream fout = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/");
        //构建文件导出所要保存的"文件夹路径"--这里是相对路径，保存到项目根路径的文件夹下
        String realPath = new String("static" + EXPORT_PATH_PREFIX);
        logger.info("-----------导出文件保存的路径【" + realPath + "】-----------");
        String format = LocalDateTime.now().format(formatter);
        //存放导出文件的文件夹
        File file = new File(realPath + format);
        logger.info("-----------存放导出文件的文件夹【" + file + "】-----------");
        logger.info("-----------输出文件夹绝对路径 -- 这里的绝对路径是相当于当前项目的路径而不是“容器”路径【" + file.getAbsolutePath() + "】-----------");
        if (!file.isDirectory()) {
            //递归生成文件夹
            file.mkdirs();
        }
        //文件名字
        String fileName = "核对结果.xls";
        logger.info("-----------文件要保存后的名字【" + fileName + "】-----------");
        try {
            //用流将其写到指定路径
            logger.info("-----------文件要保存后的名字【" + fileName + "】-----------");
            fout = new FileOutputStream(file.getAbsolutePath() + File.separator + fileName);
            excel.write(fout);
            fout.close();
            String filePath = request.getScheme() + "://" + request.getServerName() + ":8098" + EXPORT_PATH_PREFIX + format + fileName;
            logger.info("-----------【" + filePath + "】-----------");
            return new ResponseWrapper(true, 200, "导出成功", filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseWrapper(false, 400, "导出失败，原因未知", null);
    }
}

