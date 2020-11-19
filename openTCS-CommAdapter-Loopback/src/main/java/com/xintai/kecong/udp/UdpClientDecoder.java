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
import com.xintai.kecong.message.ByteQueue;
import com.xintai.kecong.message.ConnectionEventListener;
import com.xintai.kecong.message.DataConvertUtl;
import com.xintai.kecong.message.KeCongCommandResponse;
import com.xintai.kecong.message.ParseRecievedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;


/**
 * @author binbin.hou
 * @since 1.0.0
 */
public class UdpClientDecoder extends MessageToMessageDecoder<DatagramPacket>{
private final ConnectionEventListener<KeCongCommandResponse> responseHandler;
  private ByteQueue queue;
  private ParseRecievedMessage parserecivedmessage;
public UdpClientDecoder(ConnectionEventListener<KeCongCommandResponse> responseHandler)
{
this.responseHandler=responseHandler;
}
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        
      ByteBuf bytyBufeBuf=msg.content();
  int length=   bytyBufeBuf.readableBytes();
      byte[] data =new byte[length];
      bytyBufeBuf.readBytes(data);
      final String result = new String(data);
        System.out.println("[Client] decode msg: " + result);
         String string=DataConvertUtl.toHexString(data);
  System.out.println("Client"+string);
         queue = new ByteQueue(data, 0, data.length);
            parserecivedmessage = new  ParseRecievedMessage(queue);
       // MessageBean messageBean = new MessageBean();
       // messageBean.setTime(result);
         responseHandler.onIncomingTelegram(parserecivedmessage.ParseResponseMessage());
        //out.add(messageBean);
    }


}