package com.preferredmall.user.service;

import com.preferredmall.user.dto.AddressSaveDTO;
import com.preferredmall.user.entity.Address;
import com.preferredmall.user.vo.AddressVO;

import java.util.List;

public interface AddressService {

    Address getById(Long id);

    Address getDefault(Long userId);

    List<AddressVO> listByUser(Long userId);

    AddressVO save(Long userId, AddressSaveDTO dto);

    AddressVO update(Long userId, Long addressId, AddressSaveDTO dto);

    void delete(Long userId, Long addressId);

    /**
     * 设置默认地址（会把原默认置为 0）
     */
    void setDefault(Long userId, Long addressId);
}
