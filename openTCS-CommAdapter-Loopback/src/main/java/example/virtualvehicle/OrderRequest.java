/**
 * Copyright (c) Fraunhofer IML
 */
package example.virtualvehicle;

import static com.google.common.base.Ascii.ETX;
import static com.google.common.base.Ascii.STX;
import com.google.common.primitives.Ints;

import static java.util.Objects.requireNonNull;

/**
 * Represents an order request addressed to the vehicle.
 *代表一个派送给车辆的订单需求
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class OrderRequest
    extends Request {

  /**
   * The request type.
   * 需求类型
   */
  public static final byte TYPE = 2;
  /**
   * The expected length of a telegram of this type.
   * 这个类型的报文的程度
   */
  public static final int TELEGRAM_LENGTH = 12;
  /**
   * The size of the payload (the raw content, without STX, SIZE, CHECKSUM and ETX).
   * 有效数据的长度
   */
  public static final int PAYLOAD_LENGTH = TELEGRAM_LENGTH - 4;
  /**
   * 和校验的位置
   */
  public static final int CHECKSUM_POS = TELEGRAM_LENGTH - 2;
  /**
   * 订单的id
   */
  private final int orderId;
  /**
   * 目的地的名字
   */
  private final int destinationId;
  /**
   * 在目的地要执行的动作
   */
  private final OrderAction destinationAction;

  /**
   * 创建新的实例
   *
   * @param requestId The request's id.
   * @param orderId The order id.
   * @param destinationId The name of the destination point.
   * @param destinationAction The action to execute at the destination point.
   */
  public OrderRequest(int requestId,
                      int orderId,
                      int destinationId,
                      OrderAction destinationAction) {
    super(TELEGRAM_LENGTH);
    this.id = requestId;
    this.orderId = orderId;
    this.destinationId = destinationId;
    this.destinationAction = requireNonNull(destinationAction, "destinationAction");

    encodeTelegramContent(orderId, destinationId, destinationAction);
  }

  /**
   * Returns this order request's order id.
   *返回这个需求的id
   * @return This order request's order id.
   */
  public int getOrderId() {
    return orderId;
  }

  /**
   * Returns this order request's destination name.
   *返回这个需求的目的名字
   * @return This order request's destination name.
   */
  public int getDestinationId() {
    return destinationId;
  }

  /**
   * Returns this order request's destination action.
   *返回这个需求的目的地动作
   * @return This order request's destination action.
   */
  public OrderAction getDestinationAction() {
    return destinationAction;
  }

  @Override
  public String toString() {
    return "OrderRequest{"
        + "requestId=" + id + ", "
        + "orderId=" + orderId + ", "
        + "destinationId=" + destinationId + ", "
        + "destinationAction=" + destinationAction + '}';
  }

  @Override
  public void updateRequestContent(int requestId) {
    id = requestId;
    encodeTelegramContent(orderId, destinationId, destinationAction);
  }

  /**
   * Encodes this telegram's content into the raw content byte array.
   * 编码这个报文的内容到原始字节序列的组合
   * @param orderId The order id
   * @param destinationId The destination name
   * @param destinationAction The destination action
   */
  private void encodeTelegramContent(int orderId,
                                     int destinationId,
                                     OrderAction destinationAction) {
    // Start of each telegram
    rawContent[0] = STX;
    rawContent[1] = PAYLOAD_LENGTH;

    // Payload of the telegram
    rawContent[2] = TYPE;

    byte[] tmpWord = Ints.toByteArray(id);
    rawContent[3] = tmpWord[2];
    rawContent[4] = tmpWord[3];

    tmpWord = Ints.toByteArray(orderId);
    rawContent[5] = tmpWord[2];
    rawContent[6] = tmpWord[3];

    tmpWord = Ints.toByteArray(destinationId);
    rawContent[7] = tmpWord[2];
    rawContent[8] = tmpWord[3];

    rawContent[9] = destinationAction.getActionByte();

    // End of each telegram
    rawContent[CHECKSUM_POS] = getCheckSum(rawContent);
    rawContent[TELEGRAM_LENGTH - 1] = ETX;
  }

  /**
   * Defines all actions that a vehicle can execute as part of an order.
   * 定义一个车辆可以执行的所有的命令
   */
  public enum OrderAction {
    /**
     * No action.
     * 没有动作
     */
    NONE('N'),
    /**
     * Action to load an object.
     * 负载一个物体的动作
     */
    LOAD('L'),
    /**
     * Action to unload an object.
     * 卸载一个物体的动作
     */
    UNLOAD('U'),
    /**
     * Charge vehicle.
     * 给车辆充电
     */
    CHARGE('C');

    /**
     * The actual byte to put into the telegram to the vehicle.
     * 被放到发送给车辆报文的真实字节
     */
    private final byte actionByte;

    /**
     * Creates a new Action.
     *创建新的订单
     * @param action The actual byte to put into the telegram to the vehicle.
     */
    OrderAction(char action) {
      this.actionByte = (byte) action;
    }

    /**
     * Returns the actual byte to put into the telegram to the vehicle.
     *返回真实被放到车辆报文的字节数据
     * @return The actual byte to put into the telegram to the vehicle.
     */
    public byte getActionByte() {
      return actionByte;
    }

    /**
     * Maps the given {@code actionString} to an order action.
     *把给定的动作映射到一个订单动作
     * @param actionString
     * @return The action associated with the {@code actionString}.
     * Returns {@link #NONE} if there isn't any action associated with the {@code actionString}.
     */
    public static OrderAction stringToAction(String actionString) {
      OrderAction action = NONE;
      if (actionString.equals(LoadAction.LOAD)) {
        action = LOAD;
      }
      if (actionString.equals(LoadAction.UNLOAD)) {
        action = UNLOAD;
      }
      if (actionString.equals(LoadAction.CHARGE)) {
        action = CHARGE;
      }
      return action;
    }
  }

}
