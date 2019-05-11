/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.flinkx.stream.reader;

import com.dtstack.flinkx.inputformat.RichInputFormat;
import com.dtstack.flinkx.reader.MetaColumn;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.io.GenericInputSplit;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.types.Row;

import java.io.IOException;
import java.util.List;

/**
 * @Company: www.dtstack.com
 * @author jiangbo
 */
public class StreamInputFormat extends RichInputFormat {

    protected static final long serialVersionUID = 1L;

    private long recordRead = 0;

    protected long sliceRecordCount;

    protected List<MetaColumn> columns;

    @Override
    public void openInternal(InputSplit inputSplit) throws IOException {
        if (restoreConfig.isRestore() && formatState != null){
            recordRead = (Long)formatState.getState();
        }
    }

    @Override
    public Row nextRecordInternal(Row row) throws IOException {
        row = MockDataUtil.getMockRow(columns);
        if (restoreConfig.isRestore() && columns.get(0).getValue() == null){
            row.setField(0, recordRead);
        }

        return row;
    }

    @Override
    public boolean reachedEnd() throws IOException {
        return ++recordRead > sliceRecordCount && sliceRecordCount > 0;
    }

    @Override
    protected void closeInternal() throws IOException {
    }

    @Override
    public void configure(Configuration parameters) {

    }

    @Override
    public InputSplit[] createInputSplits(int minNumSplits) throws IOException {
        InputSplit[] inputSplits = new InputSplit[minNumSplits];
        for (int i = 0; i < minNumSplits; i++) {
            inputSplits[i] = new GenericInputSplit(i,minNumSplits);
        }

        return inputSplits;
    }
}
