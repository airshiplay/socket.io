package com.airlenet.webssh.controller;

import com.airlenet.webssh.api.ApiResult;
import com.airlenet.webssh.entity.DeviceEntity;
import com.airlenet.webssh.service.CacheService;
import com.airlenet.webssh.shell.rtty.RttyDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;

@Path("rtty")
@Component
public class RttyController {
    @Autowired
    private CacheService cacheService;

    @GET
    @Path("/list")
    public ApiResult<List<RttyDevice>> list(@BeanParam DeviceEntity deviceEntity,
                                            @QueryParam("pageNum") @DefaultValue("1") int pageNum,
                                            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                                            @QueryParam("query") @DefaultValue("") String query,
                                            @Context HttpServletRequest req) {
        List<RttyDevice> rttyDeviceList = cacheService.getRttyDeviceList();

        return ApiResult.ok(rttyDeviceList).total();
    }
}
