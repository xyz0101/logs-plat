package com.definesys.log.logplatform.repositories;

import com.definesys.log.logplatform.entity.pos.UserSystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSystemConfigRepository extends JpaRepository<UserSystemConfig,Integer> {

    UserSystemConfig getBySystemCode(String systemCode);



}
