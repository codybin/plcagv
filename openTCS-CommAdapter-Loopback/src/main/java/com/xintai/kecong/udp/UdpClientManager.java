

package  com.xintai.kecong.udp;


import com.xintai.kecong.message.ConnectionEventListener;
import com.xintai.kecong.message.KeCongCommandResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;


public class UdpClientManager <O>{

private final ConnectionEventListener<KeCongCommandResponse> responseHandler;
private final String host;
private final int port;
private  boolean initialized;
private Bootstrap b;
private   EventLoopGroup group;
private Channel ch;
 public UdpClientManager(ConnectionEventListener<KeCongCommandResponse> responseHandler,String host ,int port)
 {

   this. responseHandler=responseHandler;

   this.host=host;

   this.port=port;
 
 }
  public boolean isInitialized() {
   
    return initialized;
  }
  public void terminate() {
    if (!initialized) {
      return;
    }
    group.shutdownGracefully();
    group = null;
    b = null;
    initialized = false;
  }
public void initial()throws Exception{

 group=new NioEventLoopGroup();
  b=new Bootstrap();
b.group(group).channel(NioDatagramChannel.class)
.option(ChannelOption.SO_BROADCAST, true)
.handler(new UdpChanleInitializer(responseHandler,host,port));
ch=b.bind(0).sync().channel();


/*ch.writeAndFlush(
new DatagramPacket(
Unpooled.copiedBuffer(context, CharsetUtil.UTF_8),
new InetSocketAddress("locahost", port)));

if(!ch.closeFuture().await(15000)){
System.out.println("≤È—Ø≥¨ ±");
}*/
initialized=true;

}
  public void send(O telegram) {
   
    ch.writeAndFlush(telegram);
  }
}



