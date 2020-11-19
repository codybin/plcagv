/**
 * Copyright (c) Fraunhofer IML
 */
package example.virtualvehicle;

import static com.google.common.base.Ascii.ETX;
import static com.google.common.base.Ascii.STX;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.primitives.Ints;

import static java.util.Objects.requireNonNull;

/**
 * Represents an order response sent from the vehicle.
 *代表一个来自车辆的命令响应
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class OrderResponse
    extends Response {

  /**
   * The response type.
   * 相应类型
   */
  public static final byte TYPE = 2;
  /**
   * The expected length of a telegram of this type.
   * 这个报文期望的长度
   */
  public static final int TELEGRAM_LENGTH = 9;
  /**
   * The size of the payload (the raw content, without STX, SIZE, CHECKSUM and ETX).
   * 有效数据的尺寸
   */
  public static final int PAYLOAD_LENGTH = TELEGRAM_LENGTH - 4;
  /**
   * The position of the checksum byte.
   * 和校验的位置
   */
  public static final int CHECKSUM_POS = TELEGRAM_LENGTH - 2;
  /**
   * The order id received by the vehicle.
   * 被车辆接收的订单id
   */
  private int orderId;

  /**
   * Creates a new instance.
   *创建新的实例
   * @param telegramData This telegram's raw content.
   */
  public OrderResponse(byte[] telegramData) {
    super(TELEGRAM_LENGTH);
    requireNonNull(telegramData, "telegramData");
    checkArgument(telegramData.length == TELEGRAM_LENGTH);

    System.arraycopy(telegramData, 0, rawContent, 0, TELEGRAM_LENGTH);
    decodeTelegramContent();
  }

  /**
   * Returns the order id received by the vehicle.
   *返回被车辆接收的订单id
   * @return The order id received by the vehicle.
   */
  public int getOrderId() {
    return orderId;
  }

  @Override
  public String toString() {
    return "OrderResponse{" + "id=" + id + '}';
  }

  /**
   * Checks if the given byte array is an order reponse telegram.
   *检查是否被给定的字节是一个订单回应的报文
   * @param telegramData The telegram data to check.
   * @return {@code true} if, and only if, the given data is an order response telegram.
   */
  public static boolean isOrderResponse(byte[] telegramData) {
    requireNonNull(telegramData, "telegramData");

    boolean result = true;
    if (telegramData.length != TELEGRAM_LENGTH) {
      result = false;
    }
    else if (telegramData[0] != STX) {
      result = false;
    }
    else if (telegramData[TELEGRAM_LENGTH - 1] != ETX) {
      result = false;
    }
    else if (telegramData[1] != PAYLOAD_LENGTH) {
      result = false;
    }
    else if (telegramData[2] != TYPE) {
      result = false;
    }
    else if (getCheckSum(telegramData) != telegramData[CHECKSUM_POS]) {
      result = false;
    }
    return result;
  }

  private void decodeTelegramContent() {
    this.id = Ints.fromBytes((byte) 0, (byte) 0, rawContent[3], rawContent[4]);
    orderId = Ints.fromBytes((byte) 0, (byte) 0, rawContent[5], rawContent[6]);
  }
}
