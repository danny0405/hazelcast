/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.sql.impl.exec;

import com.hazelcast.sql.impl.QueryContext;
import com.hazelcast.sql.impl.row.EmptyRowBatch;
import com.hazelcast.sql.impl.row.RowBatch;
import com.hazelcast.sql.impl.worker.data.DataWorker;

/**
 * Scan over an empty result-set.
 */
public class EmptyScanExec implements Exec {
    /** Singleton instance. */
    public static EmptyScanExec INSTANCE = new EmptyScanExec();

    private EmptyScanExec() {
        // No-op.
    }

    @Override
    public void setup(QueryContext ctx, DataWorker worker) {
        // No-op.
    }

    @Override
    public IterationResult advance() {
        return IterationResult.FETCHED_DONE;
    }

    @Override
    public RowBatch currentBatch() {
        return EmptyRowBatch.INSTANCE;
    }
}
