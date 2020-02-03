package com.zhurong.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.zhurong.bean.Hospital;
import com.zhurong.model.Filter;
import com.zhurong.service.EsService;
import com.zhurong.util.PageResult;

import io.swagger.annotations.Api;

@Api("Info接口")
@RestController
@CrossOrigin(origins = "*")
public class InfoController {

    private static final  Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private EsService esService;

    private final static BiMap<String, Integer> SUPPLY = HashBiMap.create();

//    static {
//        SUPPLY.put("医用外科口罩", 1);
//        SUPPLY.put("n95口罩", 2);
//        SUPPLY.put("一次性医用口罩", 3);
//        SUPPLY.put("防护面罩", 4);
//        SUPPLY.put("防冲击眼罩", 5);
//        SUPPLY.put("防护目镜", 6);
//        SUPPLY.put("防护眼镜", 7);
//        SUPPLY.put("一次性医用帽子", 8);
//        SUPPLY.put("测体温设备", 9);
//        SUPPLY.put("空气消毒设备", 10);
//        SUPPLY.put("医用紫外线消毒车", 11);
//    }
    static {
        SUPPLY.put("医用耗材", 1);
        SUPPLY.put("一次性医用外科口罩", 7);
        SUPPLY.put("一次性消毒床单", 8);
        SUPPLY.put("一次性医用手套", 9);
        SUPPLY.put("一次性医用乳胶手套", 10);
        SUPPLY.put("一次性医用橡胶手套", 11);
        SUPPLY.put("一次性手术衣", 12);
        SUPPLY.put("一次性医用防护服", 13);
        SUPPLY.put("一次性医用面罩", 14);
        SUPPLY.put("一次性隔离衣", 15);
        SUPPLY.put("一次性医用帽", 16);
        SUPPLY.put("防污染鞋套", 17);

        SUPPLY.put("防护设备", 2);
        SUPPLY.put("N95口罩", 18);
        SUPPLY.put("医用帽", 19);
        SUPPLY.put("医用防护服", 20);
        SUPPLY.put("防护眼镜", 21);
        SUPPLY.put("防护面罩", 22);
        SUPPLY.put("全面型呼吸防护器", 23);
        SUPPLY.put("正压呼吸面罩", 24);
        SUPPLY.put("手术衣", 25);
        SUPPLY.put("正压隔离衣", 26);
        SUPPLY.put("医用手套", 27);
        SUPPLY.put("医用乳胶手套", 28);
        SUPPLY.put("医用橡胶手套", 29);
        SUPPLY.put("防水围裙", 30);
        SUPPLY.put("长筒胶鞋", 31);

        SUPPLY.put("医疗设备", 3);
        SUPPLY.put("手持测温仪", 32);
        SUPPLY.put("有创呼吸机", 33);
        SUPPLY.put("无创呼吸机", 34);
        SUPPLY.put("心电监护仪", 35);
        SUPPLY.put("12导心电图机", 36);
        SUPPLY.put("移动彩超", 37);
        SUPPLY.put("移动DR", 38);
        SUPPLY.put("除颤仪", 39);
        SUPPLY.put("移动式空气消毒机", 40);
        SUPPLY.put("ThermoFisher的TapMan实时荧光定量PCR分析", 41);
        SUPPLY.put("ThermoFisher的AppliedBiosystems实时荧光定量PCR仪器", 42);
        SUPPLY.put("ThermoFisher的SYBRGreen和TapMan实时荧光定量PCR扩增", 43);
        SUPPLY.put("超低容量喷雾器", 44);
        SUPPLY.put("消洗设备", 45);
        SUPPLY.put("空气消毒仪", 46);
        SUPPLY.put("医用紫外线消毒车", 47);
        SUPPLY.put("移动式等离子空气消毒机", 48);

        SUPPLY.put("消毒药品", 4);
        SUPPLY.put("过氧乙酸", 49);
        SUPPLY.put("75%酒精", 50);
        SUPPLY.put("95%酒精", 51);
        SUPPLY.put("快速手消毒液", 52);
        SUPPLY.put("84消毒液", 53);
        SUPPLY.put("过氧化氢", 54);
        SUPPLY.put("碘伏", 55);
        SUPPLY.put("1%活力碘", 56);
        SUPPLY.put("含氯消毒片", 57);
        SUPPLY.put("二氧化氯泡腾片", 58);
        SUPPLY.put("消毒凝胶", 59);
        SUPPLY.put("安利久洗手液", 60);

        SUPPLY.put("药品", 5);
        SUPPLY.put("磷酸奥司他韦", 61);
        SUPPLY.put("洛匹那韦利托那韦", 62);
        SUPPLY.put("盐酸莫西沙星片", 63);
        SUPPLY.put("阿比多尔片", 64);
        SUPPLY.put("胸腺钛肠溶片", 65);

        SUPPLY.put("试剂", 6);
        SUPPLY.put("注射用甲泼尼龙琥珀酸钠", 66);
        SUPPLY.put("病毒核酸试剂", 67);
        SUPPLY.put("快速检测试剂盒", 68);
    }

