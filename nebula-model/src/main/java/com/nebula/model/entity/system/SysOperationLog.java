package com.nebula.model.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@TableName("sys_operation_log")
@Schema(description = "操作日志表")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作描述")
    private String operation;

    @Schema(description = "方法名称")
    private String method;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "响应结果")
    private String responseResult;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "地理位置")
    private String location;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "状态：SUCCESS-成功，FAIL-失败")
    private String status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "执行时间(毫秒)")
    private Long executionTime;

    @Schema(description = "操作时间")
    private OffsetDateTime operationTime;
}
