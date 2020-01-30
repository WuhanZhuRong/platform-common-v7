package com.zhurong.service;

import com.zhurong.bean.Hospital;
import com.zhurong.bean.User;
import com.zhurong.util.PageResult;

public interface EsService {

    void delById(Integer id);

    PageResult<User> getAll(Integer page, Integer size);
    
    PageResult<Hospital> getHospAll(Integer page, Integer size);

    void save(User user);

    PageResult<User> findByNameLike(Integer page, Integer size, String criteria);

    PageResult<User> search(Integer page, Integer size, String criteria);

    PageResult<User> search(Integer page, Integer size, String name, Integer age, String start, String end);

    void createIndex(String index);

    String delIndex(String index);
}
