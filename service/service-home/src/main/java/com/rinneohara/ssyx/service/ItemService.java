package com.rinneohara.ssyx.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ItemService {
    public Map<String,Object> item(Long id,Long userId);
}
