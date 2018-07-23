package com.linkedin.pinot.common.config;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MultiLevelRollupSetting {

  @ConfigKey("rollupFrom")
  public String _rollupFrom;

  @ConfigKey("rollupTo")
  public String _rollupTo;

  @ConfigKey("minNumSegments")
  public int _minNumSegments;

  @ConfigKey("minNumTotalDocs")
  public int _minNumTotalDocs;

  public String getRollupFrom() {
    return _rollupFrom;
  }

  public void setRollupFrom(String rollupFrom) {
    _rollupFrom = rollupFrom;
  }

  public String getRollupTo() {
    return _rollupTo;
  }

  public void setRollupTo(String rollupTo) {
    _rollupTo = rollupTo;
  }

  public int getMinNumSegments() {
    return _minNumSegments;
  }

  public void setMinNumSegments(int minNumSegments) {
    _minNumSegments = minNumSegments;
  }

  public int getMinNumTotalDocs() {
    return _minNumTotalDocs;
  }

  public void setMinNumTotalDocs(int minNumTotalDocs) {
    _minNumTotalDocs = minNumTotalDocs;
  }
}


