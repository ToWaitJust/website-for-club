package cn.iocoder.yudao.module.mall.controller.admin.product;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.product.vo.ProductSaveReqVO;
import cn.iocoder.yudao.module.mall.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 商品管理")
@RestController
@RequestMapping("/mall/product")
@Validated
public class ProductController {

    @Resource
    private ProductService productService;

    @PostMapping("/create")
    @Operation(summary = "创建商品")
    public CommonResult<Long> createProduct(@Valid @RequestBody ProductSaveReqVO createReqVO) {
        return success(productService.createProduct(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品")
    public CommonResult<Boolean> updateProduct(@Valid @RequestBody ProductSaveReqVO updateReqVO) {
        productService.updateProduct(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商品")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteProduct(@RequestParam("id") Long id) {
        productService.deleteProduct(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得商品")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<ProductRespVO> getProduct(@RequestParam("id") Long id) {
        return success(productService.getProduct(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获得商品列表")
    public CommonResult<List<ProductRespVO>> getProductList() {
        return success(productService.getProductList());
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品分页")
    public CommonResult<PageResult<ProductRespVO>> getProductPage(@Valid ProductPageReqVO pageReqVO) {
        return success(productService.getProductPage(pageReqVO));
    }

}
