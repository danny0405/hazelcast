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

package com.hazelcast.sql.impl.mailbox;

import com.hazelcast.sql.impl.QueryId;

/**
 * Inbox identifier.
 */
public class InboxKey {
    /** Query ID. */
    private final QueryId queryId;

    /** Edge ID. */
    private final int edgeId;

    /** Stripe. */
    private final int stripe;

    public InboxKey(QueryId queryId, int edgeId, int stripe) {
        this.queryId = queryId;
        this.edgeId = edgeId;
        this.stripe = stripe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        InboxKey other = (InboxKey)o;

        return queryId.equals(other.queryId) && edgeId == other.edgeId && stripe == other.stripe;
    }

    @Override
    public int hashCode() {
        int result = queryId.hashCode();

        result = 31 * result + edgeId;
        result = 31 * result + stripe;

        return result;
    }
}
