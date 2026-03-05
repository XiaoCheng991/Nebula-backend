package com.nebula.model.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.nebula.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
@Schema(description = "数据字典类型表")
public class SysDictType extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "字典类型ID")
    private Long id;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否系统内置，不可删除")
    private Boolean isSystem;

    @Schema(description = "状态：ACTIVE-启用，DISABLED-禁用")
    private String status;
}
