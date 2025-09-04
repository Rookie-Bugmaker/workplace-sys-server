package com.crrc.sys.mapper.mongodb;

import com.crrc.sys.entity.parameter.GlobalSettingValue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GlobalSettingValueRepository extends MongoRepository<GlobalSettingValue, String> {
    boolean existsBySettingId(String settingId);
    List<GlobalSettingValue> findAllBySettingId(String settingId);

    @Query("{'setting_id': ?0, 'item_results': {$elemMatch: {'settingItemId': ?1, 'settingItemContent': ?2}}}")
    List<GlobalSettingValue> findBySettingIdAndItemResults(String settingId, String settingItemId, Object settingItemContent);

    @Query("{'setting_id': ?0, 'item_results': {$elemMatch: {'settingItemId': ?1, 'settingItemContent': {$regex: ?2, $options: 'i'}}}}")
    List<GlobalSettingValue> findBySettingIdAndItemResultsWithRegex(
            String settingId,
            String settingItemId,
            String regexPattern);
}