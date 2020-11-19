/**
 * Copyright (c) Fraunhofer IML
 */
package example.virtualvehicle;;

/**
 * A request represents a telegram sent from the control system to vehicle control and expects
 * a response with the same id to match.
 * 一个需求代表一个报文，控制系统发送该报文给车辆控制，并且控制系统希望返回一个相同id的回应
 *从控制系统到车辆的报文
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public abstract class Request
    extends Telegram {

  /**
   * Creates a new instance.
   *
   * @param telegramLength The request's length.
   */
  public Request(int telegramLength) {
    super(telegramLength);
  }

  /**
   * Updates the content of the request to include the given id.
   *更新需求的内容
   * @param telegramId The request's new id.
   */
  public abstract void updateRequestContent(int telegramId);
}
