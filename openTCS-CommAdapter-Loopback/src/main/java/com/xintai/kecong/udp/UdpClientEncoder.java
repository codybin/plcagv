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
import com.xintai.kecong.message.KeCongComandRead;
import com.xintai.kecong.message.KeCongComandWrite;
import com.xintai.kecong.message.KeCongRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 编码器
 * 1. 将服务器的对象==》DatagramPacket
 * @author binbin.hou
 * @since 1.0.0
 */
public class UdpClientEncoder extends MessageToMessageEncoder<KeCongRequestMessage> {

    /**
     * 需要传输的远方地址
     */
    private final  InetSocketAddress remoteAddress;
 private final String host;
 private final int port;
    public UdpClientEncoder(String host,int port) {
        // 广播地址
        this.host=host;
        this.port=port;
        this.remoteAddress = new InetSocketAddress(host, port);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, KeCongRequestMessage msg, List<Object> out) throws Exception {
      
      if(msg instanceof KeCongComandRead||msg instanceof KeCongComandWrite)
      {
       ByteBuf byteBuf = Unpooled.copiedBuffer(msg.creatMessage());
       InetSocketAddress remoteAddress1=new InetSocketAddress("192.168.100.200",17800);
        out.add(new DatagramPacket(byteBuf, remoteAddress1 )); 
          System.out.println("[Client] encode to " + remoteAddress1.toString());
      
      }else
      {
      ByteBuf byteBuf = Unpooled.copiedBuffer(msg.creatMessage());
        System.out.println("[Client] encode to " + remoteAddress.toString());
        out.add(new DatagramPacket(byteBuf, remoteAddress)); 
      }
     
    }

}