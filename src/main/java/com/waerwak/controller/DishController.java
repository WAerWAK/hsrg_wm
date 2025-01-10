package com.waerwak.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waerwak.common.R;
import com.waerwak.entity.Category;
import com.waerwak.entity.Dish;
import com.waerwak.entity.DishDto;
import com.waerwak.entity.DishFlavor;
import com.waerwak.service.CategoryService;
import com.waerwak.service.DishFlavorService;
import com.waerwak.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @FileName DishController
 * @Date 2025/1/8
 * @Description 关于菜品模块的接口
 */
@RestController
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 分页查询 + 模糊搜索
     */
    @GetMapping("/dish/page")
    public R<Page<DishDto>> page(int page, int pageSize, Dish dish) {
        QueryWrapper<Dish> Wrapper = new QueryWrapper<>();
        Page<Dish> dishPage = new Page<>(page, pageSize);
        String dishName = dish.getName();
        Wrapper.like(StringUtils.hasText(dishName), "name", dish.getName())
                .eq("is_deleted", 0);
        Page<DishDto> dishDtoPage = new Page<>();
        dishService.page(dishPage, Wrapper);
        //将dishPage复制到dishDtoPage，dishPage中的list<dish>即records排除
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> collect = records.stream().map((item) ->
        {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                dishDto.setCategoryName(byId.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(collect);
        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     */
    @PostMapping("/dish")
    public R<String> addDish(HttpServletRequest request, @RequestBody DishDto dishDto) {

        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.eq("name",dishDto.getName());
        Dish one = dishService.getOne(wrapper);
        if (one != null && one.getIsDeleted() == 0){
            return R.error("已存在该菜品！");
        }

        // 创建 Dish 实体
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);

        Long employee1 = (Long) request.getSession().getAttribute("employee");
        dish.setCreateUser(employee1);
        dish.setUpdateUser(employee1);
        dish.setCreateTime(new Date());
        dish.setUpdateTime(new Date());

        dish.setCode("22222");
        dish.setStatus(1);
        dish.setSort(0);
        dish.setIsDeleted(0);

        boolean isSaved;
        // 处理口味数据（如果有）
        if (dishDto.getFlavors() != null && !dishDto.getFlavors().isEmpty()) {
            if (one != null){
                UpdateWrapper<Dish> wrapper1 = new UpdateWrapper<>();
                wrapper1.eq("id",one.getId());
                isSaved = dishService.update(dish,wrapper1);
                dish.setId(one.getId());
            }else {
                isSaved = dishService.save(dish);
            }

            // 保存每个口味信息
            for (DishDto.Flavor flavor : dishDto.getFlavors()) {
                DishFlavor dishFlavor = new DishFlavor();  // 每次都创建新的对象

                dishFlavor.setDishId(dish.getId());
                dishFlavor.setName(flavor.getName());
                dishFlavor.setValue(JSONUtils.toJSONString(flavor.getValue()));  // 如果 value 是 List<String>，转换为 JSON 字符串
                dishFlavor.setCreateUser(employee1);
                dishFlavor.setUpdateUser(employee1);
                dishFlavor.setCreateTime(new Date());
                dishFlavor.setUpdateTime(new Date());
                dishFlavor.setIsDeleted(0);

                boolean dishFlavorIsSaved = dishFlavorService.save(dishFlavor);
                if (!dishFlavorIsSaved) {
                    return R.error("口味保存失败！");
                }
            }

            if (isSaved) {
                return R.success("菜品添加成功！");
            } else {
                return R.error("菜品添加失败！");
            }
        } else {
            // 保存菜品
            if (one != null){
                UpdateWrapper<Dish> wrapper1 = new UpdateWrapper<>();
                wrapper1.eq("id",one.getId());
                isSaved = dishService.update(dish,wrapper1);
            }else {
                isSaved = dishService.save(dish);
            }
            if (isSaved) {
                return R.success("菜品添加成功！");
            } else {
                return R.error("菜品添加失败！");
            }
        }
    }

    /**
     * 起售停售/批量或单独
     */
    @PostMapping("/dish/status/{status}")
    public R<String> updateDishStatus(@RequestParam List<Long> ids, @PathVariable Integer status, HttpServletRequest request) {
        // 校验状态值合法性
        if (status != 0 && status != 1) {
            return R.error("无效的状态值，只能是 0 或 1");
        }

        // 校验 ids 是否为空
        if (ids == null || ids.isEmpty()) {
            return R.error("操作失败，未提供有效的菜品 ID 列表");
        }

        // 获取当前用户 ID（从 session 中获取，假设 session 中保存了当前用户 ID）
        Long currentUserId = (Long) request.getSession().getAttribute("employee");
        if (currentUserId == null) {
            return R.error("无法获取当前登录用户信息");
        }

        // 批量更新状态、修改人和修改时间
        List<Dish> dishes = ids.stream().map(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            dish.setUpdateUser(currentUserId); // 设置修改人
            dish.setUpdateTime(new Date());   // 设置修改时间
            return dish;
        }).collect(Collectors.toList());

        boolean result = dishService.updateBatchById(dishes);

        // 返回结果
        if (result) {
            String operation = status == 1 ? "起售" : "停售";
            return R.success("菜品已成功" + operation);
        } else {
            return R.error("菜品状态更新失败");
        }
    }


    /**
     * 删除菜品以及相关口味信息（支持批量逻辑删除）
     */
    @DeleteMapping("/dish")
    public R<String> deleteDish( HttpServletRequest request,@RequestParam List<Long> ids) {
        // 获取当前操作用户的 ID
        Long currentUserId = (Long) request.getSession().getAttribute("employee");

        // 检查是否存在正在售卖的菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids).eq(Dish::getStatus, 1); // 状态为1表示正在售卖
        long count = dishService.count(queryWrapper);

        if (count > 0) {
            return R.error("部分菜品正在售卖中，无法删除！");
        }

        // 更新菜品的 is_deleted 字段为 1,并更新 update_time 和 update_user
        List<Dish> dishes = ids.stream().map(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setIsDeleted(1); // 设置逻辑删除标志
            dish.setUpdateTime(new Date()); // 设置当前时间为更新时间
            dish.setUpdateUser(currentUserId); // 设置当前用户为更新人
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);

        // 更新关联的口味信息的 is_deleted 字段为 1
        LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
        flavorQueryWrapper.in(DishFlavor::getDishId, ids);
        List<DishFlavor> flavors = dishFlavorService.list(flavorQueryWrapper);

        if (!flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setIsDeleted(1);// 设置逻辑删除标志
                flavor.setUpdateTime(new Date()); // 设置当前时间为更新时间
                flavor.setUpdateUser(currentUserId); // 设置当前用户为更新人
            });
            dishFlavorService.updateBatchById(flavors);
        }

        return R.success("菜品删除成功！");
    }

    /**
     * 查询菜品信息（修改菜品信息的回显）
     */
    @GetMapping("/dish/{id}")
    public R<DishDto> queryDishById(@PathVariable("id") Long id) {
        Dish byId = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(byId, dishDto);
        QueryWrapper<DishFlavor> wrapper = new QueryWrapper<>();
        wrapper.eq("dish_id", id).eq("is_deleted",0);
        List<DishFlavor> list = dishFlavorService.list(wrapper);
        List<DishDto.Flavor> flavors = new ArrayList<>();
        for (DishFlavor dishFlavor : list) {
            DishDto.Flavor flavor = new DishDto.Flavor();
            flavor.setName(dishFlavor.getName());
            flavor.setValue(Arrays.asList(dishFlavor.getValue().split(",")));  // 假设 value 是以逗号分隔的字符串
            flavors.add(flavor);
        }
        dishDto.setFlavors(flavors);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     */
    @PutMapping("/dish")
    public R<String> editDish(HttpServletRequest request, @RequestBody DishDto dishDto) {
        // 获取当前用户
        Long employee1 = (Long) request.getSession().getAttribute("employee");

        // 先根据菜品ID获取原菜品信息
        Dish existingDish = dishService.getById(dishDto.getId());
        if (existingDish == null) {
            return R.error("菜品不存在！");
        }

        // 更新菜品信息
        existingDish.setName(dishDto.getName());
        existingDish.setPrice(dishDto.getPrice());
        existingDish.setCategoryId(dishDto.getCategoryId());
        existingDish.setImage(dishDto.getImage());
        existingDish.setDescription(dishDto.getDescription());
        existingDish.setUpdateUser(employee1);
        existingDish.setUpdateTime(new Date());

        // 更新菜品
        boolean isDishUpdated = dishService.updateById(existingDish);
        if (!isDishUpdated) {
            return R.error("菜品信息修改失败！");
        }

        // 获取现有的口味信息
        QueryWrapper<DishFlavor> wrapper = new QueryWrapper<>();
        wrapper.eq("dish_id", dishDto.getId());
        List<DishFlavor> existingDishFlavors = dishFlavorService.list(wrapper);

        // 处理口味数据（如果有）
        if (dishDto.getFlavors() != null && !dishDto.getFlavors().isEmpty()) {
            // 遍历现有的口味，检查是否有删除的口味
            for (DishFlavor existingFlavor : existingDishFlavors) {
                // 如果现有口味没有在新的数据中，则将其标记为已删除
                boolean flavorExists = dishDto.getFlavors().stream()
                        .anyMatch(flavor -> flavor.getName().equals(existingFlavor.getName()));
                if (!flavorExists) {
                    existingFlavor.setIsDeleted(1);  // 软删除口味
                    existingFlavor.setUpdateUser(employee1);
                    existingFlavor.setUpdateTime(new Date());
                    boolean isFlavorUpdated = dishFlavorService.updateById(existingFlavor);
                    if (!isFlavorUpdated) {
                        return R.error("口味删除失败！");
                    }
                }
            }

            // 新增新增的口味，或者更新已存在的口味
            for (DishDto.Flavor flavor : dishDto.getFlavors()) {
                // 检查该口味是否已经存在
                DishFlavor existingFlavor = existingDishFlavors.stream()
                        .filter(f -> f.getName().equals(flavor.getName()))
                        .findFirst()
                        .orElse(null);

                if (existingFlavor != null) {
                    // 口味已存在，更新该口味
                    existingFlavor.setValue(JSONUtils.toJSONString(flavor.getValue()));  // 如果 value 是 List<String>，转换为 JSON 字符串
                    existingFlavor.setUpdateUser(employee1);
                    existingFlavor.setUpdateTime(new Date());
                    existingFlavor.setIsDeleted(0);

                    boolean isFlavorUpdated = dishFlavorService.updateById(existingFlavor);
                    if (!isFlavorUpdated) {
                        return R.error("口味更新失败！");
                    }
                } else {
                    // 口味不存在，新增口味
                    DishFlavor newDishFlavor = new DishFlavor();
                    newDishFlavor.setDishId(dishDto.getId());
                    newDishFlavor.setName(flavor.getName());
                    newDishFlavor.setValue(JSONUtils.toJSONString(flavor.getValue()));  // 如果 value 是 List<String>，转换为 JSON 字符串
                    newDishFlavor.setCreateUser(employee1);
                    newDishFlavor.setUpdateUser(employee1);
                    newDishFlavor.setCreateTime(new Date());
                    newDishFlavor.setUpdateTime(new Date());
                    newDishFlavor.setIsDeleted(0);  // 新口味默认未删除

                    boolean isFlavorSaved = dishFlavorService.save(newDishFlavor);
                    if (!isFlavorSaved) {
                        return R.error("口味新增失败！");
                    }
                }
            }
        } else {
            // 如果没有口味数据，更新所有口味为已删除
            existingDishFlavors.forEach(flavor -> {
                flavor.setIsDeleted(1);
                flavor.setUpdateUser(employee1);
                flavor.setUpdateTime(new Date());
                dishFlavorService.updateById(flavor);
            });
        }

        return R.success("菜品信息修改成功！");
    }

    /**
     * 获取菜品列表,用于添加套餐
     */
    @GetMapping("/dish/list")
    public R getCategoryList(Long categoryId,String name){
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.eq("status",1);
        if(name != null && !name.isEmpty()){
            wrapper.like(StringUtils.hasText(name),"name",name);
        }else {
            wrapper.eq("category_id",categoryId);
        }
        List<Dish> list = dishService.list(wrapper);
        return R.success(list);
    }
}
