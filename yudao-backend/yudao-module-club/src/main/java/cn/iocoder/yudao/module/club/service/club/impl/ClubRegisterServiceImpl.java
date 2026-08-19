package cn.iocoder.yudao.module.club.service.club.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterPageReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterRespVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterSaveReqVO;
import cn.iocoder.yudao.module.club.controller.admin.club.vo.ClubRegisterUpdateStatusReqVO;
import cn.iocoder.yudao.module.club.dal.dataobject.club.ClubRegisterDO;
import cn.iocoder.yudao.module.club.dal.mysql.club.ClubRegisterMapper;
import cn.iocoder.yudao.module.club.service.club.ClubRegisterService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 报名 Service 实现
 */
@Service
public class ClubRegisterServiceImpl implements ClubRegisterService {

    @Resource
    private ClubRegisterMapper clubRegisterMapper;

    @Override
    public Long createRegister(ClubRegisterSaveReqVO createReqVO) {
        ClubRegisterDO registerDO = new ClubRegisterDO();
        BeanUtils.copyProperties(createReqVO, registerDO);
        registerDO.setStatus(0); // 默认待处理
        clubRegisterMapper.insert(registerDO);
        return registerDO.getId();
    }

    @Override
    public void updateRegisterStatus(ClubRegisterUpdateStatusReqVO updateReqVO) {
        clubRegisterMapper.updateById(new ClubRegisterDO() {{
            setId(updateReqVO.getId());
            setStatus(updateReqVO.getStatus());
        }});
    }

    @Override
    public PageResult<ClubRegisterRespVO> getRegisterPage(ClubRegisterPageReqVO pageReqVO) {
        PageResult<ClubRegisterDO> pageResult = clubRegisterMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<ClubRegisterDO>()
                        .eqIfPresent(ClubRegisterDO::getBusinessLine, pageReqVO.getBusinessLine())
                        .eqIfPresent(ClubRegisterDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(ClubRegisterDO::getId));
        List<ClubRegisterRespVO> list = pageResult.getList().stream().map(registerDO -> {
            ClubRegisterRespVO respVO = new ClubRegisterRespVO();
            BeanUtils.copyProperties(registerDO, respVO);
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

}
