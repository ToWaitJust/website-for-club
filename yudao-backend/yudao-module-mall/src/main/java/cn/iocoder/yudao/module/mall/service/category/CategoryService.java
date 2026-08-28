package cn.iocoder.yudao.module.mall.service.category;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategoryPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategoryRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategorySaveReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.CategoryDO;

import java.util.List;

/**
 * 商品分类 Service
 */
public interface CategoryService {

    Long createCategory(CategorySaveReqVO createReqVO);

    void updateCategory(CategorySaveReqVO updateReqVO);

    void deleteCategory(Long id);

    CategoryRespVO getCategory(Long id);

    List<CategoryRespVO> getCategoryList();

    PageResult<CategoryRespVO> getCategoryPage(CategoryPageReqVO pageReqVO);

    /**
     * 获取启用的分类列表（用户端使用）
     */
    List<CategoryDO> getEnabledCategoryList();

}
