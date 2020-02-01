package com.zhurong.service;

import com.zhurong.bean.Hospital;
import com.zhurong.bean.User;
import com.zhurong.util.PageResult;

import java.util.ArrayList;

public interface EsService {

    void delById(Integer id);

    PageResult<User> getAll(Integer page, Integer size);

    PageResult<Hospital> getHospAll(Integer page, Integer size);

    void save(User user);

    PageResult<User> findByNameLike(Integer page, Integer size, String criteria);

    PageResult<Hospital> findHospByCity(Integer page, Integer size, String city);

    PageResult<Hospital> findHospitalList(Integer page, Integer size, String city, ArrayList<String> supplies, ArrayList<String> catagories);

    PageResult<Hospital> findHospital(String id);

    PageResult<User> search(Integer page, Integer size, String criteria);

    PageResult<User> search(Integer page, Integer size, String name, Integer age, String start, String end);

    void createIndex(String index);

    String delIndex(String index);
}
