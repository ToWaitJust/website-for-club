package cn.iocoder.yudao.module.mall.controller.admin.cart;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.cart.vo.CartSaveReqVO;
import cn.iocoder.yudao.module.mall.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 购物车
 */
@Tag(name = "管理后台 - 购物车")
@RestController
@RequestMapping("/mall/cart")
@Validated
public class CartController {

    @Resource
    private CartService cartService;

    @PostMapping("/create")
    @Operation(summary = "添加购物车")
    public CommonResult<Long> createCart(@Valid @RequestBody CartSaveReqVO createReqVO) {
        return success(cartService.createCart(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新购物车")
    public CommonResult<Boolean> updateCart(@Valid @RequestBody CartSaveReqVO updateReqVO) {
        cartService.updateCart(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除购物车")
    public CommonResult<Boolean> deleteCart(@RequestParam("id") Long id) {
        cartService.deleteCart(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得购物车")
    public CommonResult<CartRespVO> getCart(@RequestParam("id") Long id) {
        return success(cartService.getCart(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询购物车")
    public CommonResult<PageResult<CartRespVO>> getCartPage(@Valid CartPageReqVO pageReqVO) {
        return success(cartService.getCartPage(pageReqVO));
    }

}
