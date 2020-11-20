/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernelcontrolcenter;

import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import org.opentcs.common.ClientConnectionMode;
import org.opentcs.common.KernelClientApplication;
import org.opentcs.common.PortalManager;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.kernelcontrolcenter.exchange.KernelEventFetcher;
import org.opentcs.kernelcontrolcenter.util.KernelControlCenterConfiguration;
import org.opentcs.util.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The kernel control center application's entry point.
 *控制中心应用的进入点
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class KernelControlCenterApplication
    implements KernelClientApplication {

  /**
   * This class' logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(KernelControlCenterApplication.class);
  /**
   * The instance fetching for kernel events.
   * 获取内核事件的实例
   */
  private final KernelEventFetcher eventFetcher;
  /**
   * The actual kernel control center.
   * 真实的控制中心街
   */
  private final KernelControlCenter kernelControlCenter;
  /**
   * The service portal manager.
   * 服务端口管理
   */
  private final PortalManager portalManager;
  /**
   * The application's event bus.
   * 应用的事件通道
   */
  private final EventBus eventBus;
  /**
   * The application's configuration.
   * 应用的配置
   */
  private final KernelControlCenterConfiguration configuration;
  /**
   * Whether this application is online or not.
   * 是否这个应用在线
   */
  private ConnectionState connectionState = ConnectionState.OFFLINE;
  /**
   * Whether this application is initialized or not.
   * 是否这个应用被初始化
   */
  private boolean initialized;

  /**
   * Creates a new instance.
   *
   * @param eventFetcher The instance fetching for kernel events.
   * @param kernelControlCenter The actual kernel control center.
   * @param portalManager The service portal manager.
   * @param eventBus The application's event bus.
   * @param configuration The application's configuration.
   */
  @Inject
  public KernelControlCenterApplication(KernelEventFetcher eventFetcher,
                                        KernelControlCenter kernelControlCenter,
                                        PortalManager portalManager,
                                        @ApplicationEventBus EventBus eventBus,
                                        KernelControlCenterConfiguration configuration) {
    this.eventFetcher = requireNonNull(eventFetcher, "eventHub");
    this.kernelControlCenter = requireNonNull(kernelControlCenter, "kernelControlCenter");
    this.portalManager = requireNonNull(portalManager, "portalManager");
    this.eventBus = requireNonNull(eventBus, "eventBus");
    this.configuration = requireNonNull(configuration, "configuration");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    kernelControlCenter.initialize();
    eventFetcher.initialize();

    // Trigger initial connect
    online(configuration.connectAutomaticallyOnStartup());

    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }

    // If we want to terminate but are still online, go offline first
    offline();

    eventFetcher.terminate();
    kernelControlCenter.terminate();

    initialized = false;
  }

  @Override
  public void online(boolean autoConnect) {
    if (isOnline() || isConnecting()) {
      return;
    }

    connectionState = ConnectionState.CONNECTING;
    //连接内核
    if (portalManager.connect(toConnectionMode(autoConnect))) {
      LOG.info("应用和内核连接...");
      connectionState = ConnectionState.ONLINE;
      eventBus.onEvent(ClientConnectionMode.ONLINE);
    }
    else {
      connectionState = ConnectionState.OFFLINE;
    }
  }

  @Override
  public void offline() {
    if (!isOnline() && !isConnecting()) {
      return;
    }

    portalManager.disconnect();

    LOG.info("应用和内核断开连接...");
    connectionState = ConnectionState.OFFLINE;
    eventBus.onEvent(ClientConnectionMode.OFFLINE);
  }

  @Override
  public boolean isOnline() {
    return connectionState == ConnectionState.ONLINE;
  }

  /**
   * Returns <code>true</code> if, and only if the control center is trying to establish a
   * connection to the kernel.
   *
   * @return <code>true</code> if, and only if the control center is trying to establish a
   * connection to the kernel
   */
  public boolean isConnecting() {
    return connectionState == ConnectionState.CONNECTING;
  }

  private PortalManager.ConnectionMode toConnectionMode(boolean autoConnect) {
    return autoConnect ? PortalManager.ConnectionMode.AUTO : PortalManager.ConnectionMode.MANUAL;
  }

  /**
   * An enum to display the different states of the control center application connection to the
   * kernel.
   */
  private enum ConnectionState {
    /**
     * The control center is not connected to the kernel and is not trying to.
     */
    OFFLINE,
    /**
     * The control center is currently trying to connect to the kernel.
     */
    CONNECTING,
    /**
     * The control center is connected to the kernel.
     */
    ONLINE
  }
}
