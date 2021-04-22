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
 * @since 2021-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 个人说明
     */
    private String note;


}
