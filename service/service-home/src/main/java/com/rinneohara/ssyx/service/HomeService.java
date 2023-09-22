package com.rinneohara.ssyx.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface HomeService {

    Map<String,Object> getDataInFo(Long userId);
}
