/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.udp;

import com.xintai.kecong.message.ConnectionEventListener;
import com.xintai.kecong.message.KeCongCommandResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 *
 * @author Lenovo
 */
public class UdpChanleInitializer  extends ChannelInitializer<Channel>{

  private final ConnectionEventListener<KeCongCommandResponse> responseHandler;
  private final String host;
  private final int port;

  public UdpChanleInitializer(ConnectionEventListener<KeCongCommandResponse> responseHandler,String host,int port)
  {
  this.host=host;
  this.port=port;
  this.responseHandler=responseHandler;
  }
  
  @Override
  protected void initChannel(Channel ch)
      throws Exception {
      ch.pipeline()
                                .addLast(new UdpClientDecoder(responseHandler))
                                .addLast(new UdpClientEncoder(host, port));
  }
  
}
