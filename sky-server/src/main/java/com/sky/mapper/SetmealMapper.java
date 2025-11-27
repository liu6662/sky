package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    @Select("select count(*) from sky_take_out.setmeal where category_id=#{id}")
    Integer countByCategoryId(Long id);

}
