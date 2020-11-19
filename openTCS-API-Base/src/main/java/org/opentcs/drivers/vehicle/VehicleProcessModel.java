/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.drivers.vehicle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.Queue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.notification.UserNotification;

/**
 * An observable model of a vehicle's and its comm adapter's attributes.
 *一个车辆和他的适配器的属性的观测模型
 * @author Iryna Felko (Fraunhofer IML)
 * @author Stefan Walter (Fraunhofer IML)
 */
public class VehicleProcessModel {

  /**
   * The maximum number of notifications we want to keep.
   * 我们想要保持的最大数量的通知
   */
  private static final int MAX_NOTIFICATION_COUNT = 100;
  /**
   * A copy of the kernel's Vehicle instance.
   * 车辆实例的复制
   */
  private final Vehicle vehicle;
  /**
   * A reference to the vehicle.
   * 对车辆的一个引用
   */
  private final TCSObjectReference<Vehicle> vehicleReference;
  /**
   * Used for implementing property change events.
   */
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  /**
   * Whether the comm adapter is currently enabled.
   */
  private boolean commAdapterEnabled;
  /**
   * Whether the comm adapter is currently connected to the vehicle.
   */
  private boolean commAdapterConnected;
  /**
   * The name of the vehicle's current position.
   */
  private String vehiclePosition;
  /**
   * User notifications published by the comm adapter.
   */
  private final Queue<UserNotification> notifications = new LinkedList<>();
  /**
   * The vehicle's precise position.
   */
  private Triple precisePosition;
  /**
   * The vehicle's orientation angle.
   */
  private double orientationAngle = Double.NaN;
  /**
   * The vehicle's energy level.
   */
  private int energyLevel = 100;
  /**
   * The vehicle's load handling devices (state).
   */
  private List<LoadHandlingDevice> loadHandlingDevices = new LinkedList<>();
  /**
   * The vehicle's current state.
   * 车辆的当前状态
   */
  private Vehicle.State state = Vehicle.State.UNKNOWN;

  /**
   * Creates a new instance.
   *创建新的实例
   * @param attachedVehicle The vehicle attached to the new instance.
   */
  public VehicleProcessModel(@Nonnull Vehicle attachedVehicle) {
    this.vehicle = requireNonNull(attachedVehicle, "attachedVehicle");
    this.vehicleReference = vehicle.getReference();
  }

  /**
   * Registers a new property change listener with this model.
   *注册监听这个模型的新的监听者
   * @param listener The listener to be registered.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  /**
   * Unregisters a property change listener from this model.
   *去除检测这个模型的注册者
   * @param listener The listener to be unregistered.
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  /**
   * Returns a reference to the vehicle.
   *返回对车辆的一个引用
   * @return A reference to the vehicle.
   */
  @Nonnull
  public TCSObjectReference<Vehicle> getVehicleReference() {
    return vehicleReference;
  }

  /**
   * Returns the vehicle's name.
   *返回车辆的名字
   * @return The vehicle's name.
   */
  @Nonnull
  public String getName() {
    return vehicleReference.getName();
  }

  /**
   * Returns user notifications published by the comm adapter.
   *
   * @return The notifications.
   */
  @Nonnull
  public Queue<UserNotification> getNotifications() {
    return notifications;
  }

  /**
   * Publishes an user notification.
   *发布一个用户的通知
   * @param notification The notification to be published.
   */
  public void publishUserNotification(@Nonnull UserNotification notification) {
    requireNonNull(notification, "notification");

    notifications.add(notification);
    while (notifications.size() > MAX_NOTIFICATION_COUNT) {
      notifications.remove();
    }

    getPropertyChangeSupport().firePropertyChange(Attribute.USER_NOTIFICATION.name(),
                                                  null,
                                                  notification);
  }

  /**
   * Publishes an event via the kernel's event mechanism.
   *通过内核的事件机制发布一个事件
   * @param event The event to be published.
   */
  public void publishEvent(@Nonnull VehicleCommAdapterEvent event) {
    requireNonNull(event, "event");

    getPropertyChangeSupport().firePropertyChange(Attribute.COMM_ADAPTER_EVENT.name(),
                                                  null,
                                                  event);
  }

