package org.apache.hadoop.util;

import java.nio.ByteBuffer;
import java.util.zip.Checksum;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class PureJavaCrc32C implements Checksum {
  private int crc = 0xFFFFFFFF;
  private static final int[] CRC32C_TABLE = new int[256];
  static {
    for (int i = 0; i < 256; i++) {
      int crc = i;
      for (int j = 0; j < 8; j++) {
        if ((crc & 1) != 0) {
          crc = 0x82F63B78 ^ (crc >>> 1);
        } else {
          crc = crc >>> 1;
        }
      }
      CRC32C_TABLE[i] = crc;
    }
  }

  public PureJavaCrc32C() {}

  public void update(int b) {
    crc = CRC32C_TABLE[(crc ^ (b & 0xFF)) & 0xFF] ^ (crc >>> 8);
  }

  public void update(byte[] b) {
    update(b, 0, b.length);
  }

  public void update(byte[] b, int off, int len) {
    int end = off + len;
    for (int i = off; i < end; i++) {
      crc = CRC32C_TABLE[(crc ^ (b[i] & 0xFF)) & 0xFF] ^ (crc >>> 8);
    }
  }

  public void update(ByteBuffer buffer) {
    if (buffer.hasArray()) {
      int pos = buffer.arrayOffset() + buffer.position();
      update(buffer.array(), pos, buffer.remaining());
      buffer.position(buffer.position() + buffer.remaining());
    } else {
      byte[] b = new byte[buffer.remaining()];
      buffer.get(b);
      update(b);
    }
  }

  public long getValue() {
    return (~crc) & 0xFFFFFFFFL;
  }

  public void reset() {
    crc = 0xFFFFFFFF;
  }

  public static int mod(long value) {
    return (int) (value & 0xFFFFFFFF);
  }
}
