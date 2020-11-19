/**
 * Copyright (c) Fraunhofer IML
 */
package example.virtualvehicle;;

/**
 * Declares methods for comm adapters capable of sending telegrams/requests.
 *声明的方法，适配器发送的方法。
 * @author Martin Grzenia (Fraunhofer IML)
 */
public interface TelegramSender {

  /**
   * Sends the given {@link Request}.
   *
   * @param request The {@link Request} to be sent.
   */
  void sendTelegram(Request request);
}