  /**
   * Indicates whether the comm adapter is currently enabled or not.
   *表明是否适配器当前可用或者不可用
   * @return <code>true</code> if, and only if, the comm adapter is currently enabled.
   */
  public boolean isCommAdapterEnabled() {
    return commAdapterEnabled;
  }

  /**
   * Sets the comm adapter's <em>enabled</em> flag.
   *设置适配器的使能标示
   * @param commAdapterEnabled The new value.
   */
  public void setCommAdapterEnabled(boolean commAdapterEnabled) {
    boolean oldValue = this.commAdapterEnabled;
    this.commAdapterEnabled = commAdapterEnabled;

    getPropertyChangeSupport().firePropertyChange(Attribute.COMM_ADAPTER_ENABLED.name(),
                                                  oldValue,
                                                  commAdapterEnabled);
  }

  /**
   * Indicates whether the comm adapter is currently connected or not.
   *表明当前适配器是否连接
   * @return <code>true</code> if, and only if, the comm adapter is currently connected.
   */
  public boolean isCommAdapterConnected() {
    return commAdapterConnected;
  }

  /**
   * Sets the comm adapter's <em>connected</em> flag.
   *设置适配器的连接的标示
   * @param commAdapterConnected The new value.
   */
  public void setCommAdapterConnected(boolean commAdapterConnected) {
    boolean oldValue = this.commAdapterConnected;
    this.commAdapterConnected = commAdapterConnected;

    getPropertyChangeSupport().firePropertyChange(Attribute.COMM_ADAPTER_CONNECTED.name(),
                                                  oldValue,
                                                  commAdapterConnected);
  }

  /**
   * Returns the vehicle's current position.
   *返回车辆的当前位置
   * @return The position.
   */
  @Nullable
  public String getVehiclePosition() {
    return vehiclePosition;
  }

  /**
   * Updates the vehicle's current position.
   *更新车辆的当前位置
   * @param position The new position
   */
  public void setVehiclePosition(@Nullable String position) {
    // Otherwise update the position, notify listeners and let the kernel know.
    String oldValue = this.vehiclePosition;
    vehiclePosition = position;

    getPropertyChangeSupport().firePropertyChange(Attribute.POSITION.name(),
                                                  oldValue,
                                                  position);
  }

  /**
   * Returns the vehicle's precise position.
   *获得车辆的精确位置
   * @return The vehicle's precise position.
   */
  @Nullable
  public Triple getVehiclePrecisePosition() {
    return precisePosition;
  }

  /**
   * Sets the vehicle's precise position.
   *设置车辆的精确位置
   * @param position The new position.
   */
  public void setVehiclePrecisePosition(@Nullable Triple position) {
    // Otherwise update the position, notify listeners and let the kernel know.
    Triple oldValue = this.precisePosition;
    this.precisePosition = position;

    getPropertyChangeSupport().firePropertyChange(Attribute.PRECISE_POSITION.name(),
                                                  oldValue,
                                                  position);
  }

  /**
   * Returns the vehicle's current orientation angle.
   *返回车辆的当前的方向角度
   * @return The vehicle's current orientation angle.
   * @see Vehicle#getOrientationAngle()
   */
  public double getVehicleOrientationAngle() {
    return orientationAngle;
  }

  /**
   * Sets the vehicle's current orientation angle.
   *设置车辆的当前的方向角度
   * @param angle The new angle
   */
  public void setVehicleOrientationAngle(double angle) {
    double oldValue = this.orientationAngle;
    this.orientationAngle = angle;

    getPropertyChangeSupport().firePropertyChange(Attribute.ORIENTATION_ANGLE.name(),
                                                  oldValue,
                                                  angle);
  }

  /**
   * Returns the vehicle's current energy level.
   *返回车辆的当前的电量水平
   * @return The vehicle's current energy level.
   */
  public int getVehicleEnergyLevel() {
    return energyLevel;
  }

  /**
   * Sets the vehicle's current energy level.
   *设置车辆的当前电量水平
   * @param newLevel The new level.
   */
  public void setVehicleEnergyLevel(int newLevel) {
    int oldValue = this.energyLevel;
    this.energyLevel = newLevel;

    getPropertyChangeSupport().firePropertyChange(Attribute.ENERGY_LEVEL.name(),
                                                  oldValue,
                                                  newLevel);
  }

