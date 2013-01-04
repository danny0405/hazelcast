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

package com.hazelcast.nio;

import com.hazelcast.nio.serialization.Data;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class IOUtil {

    public static void writeByteArray(ObjectDataOutput out, byte[] value) throws IOException {
        int size = (value == null) ? 0 : value.length;
        out.writeInt(size);
        if (size > 0) {
            out.write(value);
        }
    }

    public static byte[] readByteArray(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        if (size == 0) {
            return null;
        } else {
            byte[] b = new byte[size];
            in.readFully(b);
            return b;
        }
    }

    public static void writeNullableObject(ObjectDataOutput out, Object obj) throws IOException {
        boolean NULL = obj == null;
        out.writeBoolean(NULL);
        if (!NULL) {
            out.writeObject(obj);
        }
    }

    public static <T> T readNullableObject(ObjectDataInput in) throws IOException {
        boolean NULL = in.readBoolean();
        if (!NULL) {
            return in.readObject();
        }
        return null;
    }

    public static void writeNullableString(ObjectDataOutput out, String obj) throws IOException {
        boolean NULL = obj == null;
        out.writeBoolean(NULL);
        if (!NULL) {
            out.writeUTF(obj);
        }
    }

    public static String readNullableString(ObjectDataInput in) throws IOException {
        boolean NULL = in.readBoolean();
        if (!NULL) {
            return in.readUTF();
        }
        return null;
    }

    public static void writeNullableData(ObjectDataOutput out, Data data) throws IOException {
        if (data != null) {
            out.writeBoolean(true);
            data.writeData(out);
        } else {
            // null
            out.writeBoolean(false);
        }
    }

    public static Data readNullableData(ObjectDataInput in) throws IOException {
        final boolean isNotNull = in.readBoolean();
        if (isNotNull) {
            Data data = new Data();
            data.readData(in);
            return data;
        }
        return null;
    }

//    public static byte[] toByteArray(Object obj) {
//        if (obj == null) {
//            return null;
//        }
//        if (obj instanceof byte[]) {
//            return (byte[]) obj;
//        }
//        return ThreadContext.get().toByteArray(obj);
//    }
//
//    public static Data toData(Object obj) {
//        if (obj == null) {
//            return null;
//        }
//        if (obj instanceof Data) {
//            return (Data) obj;
//        }
//        return ThreadContext.get().toData(obj);
//    }
//
//    public static <T> T toObject(byte[] data) {
//        return (T) ThreadContext.get().toObject(data);
//    }
//
//    public static <T> T toObject(Data data) {
//        if (data == null) {
//            return null;
//        }
//        return (T) ThreadContext.get().toObject(data);
//    }
//
//    public static Object toObject(Object data) {
//        if (data == null) {
//            return null;
//        }
//        if (data instanceof Data) {
//            return ThreadContext.get().toObject((Data) data);
//        }
//        return data;
//    }
//
//    public static <T> T toObject(DataHolder dataHolder) {
//        return toObject(dataHolder.toData());
//    }

    public static ObjectInputStream newObjectInputStream(final InputStream in) throws IOException {
        return new ObjectInputStream(in) {
            protected Class<?> resolveClass(final ObjectStreamClass desc) throws ClassNotFoundException {
                return ClassLoaderUtil.loadClass(desc.getName());
            }
        };
    }

    public static OutputStream newOutputStream(final ByteBuffer buf) {
        return new OutputStream() {
            public void write(int b) throws IOException {
                buf.put((byte) b);
            }

            public void write(byte[] bytes, int off, int len) throws IOException {
                buf.put(bytes, off, len);
            }
        };
    }

    public static InputStream newInputStream(final ByteBuffer buf) {
        return new InputStream() {
            public int read() throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                return buf.get() & 0xff;
            }

            public int read(byte[] bytes, int off, int len) throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                len = Math.min(len, buf.remaining());
                buf.get(bytes, off, len);
                return len;
            }
        };
    }

    public static int copyToHeapBuffer(ByteBuffer src, ByteBuffer dest) {
        if (src == null) return 0;
        int n = Math.min(src.remaining(), dest.remaining());
        if (n > 0) {
            if (n < 16) {
                for (int i = 0; i < n; i++) {
                    dest.put(src.get());
                }
            } else {
                int srcPosition = src.position();
                int destPosition = dest.position();
                System.arraycopy(src.array(), srcPosition, dest.array(), destPosition, n);
                src.position(srcPosition + n);
                dest.position(destPosition + n);
            }
        }
        return n;
    }

    public static int copyToDirectBuffer(ByteBuffer src, ByteBuffer dest) {
        int n = Math.min(src.remaining(), dest.remaining());
        if (n > 0) {
            dest.put(src.array(), src.position(), n);
            src.position(src.position() + n);
        }
        return n;
    }

    public static int copyFromDirectToDirectBuffer(ByteBuffer src, ByteBuffer dest) {
        int n = Math.min(src.remaining(), dest.remaining());
        if (src.remaining() <= n) {
            dest.put(src);
        } else {
            int realLimit = src.limit();
            src.limit(src.position() + n);
            dest.put(src);
            src.limit(realLimit);
        }
        return n;
    }

    public static void writeLongString(DataOutput dos, String str) throws IOException {
        int chunk = 1000;
        int count = str.length() / chunk;
        int remaining = str.length() - (count * chunk);
        dos.writeInt(count + ((remaining > 0) ? 1 : 0));
        for (int i = 0; i < count; i++) {
            dos.writeUTF(str.substring(i * chunk, (i + 1) * chunk));
        }
        if (remaining > 0) {
            dos.writeUTF(str.substring(count * chunk));
        }
    }

    public static String readLongString(DataInput in) throws IOException {
        int count = in.readInt();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(in.readUTF());
        }
        return sb.toString();
    }

//    public static Data addDelta(Data longData, long delta) {
//        long longValue = (Long) toObject(longData);
//        return toData(longValue + delta);
//    }
//
//    public static Data addDelta(Data intData, int delta) {
//        int intValue = (Integer) toObject(intData);
//        return toData(intValue + delta);
//    }

    public static byte[] compress(byte[] input) throws IOException {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_SPEED);
        compressor.setInput(input);
        compressor.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length / 10);
        byte[] buf = new byte[input.length / 10];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        bos.close();
        compressor.end();
        return bos.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
        byte[] buf = new byte[1024];
        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
            }
        }
        bos.close();
        inflater.end();
        return bos.toByteArray();
    }

    public static void closeResource(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
