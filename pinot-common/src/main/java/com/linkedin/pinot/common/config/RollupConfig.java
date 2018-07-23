package com.linkedin.pinot.common.config;

import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RollupConfig {

  @ConfigKey("preAggregateType")
  @UseChildKeyHandler(SimpleMapChildKeyHandler.class)
  public Map<String, String> _preAggregateType;

  @ConfigKey("multiLevelRollupSettings")
  List<MultiLevelRollupSetting> _multiLevelRollupSettings;

  public Map<String, String> getPreAggregateType() {
    return _preAggregateType;
  }

  public void setPreAggregateType(Map<String, String> preAggregateType) {
    _preAggregateType = preAggregateType;
  }

  public List<MultiLevelRollupSetting> getMultiLevelRollupSettings() {
    return _multiLevelRollupSettings;
  }

  public void setMultiLevelRollupSettings(List<MultiLevelRollupSetting> multiLevelRollupSettings) {
    _multiLevelRollupSettings = multiLevelRollupSettings;
  }

}