  /**
   * Returns the vehicle's load handling devices.
   *
   * @return The vehicle's load handling devices.
   */
  @Nonnull
  public List<LoadHandlingDevice> getVehicleLoadHandlingDevices() {
    return loadHandlingDevices;
  }

  /**
   * Sets the vehicle's load handling devices.
   *
   * @param devices The new devices
   */
  public void setVehicleLoadHandlingDevices(@Nonnull List<LoadHandlingDevice> devices) {
    List<LoadHandlingDevice> devs = new LinkedList<>();
    for (LoadHandlingDevice lhd : devices) {
      devs.add(new LoadHandlingDevice(lhd));
    }
    List<LoadHandlingDevice> oldValue = this.loadHandlingDevices;
    this.loadHandlingDevices = devs;

    getPropertyChangeSupport().firePropertyChange(Attribute.LOAD_HANDLING_DEVICES.name(),
                                                  oldValue,
                                                  devs);
  }

  /**
   * Sets a property of the vehicle.
   *设置车辆的属性
   * @param key The property's key.
   * @param value The property's new value.
   */
  public void setVehicleProperty(@Nonnull String key, @Nullable String value) {
    getPropertyChangeSupport().firePropertyChange(Attribute.VEHICLE_PROPERTY.name(),
                                                  null,
                                                  new VehiclePropertyUpdate(key, value));
  }

  /**
   * Returns the vehicle's current state.
   *返回车辆的当前状态
   * @return The state
   */
  @Nonnull
  public Vehicle.State getVehicleState() {
    return state;
  }

  /**
   * Sets the vehicle's current state.
   *设置车辆的当前状态
   * @param newState The new state
   */
  public void setVehicleState(@Nonnull Vehicle.State newState) {
    Vehicle.State oldState = this.state;
    this.state = newState;

    getPropertyChangeSupport().firePropertyChange(Attribute.STATE.name(), oldState, newState);

    if (oldState != Vehicle.State.ERROR && newState == Vehicle.State.ERROR) {
      publishUserNotification(new UserNotification(getName(),
                                                   "Vehicle state changed to ERROR",
                                                   UserNotification.Level.NOTEWORTHY));
    }
    else if (oldState == Vehicle.State.ERROR && newState != Vehicle.State.ERROR) {
      publishUserNotification(new UserNotification(getName(),
                                                   "Vehicle state is no longer ERROR",
                                                   UserNotification.Level.NOTEWORTHY));
    }
  }

  /**
   * Sets a property of the transport order the vehicle is currently processing.
   *设置车辆正在处理的订单的属性
   * @param key The property's key.
   * @param value The property's new value.
   */
  public void setTransportOrderProperty(@Nonnull String key, @Nullable String value) {
    // XXX Should check if property already has the new value.
    getPropertyChangeSupport().firePropertyChange(Attribute.TRANSPORT_ORDER_PROPERTY.name(),
                                                  null,
                                                  new TransportOrderPropertyUpdate(key, value));
  }

  /**
   * Notifies observers that the given command has been added to the comm adapter's command queue.
   *通知观测者被给的命令已经添加到命令队列中
   * @param enqueuedCommand The command that has been added to the queue.
   */
  public void commandEnqueued(@Nonnull MovementCommand enqueuedCommand) {
    getPropertyChangeSupport().firePropertyChange(Attribute.COMMAND_ENQUEUED.name(),
                                                  null,
                                                  enqueuedCommand);
  }

  /**
   * Notifies observers that the given command has been sent to the associated vehicle.
   *通知观测者被发送的命令已经被发送到关联的车辆
   * @param sentCommand The command that has been sent to the vehicle.
   */
  public void commandSent(@Nonnull MovementCommand sentCommand) {
    getPropertyChangeSupport().firePropertyChange(Attribute.COMMAND_SENT.name(),
                                                  null,
                                                  sentCommand);
  }

  /**
   * Notifies observers that the given command has been executed by the comm adapter/vehicle.
   *
   * @param executedCommand The command that has been executed.
   */
  public void commandExecuted(@Nonnull MovementCommand executedCommand) {
    getPropertyChangeSupport().firePropertyChange(Attribute.COMMAND_EXECUTED.name(),
                                                  null,
                                                  executedCommand);
  }

