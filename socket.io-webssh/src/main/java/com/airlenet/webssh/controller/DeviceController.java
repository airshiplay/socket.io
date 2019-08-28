package com.airlenet.webssh.controller;

import com.airlenet.webssh.api.ApiResult;
import com.airlenet.webssh.entity.DeviceEntity;
import com.airlenet.webssh.service.CacheService;
import com.airlenet.webssh.service.DeviceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Path("device")
@Component
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CacheService cacheService;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON + ";" + MediaType.CHARSET_PARAMETER + "=UTF-8")
    public ApiResult<List<DeviceEntity>> list(@BeanParam DeviceEntity deviceEntity,
                                              @QueryParam("pageNum") @DefaultValue("1") int pageNum,
                                              @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                                              @QueryParam("query") @DefaultValue("") String query,
                                              @Context HttpServletRequest req) {
        Page<DeviceEntity> page = new Page<DeviceEntity>(pageNum, pageSize);
        QueryWrapper<DeviceEntity> wrapper = new QueryWrapper(deviceEntity);
        wrapper.like("name", query);
        (wrapper).or().like("ip", query);

//        QueryWrapper<DeviceEntity> queryWrapper = QueryGenerator.initQueryWrapper(deviceEntity, req.getParameterMap());

        return ApiResult.ok(deviceService.page(page, wrapper));
    }

    @POST
    @Path("add")
    public Object add(@Valid DeviceEntity deviceEntity) {
        deviceEntity.setIdentifiy(UUID.randomUUID().toString().replace("-", ""));
        boolean save = deviceService.save(deviceEntity);
        if (save) {
            return ApiResult.ok(deviceEntity);
        }
        return ApiResult.error(100, "");
    }

    /**
     * 编辑
     *
     * @param deviceEntity
     * @return
     */
    @PUT
    @Path("edit")
    public ApiResult<DeviceEntity> edit(DeviceEntity deviceEntity) {
        ApiResult<DeviceEntity> result = new ApiResult<DeviceEntity>();
        DeviceEntity sysMessageEntity = deviceService.getById(deviceEntity.getId());
        if (sysMessageEntity == null) {
            result.error(500, "未找到对应实体");
        } else {
            boolean ok = deviceService.updateById(deviceEntity);
            //TODO 返回false说明什么？
            if (ok) {
                result.ok("修改成功!");
            }
        }
        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @DELETE
    @Path("delete")
    public ApiResult<DeviceEntity> delete(@NotNull @QueryParam("id") String id) {
        ApiResult<DeviceEntity> result = new ApiResult<DeviceEntity>();
        DeviceEntity sysMessage = deviceService.getById(id);
        if (sysMessage == null) {
            result.error(500, "未找到对应实体");
        } else {
            boolean ok = deviceService.removeById(id);
            if (ok) {
                result.ok("删除成功!");
            }
        }

        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DELETE
    @Path("deleteBatch")
    public ApiResult<DeviceEntity> deleteBatch(@NotNull @QueryParam("ids") String ids) {
        ApiResult<DeviceEntity> result = new ApiResult<DeviceEntity>();
        if (ids == null || "".equals(ids.trim())) {
            result.error(500, "参数不识别！");
        } else {
            deviceService.removeByIds(Arrays.asList(ids.split(",")));
            result.ok("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GET
    @Path("/queryById")
    public ApiResult<DeviceEntity> queryById(@NotNull @QueryParam("id") String id) {
        ApiResult<DeviceEntity> result = new ApiResult<DeviceEntity>();
        DeviceEntity sysMessage = deviceService.getById(id);
        if (sysMessage == null) {
            result.error(500, "未找到对应实体");
        } else {
            result.setContent(sysMessage);
            result.setSuccess(true);
        }
        return result;
    }

}
