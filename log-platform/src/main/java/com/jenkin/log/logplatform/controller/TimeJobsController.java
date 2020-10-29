package com.jenkin.log.logplatform.controller;

import com.jenkin.log.common.entity.http.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/7 10:36
 * @description： 定时任务
 * @modified By：
 * @version: 1.0
 */
@RestController
@RequestMapping("/jobs")
@Api(tags = "定时任务")
public class TimeJobsController {

    @GetMapping("/deleteIndexByConfig")
    @ApiOperation("根据配置信息定时删除索引")
    public Response deleteIndexByConfig(){
        //TODO

        return Response.ok();
    }


}
