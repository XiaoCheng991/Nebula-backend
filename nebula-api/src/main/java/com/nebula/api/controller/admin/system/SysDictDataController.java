package com.nebula.api.controller.admin.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nebula.common.annotation.OperationLog;
import com.nebula.common.annotation.RequirePermission;
import com.nebula.config.result.Result;
import com.nebula.model.entity.system.SysDictItem;
import com.nebula.service.service.system.SysDictItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system/dict-data")
@RequiredArgsConstructor
@Tag(name = "字典数据管理", description = "字典数据管理接口")
@RequirePermission("system:dict:view")
public class SysDictDataController {

    private final SysDictItemService sysDictItemService;

    @GetMapping("/list")
    @Operation(summary = "获取字典数据列表")
    @RequirePermission("system:dict:query")
    public Result<IPage<SysDictItem>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Long dictTypeId) {

        Page<SysDictItem> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictTypeId, dictTypeId);
        wrapper.orderByAsc(SysDictItem::getSortOrder);

        return Result.success(sysDictItemService.page(page, wrapper));
    }

    @GetMapping("/by-code/{dictCode}")
    @Operation(summary = "根据字典编码获取数据")
    @RequirePermission("system:dict:query")
    public Result<List<SysDictItem>> getByDictCode(@PathVariable String dictCode) {
        return Result.success(sysDictItemService.getItemsByDictCode(dictCode));
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "获取字典数据详情")
    @RequirePermission("system:dict:query")
    public Result<SysDictItem> getById(@PathVariable Long itemId) {
        return Result.success(sysDictItemService.getById(itemId));
    }

    @PostMapping
    @Operation(summary = "新增字典数据")
    @RequirePermission("system:dict:add")
    @OperationLog(module = "字典管理", operation = "新增字典数据")
    public Result<Void> add(@Valid @RequestBody SysDictItem dictItem) {
        sysDictItemService.save(dictItem);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "编辑字典数据")
    @RequirePermission("system:dict:edit")
    @OperationLog(module = "字典管理", operation = "编辑字典数据")
    public Result<Void> edit(@Valid @RequestBody SysDictItem dictItem) {
        sysDictItemService.updateById(dictItem);
        return Result.success();
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "删除字典数据")
    @RequirePermission("system:dict:delete")
    @OperationLog(module = "字典管理", operation = "删除字典数据")
    public Result<Void> delete(@PathVariable Long itemId) {
        sysDictItemService.removeById(itemId);
        return Result.success();
    }
}
