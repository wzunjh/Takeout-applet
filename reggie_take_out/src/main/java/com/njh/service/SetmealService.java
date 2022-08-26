package com.njh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.njh.common.R;
import com.njh.domain.Setmeal;
import com.njh.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    SetmealDto getSetmealData(Long id);

    public void updateWithdishById(SetmealDto setmealDto);

    R<List<Setmeal>> findSetmealByCategoryId(Setmeal setmeal);
}
