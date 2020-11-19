/**
 * Copyright (c) Fraunhofer IML
 */
package example.virtualvehicle;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;

/**
 * A response represents an answer of a vehicle control to a request sent by the control system.
 *一个响应，该响应是车辆对控制系统发送的需求的回答
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public abstract class Response
    extends Telegram {
  
  /**
   * Creates a new instance.
   *创建新的实例
   * @param telegramLength The response's length.
   */
  public Response(int telegramLength) {
    super(telegramLength);
  }

  /**
   * Checks whether this is a response to the given request.
   * 核查是否这是一个对给定的需求的一个响应
   * <p>
   * This implementation only checks for matching telegram ids.
   * Subclasses may want to extend this check.
   * </p>
   *
   * @param request The request to check with.
   * @return {@code true} if, and only if, the given request's id matches this response's id.
   */
  public boolean isResponseTo(@Nonnull Request request) {
    requireNonNull(request, "request");
    return request.getId() == getId();
  }
}
