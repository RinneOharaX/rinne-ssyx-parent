package com.rinneohara.ssyx.service;

import com.rinneohara.ssyx.model.search.SkuEs;
import com.rinneohara.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SkuApiService {
    void upperSku(Long skuId);

    void lowerGoods(Long skuId);

    List<SkuEs> findHotSkuList();

    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);

    void incrHotScore(Long skuId);
}
