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
 *һ�����������������������ԵĹ۲�ģ��
 * @author Iryna Felko (Fraunhofer IML)
 * @author Stefan Walter (Fraunhofer IML)
 */
public class VehicleProcessModel {

  /**
   * The maximum number of notifications we want to keep.
   * ������Ҫ���ֵ����������֪ͨ
   */
  private static final int MAX_NOTIFICATION_COUNT = 100;
  /**
   * A copy of the kernel's Vehicle instance.
   * ����ʵ���ĸ���
   */
  private final Vehicle vehicle;
  /**
   * A reference to the vehicle.
   * �Գ�����һ������
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
   * �����ĵ�ǰ״̬
   */
  private Vehicle.State state = Vehicle.State.UNKNOWN;

  /**
   * Creates a new instance.
   *�����µ�ʵ��
   * @param attachedVehicle The vehicle attached to the new instance.
   */
  public VehicleProcessModel(@Nonnull Vehicle attachedVehicle) {
    this.vehicle = requireNonNull(attachedVehicle, "attachedVehicle");
    this.vehicleReference = vehicle.getReference();
  }

  /**
   * Registers a new property change listener with this model.
   *ע��������ģ�͵��µļ�����
   * @param listener The listener to be registered.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  /**
   * Unregisters a property change listener from this model.
   *ȥ��������ģ�͵�ע����
   * @param listener The listener to be unregistered.
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  /**
   * Returns a reference to the vehicle.
   *���ضԳ�����һ������
   * @return A reference to the vehicle.
   */
  @Nonnull
  public TCSObjectReference<Vehicle> getVehicleReference() {
    return vehicleReference;
  }

  /**
   * Returns the vehicle's name.
   *���س���������
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
   *����һ���û���֪ͨ
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
   *ͨ���ں˵��¼����Ʒ���һ���¼�
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
   *�����Ƿ���������ǰ���û��߲�����
   * @return <code>true</code> if, and only if, the comm adapter is currently enabled.
   */
  public boolean isCommAdapterEnabled() {
    return commAdapterEnabled;
  }

  /**
   * Sets the comm adapter's <em>enabled</em> flag.
   *������������ʹ�ܱ�ʾ
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
   *������ǰ�������Ƿ�����
   * @return <code>true</code> if, and only if, the comm adapter is currently connected.
   */
  public boolean isCommAdapterConnected() {
    return commAdapterConnected;
  }

  /**
   * Sets the comm adapter's <em>connected</em> flag.
   *���������������ӵı�ʾ
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
   *���س����ĵ�ǰλ��
   * @return The position.
   */
  @Nullable
  public String getVehiclePosition() {
    return vehiclePosition;
  }

  /**
   * Updates the vehicle's current position.
   *���³����ĵ�ǰλ��
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
   *��ó����ľ�ȷλ��
   * @return The vehicle's precise position.
   */
  @Nullable
  public Triple getVehiclePrecisePosition() {
    return precisePosition;
  }

  /**
   * Sets the vehicle's precise position.
   *���ó����ľ�ȷλ��
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
   *���س����ĵ�ǰ�ķ���Ƕ�
   * @return The vehicle's current orientation angle.
   * @see Vehicle#getOrientationAngle()
   */
  public double getVehicleOrientationAngle() {
    return orientationAngle;
  }

  /**
   * Sets the vehicle's current orientation angle.
   *���ó����ĵ�ǰ�ķ���Ƕ�
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
   *���س����ĵ�ǰ�ĵ���ˮƽ
   * @return The vehicle's current energy level.
   */
  public int getVehicleEnergyLevel() {
    return energyLevel;
  }

  /**
   * Sets the vehicle's current energy level.
   *���ó����ĵ�ǰ����ˮƽ
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
   *���ó���������
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
   *���س����ĵ�ǰ״̬
   * @return The state
   */
  @Nonnull
  public Vehicle.State getVehicleState() {
    return state;
  }

  /**
   * Sets the vehicle's current state.
   *���ó����ĵ�ǰ״̬
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
   *���ó������ڴ���Ķ���������
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
   *֪ͨ�۲��߱����������Ѿ���ӵ����������
   * @param enqueuedCommand The command that has been added to the queue.
   */
  public void commandEnqueued(@Nonnull MovementCommand enqueuedCommand) {
    getPropertyChangeSupport().firePropertyChange(Attribute.COMMAND_ENQUEUED.name(),
                                                  null,
                                                  enqueuedCommand);
  }

  /**
   * Notifies observers that the given command has been sent to the associated vehicle.
   *֪ͨ�۲��߱����͵������Ѿ������͵������ĳ���
   * @param sentCommand The command that has been sent to the vehicle.
   */
  public void commandSent(@Nonnull MovementCommand sentCommand) {
    getPropertyChangeSupport().firePropertyChange(Attribute.COMMAND_SENT.name(),
                                                  null,
                                                  sentCommand);
  }

  /**
   * Notifies observers that the given command has been executed by the comm adapter/vehicle.
   * *֪ͨ�۲��߸����������Ѿ���comm������/����ִ�С�
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
     * ����������ʹ�ܵ�����
     */
    COMM_ADAPTER_ENABLED,
    /**
     * Indicates a change of the comm adapter's <em>connected</em> setting.
     * ���� �������������õĸı�
     */
    COMM_ADAPTER_CONNECTED,
    /**
     * Indicates a change of the vehicle's position.
     * չ�ֳ�����λ�õĸı�
     */
    POSITION,
    /**
     * Indicates a change of the vehicle's precise position.
     * ��ʾ������ȷλ�õĸı�
     */
    PRECISE_POSITION,
    /**
     * Indicates a change of the vehicle's orientation angle.
     * չʾ������ת�Ƕȵĸı�
     */
    ORIENTATION_ANGLE,
    /**
     * Indicates a change of the vehicle's energy level.
     * չʾ���������ĸı�
     */
    ENERGY_LEVEL,
    /**
     * Indicates a change of the vehicle's load handling devices.
     * չʾ���������豸�ĸı�
     */
    LOAD_HANDLING_DEVICES,
    /**
     * Indicates a change of the vehicle's state.
     * չʾ����״̬�ĸı�
     */
    STATE,
    /**
     * Indicates a new user notification was published.
     * �µ��û�֪ͨ
     */
    USER_NOTIFICATION,
    /**
     * Indicates a new comm adapter event was published.
     * չʾ�µ�������������
     */
    COMM_ADAPTER_EVENT,
    /**
     * Indicates a command was enqueued.
     * չʾһ���������
     */
    COMMAND_ENQUEUED,
    /**
     * Indicates a command was sent.
     * չʾһ���������
     */
    COMMAND_SENT,
    /**
     * Indicates a command was executed successfully.
     * չʾһ������ɹ�����
     */
    COMMAND_EXECUTED,
    /**
     * Indicates a command failed.
     * չʾһ������ʧ����
     */
    COMMAND_FAILED,
    /**
     * Indicates a change of a vehicle property.
     * չʾһ���������Եĸı�
     */
    VEHICLE_PROPERTY,
    /**
     * Indicates a change of a transport order property.
     * չʾ���䶩���ĸı�
     */
    TRANSPORT_ORDER_PROPERTY;
  }
}
