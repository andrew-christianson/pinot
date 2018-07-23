/**
 * Copyright (C) 2014-2018 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.core.minion;

import com.linkedin.pinot.common.config.IndexingConfig;
import com.linkedin.pinot.common.config.SegmentMergeConfig;
import com.linkedin.pinot.common.data.DimensionFieldSpec;
import com.linkedin.pinot.common.data.Schema;
import com.linkedin.pinot.common.exception.InvalidConfigException;
import com.linkedin.pinot.common.segment.SegmentMetadata;
import com.linkedin.pinot.core.minion.rollup.RollupRecordAggregator;
import com.linkedin.pinot.core.minion.rollup.RollupRecordTransformer;
import com.linkedin.pinot.core.segment.index.SegmentMetadataImpl;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class RollupSegmentConverter {
  private List<File> _inputIndexDirs;
  private File _workingDir;
  private SegmentMergeConfig _segmentMergeConfig;
  private IndexingConfig _indexingConfig;

  public RollupSegmentConverter(@Nonnull List<File> inputIndexDirs, @Nonnull File workingDir, @Nonnull
      SegmentMergeConfig segmentMergeConfig, @Nullable IndexingConfig indexingConfig) {
    _inputIndexDirs = inputIndexDirs;
    _workingDir = workingDir;
    _segmentMergeConfig = segmentMergeConfig;
    _indexingConfig = indexingConfig;
  }

  public List<File> convert() throws Exception {
    // Fetch table name and schema from segment metadata
    List<SegmentMetadata> segmentMetadataList = new ArrayList<>();

    String tableName = null;
    Schema schema = null;
    for (File inputIndexDir : _inputIndexDirs) {
      SegmentMetadata segmentMetadata = new SegmentMetadataImpl(inputIndexDir);
      segmentMetadataList.add(segmentMetadata);
      if (tableName == null) {
        tableName = segmentMetadata.getTableName();
      } else if (!tableName.equals(segmentMetadata.getTableName())) {
        throw new InvalidConfigException("Table name has to be the same for all segments");
      }

      if (schema == null) {
        schema = segmentMetadata.getSchema();
      } else if (!schema.equals(segmentMetadata.getSchema())) {
        throw new InvalidConfigException("Schema has to be the same for all segments");
      }
    }

    // Compute segment name for merged/rolled up segment
    String segmentName = "merged";

    // Get the list of dimensions from schema
    List<String> groupByColumns = new ArrayList<>();
    for (DimensionFieldSpec dimensionFieldSpec: schema.getDimensionFieldSpecs()) {
      groupByColumns.add(dimensionFieldSpec.getName());
    }
    groupByColumns.add(schema.getTimeColumnName());

    // Initialize roll-up record transformer
    RollupRecordTransformer rollupRecordTransformer = new RollupRecordTransformer(schema.getTimeColumnName());

    // Initialize roll-up record aggregator
    RollupRecordAggregator rollupRecordAggregator = new RollupRecordAggregator(schema,
        _segmentMergeConfig.getRollupConfig().getPreAggregateType());

    // Create segment converter
    SegmentConverter segmentConverter = new SegmentConverter.Builder()
        .setTableName(tableName)
        .setSegmentName("segmentConcatenate")
        .setInputIndexDirs(_inputIndexDirs)
        .setWorkingDir(_workingDir)
        .setRecordTransformer(rollupRecordTransformer)
        .setRecordAggregator(rollupRecordAggregator)
        .setGroupByColumns(groupByColumns)
        .setTotalNumPartition(1)
        .setIndexingConfig(_indexingConfig)
        .build();

    List<File> convertedSegments = segmentConverter.convertSegment();

    return convertedSegments;
  }
}
