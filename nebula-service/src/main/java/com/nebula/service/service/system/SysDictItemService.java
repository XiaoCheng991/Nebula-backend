package com.nebula.service.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nebula.model.entity.system.SysDictItem;

import java.util.List;

public interface SysDictItemService extends IService<SysDictItem> {

    List<SysDictItem> getItemsByDictCode(String dictCode);
}
