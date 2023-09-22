package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.model.product.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService extends IService<Category> {
    List<Category> findAllList();
}
