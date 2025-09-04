package com.crrc.sys.mapper.mongodb;



import com.crrc.sys.entity.parameter.GlobalSetting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GlobalSettingRepository extends MongoRepository<GlobalSetting, String> {
    GlobalSetting findBySettingId(String settingId);
    boolean existsBySettingId(String settingId);

    // 动态模糊查询方法
    @Query("{$and: ["
            + "?#{ [0] == null ? { $where: 'true' } : { 'setting_name': { $regex: [0], $options: 'i' } } }, "
            + "?#{ [1] == null ? { $where: 'true' } : { '_id': { $regex: [1], $options: 'i' } } } "
            + "]}")
    List<GlobalSetting> findByDynamicCriteria(String settingName, String id);
}