package com.jenkin.log.logplatform.repositories;

import com.jenkin.log.logplatform.entity.pos.PartitionInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartitionRepository extends JpaRepository<PartitionInfo,Integer> {

    Page<PartitionInfo> findByPartitionUser(String partitionUser, Pageable pageable);
    Page<PartitionInfo> findByPartitionUserAndPartitionTopic(String partitionUser,String topicKey, Pageable pageable);


}
