package cn.iocoder.yudao.module.mall.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductRespVO;
import cn.iocoder.yudao.module.mall.controller.app.product.vo.AppProductDetailRespVO;
import cn.iocoder.yudao.module.mall.controller.app.product.vo.AppProductRespVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.ProductDO;
import cn.iocoder.yudao.module.mall.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户端 - 商品")
@RestController
@RequestMapping("/mall/app/product")
@Validated
public class AppProductController {

    @Resource
    private ProductService productService;

    @GetMapping("/page")
    @Operation(summary = "获得上架商品分页")
    @PermitAll
    public CommonResult<PageResult<AppProductRespVO>> getProductPage(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ProductPageReqVO pageReqVO = new ProductPageReqVO();
        pageReqVO.setCategoryId(categoryId);
        pageReqVO.setName(name);
        pageReqVO.setStatus(0);
        pageReqVO.setPageNo(pageNo);
        pageReqVO.setPageSize(pageSize);
        PageResult<ProductRespVO> pageResult = productService.getProductPage(pageReqVO);
        return success(new PageResult<>(
                BeanUtils.toBean(pageResult.getList(), AppProductRespVO.class),
                pageResult.getTotal()));
    }

    @GetMapping("/detail")
    @Operation(summary = "获得商品详情")
    @Parameter(name = "id", description = "商品编号", required = true, example = "1")
    @PermitAll
    public CommonResult<AppProductDetailRespVO> getProductDetail(@RequestParam("id") Long id) {
        ProductDO product = productService.getProductDO(id);
        if (product == null || product.getStatus() != 0) {
            return success(null);
        }
        return success(BeanUtils.toBean(product, AppProductDetailRespVO.class));
    }

}
