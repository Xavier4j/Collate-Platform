package club.doyoudo.platform.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Xavier4j
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学号
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 考生号
     */
    private Long candidateNumber;

    /**
     * 身份证号
     */
    private String idCardNumber;

    /**
     * 省份
     */
    private String province;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 院系
     */
    private String faculty;

    /**
     * 专业
     */
    private String major;

    /**
     * 录取照片URL
     */
    private String admissionPhotoUrl;

    /**
     * 入学采集照片URL
     */
    private String entrancePhotoUrl;


}
