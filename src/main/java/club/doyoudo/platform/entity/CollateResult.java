package club.doyoudo.platform.entity;

import java.time.LocalDateTime;
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
public class CollateResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学生id
     */
    private Long id;

    /**
     * 核对人id
     */
    private Long collator;

    /**
     * 是否是同一个人，0代表核对失败，1代表核对成功
     */
    private Boolean same;

    /**
     * 最后核对时间
     */
    private LocalDateTime updateTime;


}
