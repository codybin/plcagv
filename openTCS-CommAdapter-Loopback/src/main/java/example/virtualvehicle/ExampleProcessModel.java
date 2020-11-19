/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.virtualvehicle;


import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import static org.opentcs.util.Assertions.checkInRange;

/**
 * A custom model for the {@link ExampleCommAdapter} which holds additional information
 * about the connected vehicle.
 *一个用户模型，该模型保持了关于车辆连接的信息
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class ExampleProcessModel
    extends VehicleProcessModel {

  /**
   * The current/most recent state reported by the vehicle.
   * 当前的或者被车报告的最近的状态
   */
  private StateResponse currentState;
  /**
   * The previous state reported by the vehicle.
   * 被车辆报告的先前的状态
   */
  private StateResponse previousState;
  /**
   * The last order telegram sent to the vehicle.
   * 发送给车辆的最后的报文
   */
  private OrderRequest lastOrderSent;
  /**
   * The host to connect to.
   * 要连接的主机
   */
  private String vehicleHost = "127.0.0.1";
  /**
   * The port to connect to.
   * 要连接的主机端口
   */
  private int vehiclePort = 2000;
  /**
   * A flag indicating whether periodic sending of {@link StateRequest} telegrams is enabled.
   * 标志是否周期性发送报文是可能的。
   */
  private boolean periodicStateRequestEnabled = true;
  /**
   * The time to wait between periodic state request telegrams.
   * 在周期性状态报文的时间间隔
   */
  private int stateRequestInterval = 500;
  /**
   * How long (in ms) we tolerate not hearing from the vehicle before we consider communication
   * dead.
   * 在我们确认车辆断开连接之前我们要监听多久。
   */
  private int vehicleIdleTimeout = 5000;
  /**
   * Indicates whether the vehicle has not been heard of recently.
   * 暗示是否车辆最近没有被监听
   */
  private boolean vehicleIdle = true;
  /**
   * Whether to close the connection if the vehicle is considered dead.
   * 如果车辆断开连接我们是否断开连接
   */
  private boolean disconnectingOnVehicleIdle = true;
  /**
   * Whether to reconnect automatically when the vehicle connection times out.
   * 当车连接 超时，我们是否重新连接
   */
  private boolean reconnectingOnConnectionLoss = true;
  /**
   * The delay before reconnecting (in ms).
   * 重连的时间
   */
  private int reconnectDelay = 10000;
  /**
   * Whether logging should be enabled or not.
   * 是否记录应该被使能
   */
  private boolean loggingEnabled = false;

  /**
   * Creates a new instance.
   * 创建新的实例
   *
   * @param attachedVehicle The attached vehicle
   */
  public ExampleProcessModel(Vehicle attachedVehicle) {
    super(attachedVehicle);
    // Initialize the state fields
    final byte[] dummyData = new byte[StateResponse.TELEGRAM_LENGTH];
    previousState = new StateResponse(dummyData);
    currentState = new StateResponse(dummyData);
  }

  @Nonnull
  public StateResponse getCurrentState() {
    return currentState;
  }

  public void setCurrentState(@Nonnull StateResponse currentState) {
    StateResponse oldValue = this.currentState;
    this.currentState = requireNonNull(currentState, "currentState");

    getPropertyChangeSupport().firePropertyChange(Attribute.CURRENT_STATE.name(),
                                                  oldValue,
                                                  currentState);
  }

  @Nonnull
  public StateResponse getPreviousState() {
    return previousState;
  }

  public void setPreviousState(@Nonnull StateResponse previousState) {
    StateResponse oldValue = this.previousState;
    this.previousState = requireNonNull(previousState, "previousState");

    getPropertyChangeSupport().firePropertyChange(Attribute.PREVIOUS_STATE.name(),
                                                  oldValue,
                                                  previousState);
  }

  /**
   * Returns the last order telegram sent to the vehicle.
   *返回最后的发送给车辆的报文
   * @return The last order telegram sent to the vehicle, or <code>null</code>, if no order has been
   * sent to the vehicle, yet.
   */
  @Nullable
  public synchronized OrderRequest getLastOrderSent() {
    return lastOrderSent;
  }

  public synchronized void setLastOrderSent(@Nullable OrderRequest telegram) {
    OrderRequest oldValue = this.lastOrderSent;
    lastOrderSent = telegram;

    getPropertyChangeSupport().firePropertyChange(Attribute.LAST_ORDER.name(),
                                                  oldValue,
                                                  telegram);
  }

  /**
   * Returns the vehicle's host name/IP address.
   *返回车辆的主机名字和ip
   * @return The vehicle's host name/IP address.
   */
  @Nonnull
  public synchronized String getVehicleHost() {
    return vehicleHost;
  }

  /**
   * Sets the vehicle's host name/IP address.
   *设置车辆的主机名字/ip地址
   * @param vehicleHost The vehicle's host name/IP address.
   */
  public synchronized void setVehicleHost(@Nonnull String vehicleHost) {
    String oldValue = this.vehicleHost;
    this.vehicleHost = requireNonNull(vehicleHost, "vehicleHost");

    getPropertyChangeSupport().firePropertyChange(Attribute.VEHICLE_HOST.name(),
                                                  oldValue,
                                                  vehicleHost);
  }

  /**
   * Returns the TCP port number the vehicle is listening on.
   *返回车辆正在监听的端口
   * @return The TCP port number the vehicle is listening on.
   */
  public synchronized int getVehiclePort() {
    return vehiclePort;
  }

  /**
   * Sets the TCP port number the vehicle is listening on.
   *设置车辆正在监听的端口号码
   * @param vehiclePort The TCP port number.
   */
  public synchronized void setVehiclePort(int vehiclePort) {
    int oldValue = this.vehiclePort;
    this.vehiclePort = checkInRange(vehiclePort, 1, 65535, "vehiclePort");

    getPropertyChangeSupport().firePropertyChange(Attribute.VEHICLE_PORT.name(),
                                                  oldValue,
                                                  vehiclePort);
  }

  /**
   * Indicates whether the communication adapter periodically sends state requests to the vehicle.
   *暗示是否车辆应该周期性的发送状态信息到车辆
   * @return <code>true</code> if, and only if, the communication adapter periodically sends state
   * requests to the vehicle.
   */
  public synchronized boolean isPeriodicStateRequestEnabled() {
    return periodicStateRequestEnabled;
  }

  /**
   * Turns periodic state requests on/off.
   *打开或者关闭周期性的状态 获取信息
   * @param enabled If <code>true</code>, periodic state requests are turned on, otherwise they are
   * turned off.
   */
  public synchronized void setPeriodicStateRequestEnabled(boolean enabled) {
    if (periodicStateRequestEnabled == enabled) {
      return;
    }
    boolean oldVal = periodicStateRequestEnabled;
    periodicStateRequestEnabled = enabled;
    getPropertyChangeSupport().firePropertyChange(Attribute.PERIODIC_STATE_REQUESTS_ENABLED.name(),
                                                  oldVal,
                                                  enabled);
  }

  /**
   * Returns the interval (in ms) between two state requests.
   *返回在两个状态之间的间隔
   * @return The interval (in ms) between two state requests.
   */
  public synchronized int getStateRequestInterval() {
    return stateRequestInterval;
  }

  /**
   * Sets the interval (in ms) between two state requests.
   *设置在两个状态之间的间隔
   * @param interval The interval to be set.
   */
  public synchronized void setStateRequestInterval(int interval) {
    checkArgument(interval >= 0, "interval invalid: %s", interval);
    if (stateRequestInterval == interval) {
      return;
    }
    int oldVal = stateRequestInterval;
    stateRequestInterval = interval;
    getPropertyChangeSupport().firePropertyChange(Attribute.PERIOD_STATE_REQUESTS_INTERVAL.name(),
                                                  oldVal,
                                                  interval);
  }

  /**
   * Returns how long (in ms) we tolerate not hearing from the vehicle before we consider
   * communication dead.
   *在我们考虑通讯中断之前我们可以容许多长时间
   * @return How long (in ms) we tolerate not hearing from the vehicle before we consider
   * communication dead
   */
  public int getVehicleIdleTimeout() {
    return vehicleIdleTimeout;
  }

  /**
   * Sets how long (in ms) we tolerate not hearing from the vehicle before we consider
   * communication dead
   *
   * @param idleTimeout How long (in ms) we tolerate not hearing from the vehicle before we consider
   * communication dead
   */
  public void setVehicleIdleTimeout(int idleTimeout) {
    int oldValue = this.vehicleIdleTimeout;
    this.vehicleIdleTimeout = checkInRange(idleTimeout, 1, Integer.MAX_VALUE, "idleTimeout");

    getPropertyChangeSupport().firePropertyChange(Attribute.VEHICLE_IDLE_TIMEOUT.name(),
                                                  oldValue,
                                                  idleTimeout);
  }

  /**
   * Returns whether the vehicle has not been heard of recently.
   *
   * @return Whether the vehicle has not been heard of recently.
   */
  public boolean isVehicleIdle() {
    return vehicleIdle;
  }

  /**
   * Sets whether the vehicle has not been heard of recently.
   *
   * @param idle Whether the vehicle has not been heard of recently.
   */
  public void setVehicleIdle(boolean idle) {
    boolean oldValue = this.vehicleIdle;
    this.vehicleIdle = idle;

    getPropertyChangeSupport().firePropertyChange(Attribute.VEHICLE_IDLE.name(),
                                                  oldValue,
                                                  idle);
  }

  /**
   * Returns whether to close the connection if the vehicle is considered dead.
   *返回是否在空闲状态我们应该关闭连接
   * @return Whether to close the connection if the vehicle is considered dead
   */
  public boolean isDisconnectingOnVehicleIdle() {
    return disconnectingOnVehicleIdle;
  }

  /**
   * Sets whether to close the connection if the vehicle is considered dead.
   *设置如果车辆断开，是否应该关闭连接。
   * @param disconnectingOnVehicleIdle Whether to close the connection if the vehicle is
   * considered dead
   */
  public void setDisconnectingOnVehicleIdle(boolean disconnectingOnVehicleIdle) {
    boolean oldValue = this.disconnectingOnVehicleIdle;
    this.disconnectingOnVehicleIdle = disconnectingOnVehicleIdle;

    getPropertyChangeSupport().firePropertyChange(Attribute.DISCONNECTING_ON_IDLE.name(),
                                                  oldValue,
                                                  disconnectingOnVehicleIdle);
  }

  /**
   * Returns whether to reconnect automatically when the vehicle connection times out.
   *
   * @return whether to reconnect automatically when the vehicle connection times out
   */
  public boolean isReconnectingOnConnectionLoss() {
    return reconnectingOnConnectionLoss;
  }

  /**
   * Sets whether to reconnect automatically when the vehicle connection times out
   *
   * @param reconnectingOnConnectionLoss whether to reconnect automatically when the vehicle
   * connection times out
   */
  public void setReconnectingOnConnectionLoss(boolean reconnectingOnConnectionLoss) {
    boolean oldValue = this.reconnectingOnConnectionLoss;
    this.reconnectingOnConnectionLoss = reconnectingOnConnectionLoss;

    getPropertyChangeSupport().firePropertyChange(Attribute.RECONNECTING_ON_CONNECTION_LOSS.name(),
                                                  oldValue,
                                                  reconnectingOnConnectionLoss);
  }

  /**
   * Returns the delay before reconnecting (in ms).
   *
   * @return The delay before reconnecting (in ms)
   */
  public int getReconnectDelay() {
    return reconnectDelay;
  }

  /**
   * Sets the delay before reconnecting (in ms).
   *
   * @param reconnectDelay The delay before reconnecting (in ms)
   */
  public void setReconnectDelay(int reconnectDelay) {
    int oldValue = this.reconnectDelay;
    this.reconnectDelay = checkInRange(reconnectDelay, 1, Integer.MAX_VALUE, "reconnectDelay");

    getPropertyChangeSupport().firePropertyChange(Attribute.RECONNECT_DELAY.name(),
                                                  oldValue,
                                                  reconnectDelay);
  }

  /**
   * Returns whether logging is enabled.
   *返回是否记录应该被使能
   * @return Whether logging is enabled
   */
  public boolean isLoggingEnabled() {
    return loggingEnabled;
  }

  /**
   * Sets whether logging should be enabled or not.
   *设置是否记录应该被使能
   * @param loggingEnabled Whether logging should be enabled or not
   */
  public void setLoggingEnabled(boolean loggingEnabled) {
    boolean oldValue = this.loggingEnabled;
    this.loggingEnabled = loggingEnabled;

    getPropertyChangeSupport().firePropertyChange(Attribute.LOGGING_ENABLED.name(),
                                                  oldValue,
                                                  loggingEnabled);
  }

  /**
   * Model attributes specific to this implementation.
   * 针对这个实现的模型的属性
   */
  public static enum Attribute {
    CURRENT_STATE,//当前状态
    PREVIOUS_STATE,//先前状态
    LAST_ORDER,//最新的订单
    VEHICLE_HOST,//车辆主站
    VEHICLE_PORT,//车辆端口
    PERIODIC_STATE_REQUESTS_ENABLED,//车辆状态的周期性使能
    PERIOD_STATE_REQUESTS_INTERVAL,//周期性状态寻求的时间间隔
    VEHICLE_IDLE_TIMEOUT,//车辆的空闲时间
    VEHICLE_IDLE,//车辆空闲
    DISCONNECTING_ON_IDLE,//空闲状态端开
    RECONNECTING_ON_CONNECTION_LOSS,//如果断开重新连接
    LOGGING_ENABLED,//记录使能
    RECONNECT_DELAY;//重连延时
  }
}
