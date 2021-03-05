/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xintai.device.DestinationLocationService;
import com.xintai.device.Destinations;
import com.xintai.device.DestinationsLocations;
import com.xintai.informatiomn.SinceTechInformation;
import com.xintai.interaction.erp.FinshInforFromERP;
import com.xintai.interaction.erp.ReponseResult;
import com.xintai.WMSTaskTable;
import com.xintai.interaction.erp.WMSTaskTables;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import jdk.internal.org.jline.utils.ExecHelper;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.to.order.OrderSequenceCreationTO;
import org.opentcs.data.ObjectExistsException;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.order.OrderSequence;
import org.opentcs.kernel.extensions.servicewebapi.HttpConstants;
import org.opentcs.kernel.extensions.servicewebapi.RequestHandler;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.OrderHandler;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Destination;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Transport;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.RequestStatusHandler;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.StatusEventDispatcher;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Service;

/**
 * Handles requests and produces responses for version 1 of the web API.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class V1RequestHandler
    implements RequestHandler {

  /**
   * Maps between objects and their JSON representations.
   */
  private final ObjectMapper objectMapper
      = new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  /**
   * Collects interesting events and provides them for client requests.
   */
  private final StatusEventDispatcher statusEventDispatcher;
  /**
   * Creates transport orders.
   */
  private final OrderHandler orderHandler;

  private final RequestStatusHandler statusInformationProvider;
  /**
   * Whether this instance is initialized.
   */
  private boolean initialized;
  private final DestinationLocationService destinationLocationService;

  @Inject
  public V1RequestHandler(StatusEventDispatcher statusEventDispatcher,
                          OrderHandler orderHandler,
                          RequestStatusHandler requestHandler,
                          DestinationLocationService destinationLocationService) {
    this.statusEventDispatcher = requireNonNull(statusEventDispatcher, "statusEventDispatcher");
    this.orderHandler = requireNonNull(orderHandler, "orderHandler");
    this.statusInformationProvider = requireNonNull(requestHandler, "requestHandler");
    this.destinationLocationService=requireNonNull(destinationLocationService, "destinationLocationService");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    statusEventDispatcher.initialize();

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

    statusEventDispatcher.terminate();

    initialized = false;
  }

  @Override
  public void addRoutes(Service service) {
    requireNonNull(service, "service");
     service.get("/information",
    this::handleGetXintaiInfor);
    service.get("/events",
                this::handleGetEvents);
     service.put("/vehicles/:NAME/finshmark",
                this::handlePutFinshWork);
      service.post("/tasktables/complete",
                this::handleInformationFromerp);
    service.put("/vehicles/:NAME/integrationLevel",
                this::handlePutVehicleIntegrationLevel);
    service.post("/vehicles/:NAME/withdrawal",
                 this::handlePostWithdrawalByVehicle);
    service.get("/vehicles/:NAME",
                this::handleGetVehicleByName);
    service.get("/vehicles",
                this::handleGetVehicles);
    service.post("/transportOrders/:NAME/withdrawal",
                 this::handlePostWithdrawalByOrder);
    service.post("/transportOrders/:NAME",
                 this::handlePostTransportOrder);
    service.post("/finshNotice/",
                 this::handlePostFinshInformationFromERP);
    //创建表单
     service.post("/tasktables",
                 this::handleCreateTaskTable);
    service.get("/transportOrders/:NAME",
                this::handleGetTransportOrderByName);
    //获得表单
     service.get("/tasktables",
                this::handleGetTaskTable);
    service.get("/transportOrders",
                this::handleGetTransportOrders);
  }

  private Object handleGetEvents(Request request, Response response)
      throws IllegalArgumentException, IllegalStateException {
    response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
    return toJson(statusEventDispatcher.fetchEvents(minSequenceNo(request),
                                                    maxSequenceNo(request),
                                                    timeout(request)));
  }
 private Object handleGetXintaiInfor(Request request, Response response)
      throws IllegalArgumentException, IllegalStateException {
    response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
    return toJson(new SinceTechInformation("znzz","xxx..com","2020"));
  }
  private Object handlePostTransportOrder(Request request, Response response)
      throws ObjectUnknownException,
             ObjectExistsException,
             IllegalArgumentException,
             IllegalStateException {
    orderHandler.createOrder(request.params(":NAME"), fromJson(request.body(), Transport.class));
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
    return "";
  }
 
  private Object handlePostWithdrawalByOrder(Request request, Response response)
      throws ObjectUnknownException {
    orderHandler.withdrawByTransportOrder(request.params(":NAME"),
                                          immediate(request),
                                          disableVehicle(request));
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
    return "";
  }
  
    private Object handlePostFinshInformationFromERP(Request request, Response response)
      throws ObjectUnknownException {
statusInformationProvider.handerfinshinformationfromerp(fromJson(request.body(), FinshInforFromERP.class));
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
    return "";
  }
  private Object handlePostWithdrawalByVehicle(Request request, Response response)
      throws ObjectUnknownException {
    orderHandler.withdrawByVehicle(request.params(":NAME"),
                                   immediate(request),
                                   disableVehicle(request));
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
    return "";
  }

  private Object handleGetTransportOrders(Request request, Response response) {
    response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
    return toJson(
        statusInformationProvider.getTransportOrdersState(
            valueIfKeyPresent(request.queryMap(), "intendedVehicle")
        )
    );
  }

  private Object handleGetTransportOrderByName(Request request, Response response) {
    response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
    return toJson(statusInformationProvider.getTransportOrderByName(request.params(":NAME")));
  }

  private Object handleGetVehicles(Request request, Response response)
      throws IllegalArgumentException {
    response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
    return toJson(
        statusInformationProvider.getVehiclesState(valueIfKeyPresent(request.queryMap(),
                                                                     "procState"))
    );
  }

  private Object handleGetVehicleByName(Request request, Response response)
      throws ObjectUnknownException {
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
    return toJson(statusInformationProvider.getVehicleStateByName(request.params(":NAME")));
  }
  
    private Object handleInformationFromerp(Request request, Response response)
      throws ObjectUnknownException, IllegalArgumentException {
  orderHandler.handerfinshinformation(fromJson(request.body(), WMSTaskTable.class));
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
    return "";
  }
      
      
 private Object handlePutFinshWork(Request request, Response response)
      throws ObjectUnknownException, IllegalArgumentException {
    statusInformationProvider.putMESFinshWork(
        request.params(":NAME"),
      valueIfKeyPresent(request.queryMap(), "newValue")
    );
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
    return "";
  }
  private Object handlePutVehicleIntegrationLevel(Request request, Response response)
      throws ObjectUnknownException, IllegalArgumentException {
    statusInformationProvider.putVehicleIntegrationLevel(
        request.params(":NAME"),
        valueIfKeyPresent(request.queryMap(), "newValue")
    );
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
    return "";
  }

  private String valueIfKeyPresent(QueryParamsMap queryParams, String key) {
    if (queryParams.hasKey(key)) {
      return queryParams.value(key);
    }
    else {
      return null;
    }
  }

  private <T> T fromJson(String jsonString, Class<T> clazz)
      throws IllegalArgumentException {
    try {
      return objectMapper.readValue(jsonString, clazz);
    }
    catch (IOException exc) {
      System.out.println(exc.getMessage());
      throw new IllegalArgumentException("Could not parse JSON input", exc);
    }
  }

  private String toJson(Object object)
      throws IllegalStateException {
    try {
      return objectMapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(object);
    }
    catch (JsonProcessingException exc) {
      throw new IllegalStateException("Could not produce JSON output", exc);
    }
  }

  private long minSequenceNo(Request request)
      throws IllegalArgumentException {
    String param = request.queryParamOrDefault("minSequenceNo", "0");
    try {
      return Long.parseLong(param);
    }
    catch (NumberFormatException exc) {
      throw new IllegalArgumentException("Malformed minSequenceNo: " + param);
    }
  }

  private long maxSequenceNo(Request request)
      throws IllegalArgumentException {
    String param = request.queryParamOrDefault("maxSequenceNo", String.valueOf(Long.MAX_VALUE));
    try {
      return Long.parseLong(param);
    }
    catch (NumberFormatException exc) {
      throw new IllegalArgumentException("Malformed minSequenceNo: " + param);
    }
  }

  private long timeout(Request request)
      throws IllegalArgumentException {
    String param = request.queryParamOrDefault("timeout", "1000");
    try {
      // Allow a maximum timeout of 10 seconds so server threads are only bound for a limited time.
      return Math.min(10000, Long.parseLong(param));
    }
    catch (NumberFormatException exc) {
      throw new IllegalArgumentException("Malformed timeout: " + param);
    }
  }

  private boolean immediate(Request request) {
    return Boolean.parseBoolean(request.queryParamOrDefault("immediate", "false"));
  }

  private boolean disableVehicle(Request request) {
    return Boolean.parseBoolean(request.queryParamOrDefault("disableVehicle", "false"));
  }
  
  private Object handleGetTaskTable(Request request, Response response) {
    response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
    List<WMSTaskTable> list=new LinkedList<>();
   // list.add(new WMSTaskTable("2021022270004","PTRU","001","002","FINSHI"));
    //list.add(new WMSTaskTable("2021022270005","YLRU","003","004","UNFINSHI"));
    //list.add(new WMSTaskTable("2021022270006","YLRU","006","007","UNFINSHI"));
    WMSTaskTables wMSTaskTable=new WMSTaskTables();
    wMSTaskTable.setwMSTaskTables(list);
    return toJson(wMSTaskTable);
  }
   private Object handleCreateTaskTable(Request request, Response response)
      throws ObjectUnknownException {
    WMSTaskTables  wMSTaskTables=fromJson(request.body(), WMSTaskTables.class);
    int cout= wMSTaskTables.getwMSTaskTables().size();
     for(int i=0;i<cout;i++)
    {
      create_order(wMSTaskTables, i);
    }
       wMSTaskTables.getwMSTaskTables().
       forEach(
           (e)->{
             System.out.println(e.toString());
           });
    response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
   ReponseResult  reponseResult= new ReponseResult();
    return  toJson(reponseResult.SUCCESS("Success",wMSTaskTables));
  }

  private void create_order(WMSTaskTables wMSTaskTables, int i)
      throws KernelRuntimeException, IllegalStateException {
      WMSTaskTable wmstt= wMSTaskTables.getwMSTaskTables().get(i);
      //添加任务单到数据库中
      Transport transport=new Transport();
      String name=wmstt.getTasknumber();
      String startstation=  wmstt.getStartstation();
      String endStationString=wmstt.getEndstation();
      DestinationsLocations  destinationsM1=  destinationLocationService.findDestinationsByOrderType(startstation);
      DestinationsLocations  destinationsM2=  destinationLocationService.findDestinationsByOrderType(endStationString);
      if(destinationsM1==null||destinationsM2==null)
      {//或者throw 异常
          throw new ObjectUnknownException("Unknown wmsstation: " + name);
      }
      destinationLocationService.InsertWMSTaskTable(wmstt);
      destinationsM1.MergerDestionation(destinationsM2.getDestinations());
      List<Destination> destinationst=new LinkedList<>();
      destinationsM1.getDestinations().getDestinations().forEach((e)->
      {
          Destination dstDestination=new Destination();
          dstDestination.setLocationName(e.getLocationName());
          dstDestination.setOperation(e.getOperation());
          destinationst.add(dstDestination);
      });
      transport.setDestinations(destinationst);
      orderHandler.createOrder(name,transport);
  }
}
