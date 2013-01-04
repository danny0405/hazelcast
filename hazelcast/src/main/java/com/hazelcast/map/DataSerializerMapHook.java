/*
 * Copyright (c) 2008-2012, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.map;

import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.DataSerializerHook;

import java.util.HashMap;
import java.util.Map;

/**
 * @mdogan 8/24/12
 */
public final class DataSerializerMapHook implements DataSerializerHook {

    static final int PUT = 100;
    static final int GET = 101;
    static final int REMOVE = 102;
    static final int GENERIC_BACKUP = 110;

    public Map<Integer, DataSerializableFactory> getFactories() {
        final Map<Integer, DataSerializableFactory> factories = new HashMap<Integer, DataSerializableFactory>();

        factories.put(PUT, new DataSerializableFactory() {
            public DataSerializable create() {
                return new PutOperation();
            }
        });
        factories.put(GET, new DataSerializableFactory() {
            public DataSerializable create() {
                return new GetOperation();
            }
        });
        factories.put(REMOVE, new DataSerializableFactory() {
            public DataSerializable create() {
                return new RemoveOperation();
            }
        });
        factories.put(GENERIC_BACKUP, new DataSerializableFactory() {
            public DataSerializable create() {
                return new GenericBackupOperation();
            }
        });

        return factories;
    }
}
