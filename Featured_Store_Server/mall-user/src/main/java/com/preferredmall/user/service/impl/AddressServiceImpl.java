package com.preferredmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.user.dto.AddressSaveDTO;
import com.preferredmall.user.entity.Address;
import com.preferredmall.user.mapper.AddressMapper;
import com.preferredmall.user.service.AddressService;
import com.preferredmall.user.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public Address getById(Long id) {
        return addressMapper.selectById(id);
    }

    @Override
    public Address getDefault(Long userId) {
        return addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .last("LIMIT 1"));
    }

    @Override
    public List<AddressVO> listByUser(Long userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getId))
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressVO save(Long userId, AddressSaveDTO dto) {
        Address address = new Address();
        applyDto(address, dto);
        address.setUserId(userId);
        // 第一个地址自动设为默认
        Address existing = getDefault(userId);
        if (existing == null) {
            address.setIsDefault(1);
        }
        addressMapper.insert(address);
        if (Integer.valueOf(1).equals(dto.getIsDefault())) {
            clearOtherDefault(userId, address.getId());
        }
        return toVO(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressVO update(Long userId, Long addressId, AddressSaveDTO dto) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在或无权修改");
        }
        applyDto(address, dto);
        addressMapper.updateById(address);
        if (Integer.valueOf(1).equals(dto.getIsDefault())) {
            clearOtherDefault(userId, addressId);
        }
        return toVO(address);
    }

    @Override
    public void delete(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在或无权删除");
        }
        addressMapper.deleteById(addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在或无权操作");
        }
        clearAllDefault(userId);
        addressMapper.update(null, new LambdaUpdateWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 1));
    }

    private void clearAllDefault(Long userId) {
        addressMapper.update(null, new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .set(Address::getIsDefault, 0));
    }

    private void clearOtherDefault(Long userId, Long keepId) {
        addressMapper.update(null, new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .ne(Address::getId, keepId)
                .set(Address::getIsDefault, 0));
    }

    private void applyDto(Address a, AddressSaveDTO dto) {
        a.setReceiver(dto.getReceiver());
        a.setPhone(dto.getPhone());
        a.setProvinceId(dto.getProvinceId());
        a.setCityId(dto.getCityId());
        a.setDistrictId(dto.getDistrictId());
        a.setProvinceName(dto.getProvinceName());
        a.setCityName(dto.getCityName());
        a.setDistrictName(dto.getDistrictName());
        a.setDetailAddress(dto.getDetailAddress());
        a.setIsDefault(dto.getIsDefault() == null ? 0 : dto.getIsDefault());
    }

    private AddressVO toVO(Address a) {
        AddressVO vo = new AddressVO();
        vo.setId(a.getId());
        vo.setReceiver(a.getReceiver());
        vo.setPhone(a.getPhone());
        vo.setProvinceName(a.getProvinceName());
        vo.setCityName(a.getCityName());
        vo.setDistrictName(a.getDistrictName());
        vo.setDetailAddress(a.getDetailAddress());
        vo.setIsDefault(a.getIsDefault());
        vo.setCreateTime(a.getCreateTime());
        return vo;
    }
}
