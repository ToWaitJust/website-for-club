package cn.iocoder.yudao.module.mall.controller.admin.category;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategoryPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategoryRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategorySaveReqVO;
import cn.iocoder.yudao.module.mall.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 商品分类")
@RestController
@RequestMapping("/mall/category")
@Validated
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping("/create")
    @Operation(summary = "创建商品分类")
    public CommonResult<Long> createCategory(@Valid @RequestBody CategorySaveReqVO createReqVO) {
        return success(categoryService.createCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品分类")
    public CommonResult<Boolean> updateCategory(@Valid @RequestBody CategorySaveReqVO updateReqVO) {
        categoryService.updateCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商品分类")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteCategory(@RequestParam("id") Long id) {
        categoryService.deleteCategory(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得商品分类")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<CategoryRespVO> getCategory(@RequestParam("id") Long id) {
        return success(categoryService.getCategory(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获得商品分类列表")
    public CommonResult<List<CategoryRespVO>> getCategoryList() {
        return success(categoryService.getCategoryList());
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品分类分页")
    public CommonResult<PageResult<CategoryRespVO>> getCategoryPage(@Valid CategoryPageReqVO pageReqVO) {
        return success(categoryService.getCategoryPage(pageReqVO));
    }

}
