package com.rinneohara.ssyx.repository;

import com.rinneohara.ssyx.model.search.SkuEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;

public interface SkuRepository extends ElasticsearchRepository<SkuEs,Long> {
}
