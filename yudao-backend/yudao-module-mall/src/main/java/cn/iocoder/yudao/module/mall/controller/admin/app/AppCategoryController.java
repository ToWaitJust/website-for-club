package cn.iocoder.yudao.module.mall.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mall.controller.app.category.vo.AppCategoryRespVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.CategoryDO;
import cn.iocoder.yudao.module.mall.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户端 - 商品分类")
@RestController
@RequestMapping("/mall/app/category")
@Validated
public class AppCategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "获取启用的分类列表")
    @PermitAll
    public CommonResult<List<AppCategoryRespVO>> getCategoryList() {
        List<CategoryDO> list = categoryService.getEnabledCategoryList();
        return success(BeanUtils.toBean(list, AppCategoryRespVO.class));
    }

}
