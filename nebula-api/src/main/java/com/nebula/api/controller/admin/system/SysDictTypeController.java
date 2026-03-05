package com.nebula.api.controller.admin.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nebula.common.annotation.OperationLog;
import com.nebula.common.annotation.RequirePermission;
import com.nebula.config.result.Result;
import com.nebula.model.entity.system.SysDictType;
import com.nebula.service.service.system.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system/dict-type")
@RequiredArgsConstructor
@Tag(name = "字典类型管理", description = "字典类型管理接口")
@RequirePermission("system:dict:view")
public class SysDictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @GetMapping("/list")
    @Operation(summary = "获取字典类型列表")
    @RequirePermission("system:dict:query")
    public Result<IPage<SysDictType>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysDictType::getDictName, keyword)
                    .or().like(SysDictType::getDictCode, keyword));
        }
        wrapper.orderByAsc(SysDictType::getId);

        return Result.success(sysDictTypeService.page(page, wrapper));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有启用的字典类型")
    @RequirePermission("system:dict:query")
    public Result<List<SysDictType>> getAll() {
        List<SysDictType> list = sysDictTypeService.list(
                new LambdaQueryWrapper<SysDictType>()
                        .eq(SysDictType::getStatus, "ACTIVE")
                        .orderByAsc(SysDictType::getId)
        );
        return Result.success(list);
    }

    @GetMapping("/{dictId}")
    @Operation(summary = "获取字典类型详情")
    @RequirePermission("system:dict:query")
    public Result<SysDictType> getById(@PathVariable Long dictId) {
        return Result.success(sysDictTypeService.getById(dictId));
    }

    @PostMapping
    @Operation(summary = "新增字典类型")
    @RequirePermission("system:dict:add")
    @OperationLog(module = "字典管理", operation = "新增字典类型")
    public Result<Void> add(@Valid @RequestBody SysDictType dictType) {
        dictType.setIsSystem(false);
        sysDictTypeService.save(dictType);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "编辑字典类型")
    @RequirePermission("system:dict:edit")
    @OperationLog(module = "字典管理", operation = "编辑字典类型")
    public Result<Void> edit(@Valid @RequestBody SysDictType dictType) {
        sysDictTypeService.updateById(dictType);
        return Result.success();
    }

    @DeleteMapping("/{dictId}")
    @Operation(summary = "删除字典类型")
    @RequirePermission("system:dict:delete")
    @OperationLog(module = "字典管理", operation = "删除字典类型")
    public Result<Void> delete(@PathVariable Long dictId) {
        sysDictTypeService.removeById(dictId);
        return Result.success();
    }
}
