package com.waerwak.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waerwak.common.R;
import com.waerwak.entity.*;
import com.waerwak.service.CategoryService;
import com.waerwak.service.SetmealDishService;
import com.waerwak.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @FileName SetmealController
 * @Date 2025/1/8
 * @Description 关于套餐模块的接口
 */
@RestController
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 分页查询 + 模糊搜索
     */
    @GetMapping("/setmeal/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, Setmeal setmeal) {
        // 创建查询条件
        QueryWrapper<Setmeal> wrapper = new QueryWrapper<>();
        String setmealName = setmeal.getName();
        wrapper.like(StringUtils.hasText(setmealName), "name", setmealName) // 模糊搜索套餐名称
                .eq("is_deleted", 0); // 仅查询未删除的套餐

        // 分页对象
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        // 执行分页查询
        setmealService.page(setmealPage, wrapper);

        // 复制分页数据到 DTO 对象中
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        // 转换 records 数据为 DTO 类型
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> collect = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            // 根据分类 ID 查询分类名称并设置到 DTO 中
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                setmealDto.setCategoryName(byId.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        // 设置转换后的 records 到分页对象中
        setmealDtoPage.setRecords(collect);

        // 返回封装结果
        return R.success(setmealDtoPage);
    }

    /**
     * 套餐起售停售/批量或单独
     */
    @PostMapping("/setmeal/status/{status}")
    public R<String> setmealStatusByStatus(@RequestParam List<Long> ids, @PathVariable Integer status, HttpServletRequest request) {
        // 校验状态值合法性
        if (status != 0 && status != 1) {
            return R.error("无效的状态值，只能是 0 或 1");
        }

        // 校验 IDs 是否为空
        if (ids == null || ids.isEmpty()) {
            return R.error("操作失败，未提供有效的套餐 ID 列表");
        }

        // 获取当前用户 ID（从 session 中获取，假设 session 中保存了当前用户 ID）
        Long currentUserId = (Long) request.getSession().getAttribute("employee");
        if (currentUserId == null) {
            return R.error("无法获取当前登录用户信息");
        }

        // 批量更新状态、修改人和修改时间
        List<Setmeal> setmeals = ids.stream().map(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmeal.setUpdateUser(currentUserId); // 设置修改人
            setmeal.setUpdateTime(new Date());   // 设置修改时间
            return setmeal;
        }).collect(Collectors.toList());

        boolean result = setmealService.updateBatchById(setmeals);

        // 返回结果
        if (result) {
            String operation = status == 1 ? "起售" : "停售";
            return R.success("套餐已成功" + operation);
        } else {
            return R.error("套餐状态更新失败");
        }
    }

    /**
     * 新增套餐（若存在相同名称套餐则修改原套餐）
     */
    @PostMapping("/setmeal")
    public R<String> addSetmeal(HttpServletRequest request,@RequestBody SetmealDto setmealDto) {
        // 根据名称查询是否存在相同的套餐
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", setmealDto.getName());
        Setmeal existingSetmeal = setmealService.getOne(queryWrapper);
        Long currentUserId = (Long) request.getSession().getAttribute("employee");

        if (existingSetmeal != null) {
            // 如果存在相同的名称，则更新对应记录
            existingSetmeal.setCategoryId(setmealDto.getCategoryId());
            existingSetmeal.setPrice(setmealDto.getPrice());
            existingSetmeal.setStatus(1); // 状态设为启用
            existingSetmeal.setDescription(setmealDto.getDescription());
            existingSetmeal.setImage(setmealDto.getImage());
            existingSetmeal.setUpdateTime(new Date());
            existingSetmeal.setUpdateUser(currentUserId); // 使用假设的当前用户 ID
            existingSetmeal.setIsDeleted(0);

            boolean updateSetmeal = setmealService.updateById(existingSetmeal);
            if (!updateSetmeal) {
                return R.error("套餐更新失败");
            }
        } else {
            // 如果不存在相同名称，则新增套餐
            Setmeal setmeal = new Setmeal();
            setmeal.setName(setmealDto.getName());
            setmeal.setCategoryId(setmealDto.getCategoryId());
            setmeal.setPrice(setmealDto.getPrice());
            setmeal.setCode(setmealDto.getCode());
            setmeal.setImage(setmealDto.getImage());
            setmeal.setDescription(setmealDto.getDescription());
            setmeal.setStatus(1);
            setmeal.setCreateTime(new Date());
            setmeal.setUpdateTime(new Date());
            setmeal.setCreateUser(currentUserId); // 使用假设的当前用户 ID
            setmeal.setUpdateUser(currentUserId); // 使用假设的当前用户 ID
            setmeal.setIsDeleted(0);

            boolean saveSetmeal = setmealService.save(setmeal);
            if (!saveSetmeal) {
                return R.error("套餐新增失败");
            }

            // 保存套餐菜品信息
            List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
            if (setmealDishes != null && !setmealDishes.isEmpty()) {
                for (SetmealDish setmealDish : setmealDishes) {
                    setmealDish.setSetmealId(setmeal.getId()); // 设置套餐ID
                    setmealDish.setCreateTime(new Date());
                    setmealDish.setUpdateTime(new Date());
                    setmealDish.setCreateUser(currentUserId); // 使用假设的当前用户 ID
                    setmealDish.setUpdateUser(currentUserId); // 使用假设的当前用户 ID
                    setmealDish.setIsDeleted(0);
                }
                // 批量保存套餐菜品
                boolean saveDishes = setmealDishService.saveBatch(setmealDishes);
                if (!saveDishes) {
                    return R.error("套餐菜品新增失败");
                }
            }
        }

        // 返回成功信息
        return R.success(existingSetmeal != null ? "套餐更新成功" : "套餐新增成功");
    }

    /**
     * 删除菜品以及相关口味信息（支持批量删除，逻辑删除）
     */
    @DeleteMapping("/setmeal")
    public R<String> deleteSetmeal(HttpServletRequest request, @RequestParam List<Long> ids) {
        // 获取当前操作用户的 ID
        Long currentUserId = (Long) request.getSession().getAttribute("employee");

        // 校验是否存在正在售卖的菜品
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1); // 状态为1表示正在售卖
        long count = setmealService.count(queryWrapper);
        if (count > 0) {
            return R.error("部分菜品正在售卖中，无法删除！");
        }

        // 设置当前时间
        Date currentDate = new Date();

        // 批量更新菜品的 is_deleted 字段为 1，更新 update_time 和 update_user
        List<Setmeal> setmeals = ids.stream().map(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setIsDeleted(1); // 设置逻辑删除标志
            setmeal.setUpdateTime(currentDate); // 设置当前时间为更新时间
            setmeal.setUpdateUser(currentUserId); // 设置当前用户为更新人
            return setmeal;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmeals); // 批量更新菜品

        // 批量更新关联的口味信息的 is_deleted 字段为 1，更新 update_time 和 update_user
        LambdaQueryWrapper<SetmealDish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.in(SetmealDish::getSetmealId, ids);
        List<SetmealDish> setmealDishes = setmealDishService.list(dishQueryWrapper);

        if (!setmealDishes.isEmpty()) {
            setmealDishes.forEach(dish -> {
                dish.setIsDeleted(1); // 设置逻辑删除标志
                dish.setUpdateTime(currentDate); // 设置当前时间为更新时间
                dish.setUpdateUser(currentUserId); // 设置当前用户为更新人
            });
            setmealDishService.updateBatchById(setmealDishes); // 批量更新口味信息
        }

        return R.success("菜品及其关联信息已成功逻辑删除！");
    }
}
