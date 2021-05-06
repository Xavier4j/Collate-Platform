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
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CollateResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 核对结果id
     */
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 核对人id
     */
    private Long collator;

    /**
     * 核对结果：0代表不一致，1代表一致
     */
    private Boolean result;

    /**
     * 最后核对时间
     */
    private LocalDateTime updateTime;


}
