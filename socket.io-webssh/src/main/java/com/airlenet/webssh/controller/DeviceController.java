package com.airlenet.webssh.controller;

import com.airlenet.webssh.api.ApiResult;
import com.airlenet.webssh.entity.DeviceEntity;
import com.airlenet.webssh.service.CacheService;
import com.airlenet.webssh.service.DeviceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CacheService cacheService;

    @GetMapping("list")
    public ApiResult<List<DeviceEntity>> list(DeviceEntity deviceEntity,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest req) {

        Page<DeviceEntity> page = new Page<DeviceEntity>(pageNum, pageSize);
//        QueryWrapper<DeviceEntity> queryWrapper = QueryGenerator.initQueryWrapper(deviceEntity, req.getParameterMap());
        return ApiResult.ok(deviceService.page(page));
    }

    @PostMapping("add")
    public Object add(@RequestBody DeviceEntity deviceEntity) {
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
    @PutMapping(value = "/edit")
    public ApiResult<DeviceEntity> edit(@RequestBody DeviceEntity deviceEntity) {
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
    @DeleteMapping(value = "/delete")
    public ApiResult<DeviceEntity> delete(@RequestParam(name = "id", required = true) String id) {
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
    @DeleteMapping(value = "/deleteBatch")
    public ApiResult<DeviceEntity> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
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
    @GetMapping(value = "/queryById")
    public ApiResult<DeviceEntity> queryById(@RequestParam(name = "id", required = true) String id) {
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
