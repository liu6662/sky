package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sky.vo.DishVO;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDTO
     */
    @Transactional
    //事务注解，用来保证操作多个表的时候数据的统一性
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish() {{ BeanUtils.copyProperties(dishDTO, this); }};

        //像菜品表插入一条数据
        dishMapper.insert(dish);

        dish.getId();

        //像菜品口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishDTO){
        //开始分页查询
        PageHelper.startPage(dishDTO.getPage(), dishDTO.getPageSize());
        Page<DishVO> page =dishMapper.pageQuery(dishDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    public void delete(List<Long> ids){
        //判断是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品和其附带的口味
        for (Long id : ids) {
            dishMapper.deleteByIds(id);
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    public DishVO getByIdWithFlavor(Long id){

        Dish dish = dishMapper.getById(id);

        //口味
        List<DishFlavor> flavors =dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO() {{
            BeanUtils.copyProperties(dish, this);
        }};
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    public void update(DishDTO dishDTO){
        //修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //批量插入口味数据
        if(flavors!=null && flavors.size()>0){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }


    }
}