    @Autowired
    public InfoController(EsService esService){
        this.esService = esService;
    }

    @RequestMapping("/hospitals")
    public PageResult<Hospital> hospitals(Filter filter) {
    	
    	LOGGER.info("进入hospitals参数是{}",filter.toString());

        ArrayList<String> suppliesList = new ArrayList<String>();
        ArrayList<String> catagoriesList = new ArrayList<String>();
        ArrayList<Integer> suppliesIds = filter.getSupplies();
        if (suppliesIds != null) {
            for (int i = 0; i < suppliesIds.size(); i++) {
                Integer id = suppliesIds.get(i);
                if (id > 6) {
                    suppliesList.add(i, SUPPLY.inverse().get(id));
                } else {
                    catagoriesList.add(i, SUPPLY.inverse().get(id));
                }
            }
        }

        Integer page = filter.getPage() != null ? filter.getPage():1;
        Integer size = filter.getSize() != null ? filter.getSize():10;
        String city = filter.getCity();

        LOGGER.info("出hospitals");
        
        return esService.findHospitalList(page, size, city, suppliesList, catagoriesList);
    }

    @GetMapping("/hospitals/{id}")
    public PageResult<Hospital> hospital(@PathVariable String id) {
        return esService.findHospital(id);
    }

    @GetMapping("/supplies")
    public JSONArray supplies(){
        JSONArray jsonarray = new JSONArray();

        JSONObject material = new JSONObject();
        JSONArray materials = new JSONArray();
        addNode(materials, SUPPLY.inverse().get(1), 1);
        for (int i = 7; i <= 17; i++) {
            addNode(materials, SUPPLY.inverse().get(i), i);
        }
        material.put("name", "医用耗材");
        material.put("types", materials);
        jsonarray.add(material);


        JSONObject protection = new JSONObject();
        JSONArray protections = new JSONArray();
        addNode(protections, SUPPLY.inverse().get(2), 2);
        for (int i = 18; i <= 31; i++) {
            addNode(protections, SUPPLY.inverse().get(i), i);
        }
        protection.put("name", "防护设备");
        protection.put("types", protections);
        jsonarray.add(protection);

        JSONObject equipment = new JSONObject();
        JSONArray equipments = new JSONArray();
        addNode(equipments, SUPPLY.inverse().get(3), 3);
        for (int i = 32; i <= 48; i++) {
            addNode(equipments, SUPPLY.inverse().get(i), i);
        }
        equipment.put("name", "医疗设备");
        equipment.put("types", equipments);
        jsonarray.add(equipment);

        JSONObject disinfection = new JSONObject();
        JSONArray disinfections = new JSONArray();
        addNode(disinfections, SUPPLY.inverse().get(4), 4);
        for (int i = 49; i <= 60; i++) {
            addNode(disinfections, SUPPLY.inverse().get(i), i);
        }
        disinfection.put("name", "消毒药品");
        disinfection.put("types", disinfections);
        jsonarray.add(disinfection);

        JSONObject medicine = new JSONObject();
        JSONArray medicines = new JSONArray();
        addNode(medicines, SUPPLY.inverse().get(5), 5);
        for (int i = 61; i <= 65; i++) {
            addNode(medicines, SUPPLY.inverse().get(i), i);
        }
        medicine.put("name", "药品");
        medicine.put("types", medicines);
        jsonarray.add(medicine);

        JSONObject reagent = new JSONObject();
        JSONArray reagents = new JSONArray();
        addNode(reagents, SUPPLY.inverse().get(6), 6);
        for (int i = 66; i <= 68; i++) {
            addNode(reagents, SUPPLY.inverse().get(i), i);
        }
        reagent.put("name", "试剂");
        reagent.put("types", reagents);
        jsonarray.add(reagent);

        return jsonarray;
    }

    private void addNode(JSONArray array, String name, Integer id) {
        JSONObject node = new JSONObject();
        node.put("name", name);
        node.put("id", id);
        array.add(node);
    }

}
