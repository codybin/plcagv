/**
 * Copyright (c) Fraunhofer IML
 */
package example.virtualvehicle;

import java.io.Serializable;
import static java.util.Objects.requireNonNull;

/**
 * The base class for all telegram types used for communication with the vehicle.
 *用来和车通讯的报文的基本类
 * @author Stefan Walter (Fraunhofer IML)
 */
public abstract class Telegram
    implements Serializable {

  /**
   * The default value for a telegram's id.
   * 报文默认的报文id
   */
  public static final int ID_DEFAULT = 0;
  /**
   * The telegram's raw content as sent via the network.
   * 通过网络发送的报文的原始内容
   */
  protected final byte[] rawContent;
  /**
   * The identifier for a specific telegram instance.
   */
  protected int id;

  /**
   * Creates a new instance.
   *
   * @param telegramLength The telegram's length
   */
  public Telegram(int telegramLength) {
    this.rawContent = new byte[telegramLength];
  }

  /**
   * Returns this telegram's actual raw content.
   *返回报文的真实的原始内容
   * @return This telegram's actual raw content.
   */
  public byte[] getRawContent() {
    return rawContent;
  }

  /**
   * Returns the identifier for this specific telegram instance.
   *返回这个指定的报文实例的id
   * @return The identifier for this specific telegram instance.
   */
  public int getId() {
    return id;
  }

  // tag::documentation_checksumComp[]
  /**
   * Computes a checksum for the given raw content of a telegram.
   *计算对于给定的原始内容的一个和校验
   * @param rawContent A telegram's raw content.
   * @return The checksum computed for the given raw content.
   */
  public static byte getCheckSum(byte[] rawContent) {
    requireNonNull(rawContent, "rawContent");

    int cs = 0;
    for (int i = 0; i < rawContent[1]; i++) {
      cs ^= rawContent[2 + i];
    }
    return (byte) cs;
  }
  // end::documentation_checksumComp[]
}
