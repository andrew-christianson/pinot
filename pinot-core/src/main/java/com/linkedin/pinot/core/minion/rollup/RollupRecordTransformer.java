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
package com.linkedin.pinot.core.minion.rollup;

import com.linkedin.pinot.core.data.GenericRow;
import com.linkedin.pinot.core.minion.segment.RecordTransformer;
import com.linkedin.pinot.core.operator.transform.transformer.datetime.BaseDateTimeTransformer;
import com.linkedin.pinot.core.operator.transform.transformer.datetime.DateTimeTransformerFactory;


public class RollupRecordTransformer implements RecordTransformer {

  private BaseDateTimeTransformer _dateTimeTransformer;
  private String _timeColumn;

  public RollupRecordTransformer(String timeColumn) {
    _timeColumn = timeColumn;
    _dateTimeTransformer =
        DateTimeTransformerFactory.getDateTimeTransformer("1:MILLISECONDS:EPOCH", "1:DAYS:EPOCH", "1:DAYS");
  }

  @Override
  public GenericRow transformRecord(GenericRow row) {
    long[] input = new long[1];
    long[] output = new long[1];
    input[0] = (Long) row.getValue(_timeColumn);
    _dateTimeTransformer.transform(input, output, 1);
    row.putField(_timeColumn, output[0]);
    return row;
  }
}
