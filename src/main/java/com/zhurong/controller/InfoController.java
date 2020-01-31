package com.zhurong.controller;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.zhurong.model.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhurong.bean.Hospital;
import com.zhurong.service.EsService;
import com.zhurong.util.PageResult;

import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Api("Info接口")
@RestController
@CrossOrigin(origins = "*")
public class InfoController {

    private static final  Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private EsService esService;

    private final static BiMap<String, Integer> SUPPLY = HashBiMap.create();

    static {
        SUPPLY.put("医用外科口罩", 1);
        SUPPLY.put("n95口罩", 2);
        SUPPLY.put("一次性医用口罩", 3);
        SUPPLY.put("防护面罩", 4);
        SUPPLY.put("防冲击眼罩", 5);
        SUPPLY.put("防护目镜", 6);
        SUPPLY.put("防护眼镜", 7);
        SUPPLY.put("一次性医用帽子", 8);
        SUPPLY.put("测体温设备", 9);
        SUPPLY.put("空气消毒设备", 10);
        SUPPLY.put("医用紫外线消毒车", 11);
    }

    @Autowired
    public InfoController(EsService esService){
        this.esService = esService;
    }

    @RequestMapping(value = "/hospitals", method = RequestMethod.POST)
    @ResponseBody
    public PageResult<Hospital> hospitals(Filter filter) {

        ArrayList<String> suppliesList = new ArrayList<String>();
        ArrayList<Integer> suppliesIds = filter.getSupplies();
        if (suppliesIds != null) {
            for (int i = 0; i < suppliesIds.size(); i++) {
                suppliesList.add(i, SUPPLY.inverse().get(suppliesIds.get(i)));
            }
        }

        Integer page = filter.getPage() != null ? filter.getPage():1;
        Integer size = filter.getSize() != null ? filter.getSize():10;
        String city = filter.getCity();

        return esService.findHospitalList(page, size, city, suppliesList);
    }

    @RequestMapping(value = "/hospitals/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PageResult<Hospital> hospital(@PathVariable String id) {
        return esService.findHospital(id);
    }

    @RequestMapping(value = "/supplies", method = RequestMethod.GET)
    @ResponseBody
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
