/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xintai.kecong.udp;

/**
 *
 * @author Lenovo
 */
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;




public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket>{

@Override
   public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)throws Exception{
       ctx.close();
       cause.printStackTrace();
}

  @Override
  protected void channelRead0(ChannelHandlerContext chc, DatagramPacket packet)
      throws Exception {
    String response=packet.content().toString(CharsetUtil.UTF_8);
if(response.startsWith("½á¹û£º")){
System.out.println(response);

}//To change body of generated methods, choose Tools | Templates.
  }


}