  /**
   * Notifies observers that the given command could not be executed by the comm adapter/vehicle.
   *
   * @param failedCommand The command that could not be executed.
   */
  public void commandFailed(@Nonnull MovementCommand failedCommand) {
    getPropertyChangeSupport().firePropertyChange(Attribute.COMMAND_FAILED.name(),
                                                  null,
                                                  failedCommand);
  }

  protected PropertyChangeSupport getPropertyChangeSupport() {
    return pcs;
  }

  /**
   * A notification object sent to observers to indicate a change of a property.
   */
  public static class PropertyUpdate {

    /**
     * The property's key.
     */
    private final String key;
    /**
     * The property's new value.
     */
    private final String value;

    /**
     * Creates a new instance.
     *
     * @param key The key.
     * @param value The new value.
     */
    public PropertyUpdate(String key, String value) {
      this.key = requireNonNull(key, "key");
      this.value = value;
    }

    /**
     * Returns the property's key.
     *
     * @return The property's key.
     */
    public String getKey() {
      return key;
    }

    /**
     * Returns the property's new value.
     *
     * @return The property's new value.
     */
    public String getValue() {
      return value;
    }
  }

  /**
   * A notification object sent to observers to indicate a change of a vehicle's property.
   */
  public static class VehiclePropertyUpdate
      extends PropertyUpdate {

    /**
     * Creates a new instance.
     *
     * @param key The property's key.
     * @param value The new value.
     */
    public VehiclePropertyUpdate(String key, String value) {
      super(key, value);
    }
  }

  /**
   * A notification object sent to observers to indicate a change of a transport order's property.
   */
  public static class TransportOrderPropertyUpdate
      extends PropertyUpdate {

    /**
     * Creates a new instance.
     *
     * @param key The property's key.
     * @param value The new value.
     */
    public TransportOrderPropertyUpdate(String key, String value) {
      super(key, value);
    }
  }

  /**
   * Notification arguments to indicate some change.
   */
  public enum Attribute {
    /**
     * Indicates a change of the comm adapter's <em>enabled</em> setting.
     * 表现适配器使能的设置
     */
    COMM_ADAPTER_ENABLED,
    /**
     * Indicates a change of the comm adapter's <em>connected</em> setting.
     * 表现 适配器连接设置的改变
     */
    COMM_ADAPTER_CONNECTED,
    /**
     * Indicates a change of the vehicle's position.
     * 展现出车辆位置的改变
     */
    POSITION,
    /**
     * Indicates a change of the vehicle's precise position.
     * 暗示车辆精确位置的改变
     */
    PRECISE_POSITION,
    /**
     * Indicates a change of the vehicle's orientation angle.
     * 展示车辆旋转角度的改变
     */
    ORIENTATION_ANGLE,
    /**
     * Indicates a change of the vehicle's energy level.
     * 展示车辆电量的改变
     */
    ENERGY_LEVEL,
    /**
     * Indicates a change of the vehicle's load handling devices.
     * 展示车辆负载设备的改变
     */
    LOAD_HANDLING_DEVICES,
    /**
     * Indicates a change of the vehicle's state.
     * 展示车辆状态的改变
     */
    STATE,
    /**
     * Indicates a new user notification was published.
     * 新的用户通知
     */
    USER_NOTIFICATION,
    /**
     * Indicates a new comm adapter event was published.
     * 展示新的适配器被公布
     */
    COMM_ADAPTER_EVENT,
    /**
     * Indicates a command was enqueued.
     * 展示一个命令被入列
     */
    COMMAND_ENQUEUED,
    /**
     * Indicates a command was sent.
     * 展示一个命令被发送
     */
    COMMAND_SENT,
    /**
     * Indicates a command was executed successfully.
     * 展示一个命令被成功运行
     */
    COMMAND_EXECUTED,
    /**
     * Indicates a command failed.
     * 展示一个命令失败了
     */
    COMMAND_FAILED,
    /**
     * Indicates a change of a vehicle property.
     * 展示一个车辆属性的改变
     */
    VEHICLE_PROPERTY,
    /**
     * Indicates a change of a transport order property.
     * 展示运输订单的改变
     */
    TRANSPORT_ORDER_PROPERTY;
  }
}
