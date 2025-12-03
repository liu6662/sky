package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    //批量插入

    /**
     * 批量插入
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id查询
     * @param dishId
     * @return
     */
    @Delete("delete from sky_take_out.dish_flavor where id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id查询
     *
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}
