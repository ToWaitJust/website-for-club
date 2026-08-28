package cn.iocoder.yudao.module.mall.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.mall.controller.app.cart.vo.AppCartAddReqVO;
import cn.iocoder.yudao.module.mall.controller.app.cart.vo.AppCartRespVO;
import cn.iocoder.yudao.module.mall.controller.app.cart.vo.AppCartUpdateCountReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.CartDO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.ProductDO;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.ProductMapper;
import cn.iocoder.yudao.module.mall.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户端 - 购物车")
@RestController
@RequestMapping("/mall/app/cart")
@Validated
public class AppCartController {

    @Resource
 private CartService cartService;
 @Resource
 private ProductMapper productMapper;

    @GetMapping("/list")
    @Operation(summary = "获取购物车列表（含商品信息）")
    @PermitAll
    public CommonResult<List<AppCartRespVO>> getCartList(@RequestHeader("user-id") Long userId) {
        List<CartDO> list = cartService.getCartListByUserId(userId);
        List<AppCartRespVO> respList = BeanUtils.toBean(list, AppCartRespVO.class);
        if (!respList.isEmpty()) {
            List<Long> productIds = respList.stream().map(AppCartRespVO::getProductId).collect(Collectors.toList());
            List<ProductDO> products = productMapper.selectBatchIds(productIds);
            Map<Long, ProductDO> productMap = products.stream()
                    .collect(Collectors.toMap(ProductDO::getId, p -> p));
            respList.forEach(cart -> {
                ProductDO product = productMap.get(cart.getProductId());
                if (product != null) {
                    cart.setProductName(product.getName());
                    cart.setProductCoverUrl(product.getCoverUrl());
                    cart.setPrice(product.getPrice());
                    cart.setStock(product.getStock());
                }
            });
        }
        return success(respList);
    }

    @PostMapping("/add")
    @Operation(summary = "添加商品到购物车")
    @PermitAll
    public CommonResult<Long> addCart(@RequestHeader("user-id") Long userId,
                                      @Valid @RequestBody AppCartAddReqVO reqVO) {
        Long id = cartService.addCart(userId, reqVO.getProductId(), reqVO.getCount());
        return success(id);
    }

    @PutMapping("/update-count")
    @Operation(summary = "更新购物车商品数量")
    @PermitAll
    public CommonResult<Boolean> updateCartCount(@RequestHeader("user-id") Long userId,
                                                  @Valid @RequestBody AppCartUpdateCountReqVO reqVO) {
        cartService.updateCartCount(userId, reqVO.getId(), reqVO.getCount());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除购物车商品")
    @Parameter(name = "id", description = "购物车记录编号", required = true, example = "1")
    @PermitAll
    public CommonResult<Boolean> deleteCart(@RequestHeader("user-id") Long userId,
                                             @RequestParam("id") Long id) {
        cartService.deleteCart(userId, id);
        return success(true);
    }

}
