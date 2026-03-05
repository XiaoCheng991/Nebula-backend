package com.nebula.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nebula.model.entity.system.SysDictItem;
import com.nebula.model.entity.system.SysDictType;
import com.nebula.service.mapper.system.SysDictItemMapper;
import com.nebula.service.mapper.system.SysDictTypeMapper;
import com.nebula.service.service.system.SysDictItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

    private final SysDictTypeMapper sysDictTypeMapper;

    @Override
    public List<SysDictItem> getItemsByDictCode(String dictCode) {
        SysDictType dictType = sysDictTypeMapper.selectOne(
                new LambdaQueryWrapper<SysDictType>()
                        .eq(SysDictType::getDictCode, dictCode)
                        .eq(SysDictType::getStatus, "ACTIVE")
        );
        if (dictType == null) {
            return List.of();
        }
        return list(
                new LambdaQueryWrapper<SysDictItem>()
                        .eq(SysDictItem::getDictTypeId, dictType.getId())
                        .eq(SysDictItem::getStatus, "ACTIVE")
                        .orderByAsc(SysDictItem::getSortOrder)
        );
    }
}
