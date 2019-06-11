package com.airlenet.webssh.controller;

import com.airlenet.webssh.api.ApiResult;
import com.airlenet.webssh.entity.DeviceEntity;
import com.airlenet.webssh.service.CacheService;
import com.airlenet.webssh.shell.rtty.RttyDevice;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/rtty")
public class RttyController {
    @Autowired
    private CacheService cacheService;

    @GetMapping("list")
    public ApiResult<List<RttyDevice>> list(DeviceEntity deviceEntity,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestParam(defaultValue = "")String query,
                                              HttpServletRequest req) {
        List<RttyDevice> rttyDeviceList = cacheService.getRttyDeviceList();

        return ApiResult.ok(rttyDeviceList).total();
    }
}
