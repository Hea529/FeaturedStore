package com.preferredmall.user.controller;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.preferredmall.user.dto.AddressSaveDTO;
import com.preferredmall.user.service.AddressService;
import com.preferredmall.user.vo.AddressVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址 CRUD
 */
@Tag(name = "地址管理")
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    private Long currentUserId() {
        Long uid = BaseContext.currentUserId();
        if (uid == null) {
            throw new IllegalStateException("未登录");
        }
        return uid;
    }

    @Operation(summary = "获取地址列表")
    @GetMapping
    public Result<List<AddressVO>> list() {
        return Result.success(addressService.listByUser(currentUserId()));
    }

    @Operation(summary = "新增地址")
    @PostMapping
    public Result<AddressVO> save(@Valid @RequestBody AddressSaveDTO dto) {
        return Result.success(addressService.save(currentUserId(), dto));
    }

    @Operation(summary = "更新地址")
    @PutMapping("/{id}")
    public Result<AddressVO> update(@PathVariable("id") Long id,
                                    @Valid @RequestBody AddressSaveDTO dto) {
        return Result.success(addressService.update(currentUserId(), id, dto));
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        addressService.delete(currentUserId(), id);
        return Result.success();
    }

    @Operation(summary = "设置默认地址")
    @PostMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable("id") Long id) {
        addressService.setDefault(currentUserId(), id);
        return Result.success();
    }
}
