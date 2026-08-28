package cn.iocoder.yudao.module.mall.service.category.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategoryPageReqVO;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategoryRespVO;
import cn.iocoder.yudao.module.mall.controller.admin.category.vo.CategorySaveReqVO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.CategoryDO;
import cn.iocoder.yudao.module.mall.dal.dataobject.mall.ProductDO;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.CategoryMapper;
import cn.iocoder.yudao.module.mall.dal.mysql.mall.ProductMapper;
import cn.iocoder.yudao.module.mall.service.category.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.mall.enums.ErrorCodeConstants.CATEGORY_HAS_PRODUCTS;
import static cn.iocoder.yudao.module.mall.enums.ErrorCodeConstants.CATEGORY_NOT_EXISTS;

/**
 * 商品分类 Service 实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ProductMapper productMapper;

    @Override
    public Long createCategory(CategorySaveReqVO createReqVO) {
        CategoryDO categoryDO = new CategoryDO();
        BeanUtils.copyProperties(createReqVO, categoryDO);
        categoryMapper.insert(categoryDO);
        return categoryDO.getId();
    }

    @Override
    public void updateCategory(CategorySaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        CategoryDO categoryDO = new CategoryDO();
        BeanUtils.copyProperties(updateReqVO, categoryDO);
        categoryMapper.updateById(categoryDO);
    }

    @Override
    public void deleteCategory(Long id) {
        validateExists(id);
        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapperX<ProductDO>().eq(ProductDO::getCategoryId, id));
        if (productCount > 0) {
            throw exception(CATEGORY_HAS_PRODUCTS);
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public CategoryRespVO getCategory(Long id) {
        CategoryDO categoryDO = categoryMapper.selectById(id);
        CategoryRespVO respVO = new CategoryRespVO();
        BeanUtils.copyProperties(categoryDO, respVO);
        return respVO;
    }

    @Override
    public List<CategoryRespVO> getCategoryList() {
        List<CategoryDO> list = categoryMapper.selectList(
                new LambdaQueryWrapperX<CategoryDO>().orderByAsc(CategoryDO::getSort));
        return list.stream().map(categoryDO -> {
            CategoryRespVO respVO = new CategoryRespVO();
            BeanUtils.copyProperties(categoryDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<CategoryRespVO> getCategoryPage(CategoryPageReqVO pageReqVO) {
        PageResult<CategoryDO> pageResult = categoryMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<CategoryDO>()
                        .likeIfPresent(CategoryDO::getName, pageReqVO.getName())
                        .eqIfPresent(CategoryDO::getStatus, pageReqVO.getStatus())
                        .orderByAsc(CategoryDO::getSort));
        List<CategoryRespVO> list = pageResult.getList().stream().map(categoryDO -> {
            CategoryRespVO respVO = new CategoryRespVO();
            BeanUtils.copyProperties(categoryDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public List<CategoryDO> getEnabledCategoryList() {
        return categoryMapper.selectList(
                new LambdaQueryWrapperX<CategoryDO>()
                        .eq(CategoryDO::getStatus, 0)
                        .orderByAsc(CategoryDO::getSort));
    }

    private void validateExists(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
    }

}
