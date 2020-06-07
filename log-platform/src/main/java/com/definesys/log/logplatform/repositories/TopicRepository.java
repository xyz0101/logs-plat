package com.definesys.log.logplatform.repositories;

import com.definesys.log.logplatform.entity.pos.TopicInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<TopicInfo,Integer> {

    List<TopicInfo> findAllByTopicKeyIn(List<String> topics);



}
