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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户登录名 不允许更改 不允许重复
     */
    private String username;

    /**
     * 用户密码 限制6~16位
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 角色，0代表核对人员，1代表系统管理员
     */
    private Integer role;

    /**
     * 0代表未授权,1代表已授权
     */
    private Boolean isAuthorized;

    /**
     * 个人说明
     */
    private String note;


}
