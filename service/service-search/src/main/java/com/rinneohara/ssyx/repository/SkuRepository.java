package com.rinneohara.ssyx.repository;

import com.rinneohara.ssyx.model.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuRepository extends ElasticsearchRepository<SkuEs,Long> {

    Page<SkuEs> findByOrderByHotScoreDesc(Pageable page);

//    Page<SkuEs> findByWareIdAndKeyword(Long wareId, String keyword, Pageable pageable);
//
//    Page<SkuEs> findByWareIdAndCategoryId(Long wareId, Long categoryId, Pageable pageable);


    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable page);

    Page<SkuEs> findByKeywordAndWareId(String keyword, Long wareId, Pageable page);
}
