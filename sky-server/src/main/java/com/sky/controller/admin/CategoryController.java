package com.sky.controller.admin;

import com.github.pagehelper.PageHelper;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.sky.result.Result.success;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关")

public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    public Result update(CategoryDTO  categoryDTO){
        categoryService.update(categoryDTO);
        return success();
    }

    /**
     * 分类的分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("套餐分页查询:{}",categoryPageQueryDTO);
        PageResult pageResult=categoryService.pageQuery(categoryPageQueryDTO);
        return success(pageResult);
    }

    /**
     * 分类状态修改
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用禁用分类")
    @PostMapping("/status/{status}")
    public Result StartOrStop(@PathVariable Integer status, Long id){
        log.info("员工状态修改：{}{}", status,id);
        categoryService.StartOrStop(status,id);
        return success();
    }

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result add(@RequestBody  Category category){
        log.info("新增分类：{}", category);
        categoryService.save(category);
        return success();
    }


    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询分类")
    public Result<Category> getById(@PathVariable Long id){
        log.info("根据id查询分类：{}",id);
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<String> deleteById(Long id){
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }



}
