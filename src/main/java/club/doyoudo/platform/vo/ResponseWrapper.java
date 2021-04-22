package club.doyoudo.platform.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 返回结果类
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper {
    /**
     * 结果状态
     */
    private Boolean status;
    /**
     * 错误码
     */
    private int code;
    /**
     * 结果提示信息
     */
    private String msg;
    /**
     * 结果数据信息
     */
    private Object data;
}