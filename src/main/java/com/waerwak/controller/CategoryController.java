package com.waerwak.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waerwak.common.R;
import com.waerwak.entity.Category;
import com.waerwak.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @FileName CategoryController
 * @Date 2025/1/7
 * @Description 关于菜品分类模块的接口
 */
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询全部分类
     */
    @GetMapping("/category/page")
    public R<Page<Category>> page(int page, int pageSize) {
        System.out.println("进入到page方法，获取到的数据是page" + page + "pageSize" + pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Page<Category> pagepageSizePage = new Page<>(page, pageSize);
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pagepageSizePage, categoryLambdaQueryWrapper);
        return R.success(pagepageSizePage);
    }

    /**
     * 添加分类信息
     */
    @PostMapping("/category")
    public R<String> addCategory(HttpServletRequest request, @RequestBody Category category) {
        System.out.println("进入到addCategory方法，获取到的数据是categorye" + category.toString());
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name",category.getName());
        Category one = categoryService.getOne(wrapper);
        if (one != null){
            return R.error("已存在相同套餐/类别！");
        }
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        Long employee1 = (Long) request.getSession().getAttribute("employee");
        category.setCreateUser(employee1);
        category.setUpdateUser(employee1);
        boolean save = categoryService.save(category);
        if (save) {
            return R.success("添加成功");
        }
        return R.error("添加失败");
    }

    /**
     * 修改分类信息
     */
    @PutMapping("/category")
    public R<String> editCategory(HttpServletRequest request, @RequestBody Category category) {
        System.out.println("进入到editCategory方法，获取到的数据是categorye" + category.toString());
        Long employee1 = (Long) request.getSession().getAttribute("employee");
        category.setUpdateUser(employee1);
        category.setUpdateTime(new Date());
        boolean b = categoryService.updateById(category);
        if (b) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    /**
     * 删除分类信息
     */
    @DeleteMapping("/category")
    public R<String> deleCategory(Long id){
        System.out.println("进入到deleCategory获取的数据id："+id);
        boolean b = categoryService.removeById(id);
        if (b){
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    /**
     * 获取菜品分类/套餐分类列表，用于添加菜品/套餐
     */
    @GetMapping("/category/list")
    public R getCategoryList(@RequestParam(value = "type", required = false) Integer type) {
        // 如果 type 不为 null，则根据 type 进行分类筛选
        List<Category> list;
        if (type != null) {
            // 如果是添加菜品，只显示 type 为 1 的分类
            if (type == 1) {
                list = categoryService.list(new QueryWrapper<Category>().eq("type", 1));
            }
            // 如果是添加套餐，只显示 type 为 2 的分类
            else if (type == 2) {
                list = categoryService.list(new QueryWrapper<Category>().eq("type", 2));
            } else {
                // 如果传入了不合法的 type，返回空列表或错误消息
                list = Collections.emptyList(); // 或者返回错误信息
            }
        } else {
            // 如果没有传入 type，则返回所有分类
            list = categoryService.list();
        }
        return R.success(list);
    }

}