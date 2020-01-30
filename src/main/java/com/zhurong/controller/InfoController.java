package com.zhurong.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhurong.bean.Hospital;
import com.zhurong.service.EsService;
import com.zhurong.util.PageResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api("Info接口")
@RestController
public class InfoController {
    
    private static final  Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private EsService esService;

    @Autowired
    public InfoController(EsService esService){
        this.esService = esService;
    }
    
    @RequestMapping(value = "/hospital/{page}/{size}", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据", notes = "根据分页获取")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "path", name = "size", value = "每页最大数据量", dataType = "int", required = true)
    })
    public PageResult<Hospital> hospitals(@PathVariable Integer page, @PathVariable Integer size){
//        Iterable<Hospital> iterable = repository.findAll();
//        Iterator<Hospital> iterator = iterable.iterator();
//        List<Hospital> list = IteratorUtils.toList(iterator);
//        esService.getAll(page, size);
        return esService.getHospAll(page, size);
    }


    @RequestMapping(value = "/supplies/{page}/{size}", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据", notes = "根据分页获取")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "path", name = "size", value = "每页最大数据量", dataType = "int", required = true)
    })
    public JSONArray supplies(){
        JSONArray jsonarray = new JSONArray();

        JSONObject mask = new JSONObject();
        JSONArray masks = new JSONArray();
        addNode(masks, "医用外科口罩", 1);
        addNode(masks, "n95口罩", 2);
        addNode(masks, "一次性医用口罩", 3);
        mask.put("name", "口罩");
        mask.put("types", masks);
        jsonarray.add(mask);

        JSONObject faceMask = new JSONObject();
        JSONArray faceMasks = new JSONArray();
        addNode(faceMasks, "防护面罩", 4);
        addNode(faceMasks, "防冲击眼罩", 5);
        addNode(faceMasks, "防护目镜", 6);
        addNode(faceMasks, "防护眼镜", 7);
        addNode(faceMasks, "一次性医用帽子", 8);
        faceMask.put("name", "面屏眼罩");
        faceMask.put("types", faceMasks);
        jsonarray.add(faceMask);

        JSONObject equipment = new JSONObject();
        JSONArray equipments = new JSONArray();
        addNode(equipments, "测体温设备", 9);
        addNode(equipments, "空气消毒设备", 10);
        addNode(equipments, "医用紫外线消毒车", 11);
        equipment.put("name", "医疗设备");
        equipment.put("types", equipments);
        jsonarray.add(equipment);

        return jsonarray;
    }

    private void addNode(JSONArray array, String name, Integer id) {
        JSONObject node = new JSONObject();
        node.put("name", name);
        node.put("id", id);
        array.add(node);
    }

}
