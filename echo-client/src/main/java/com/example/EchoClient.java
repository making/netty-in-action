package com.example;

import java.net.InetSocketAddress;

import com.example.logging.Logger;
import com.example.logging.Loggers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class EchoClient {
	private static final Logger log = Loggers.getLogger(EchoClient.class);
	private final String hostname;
	private final int port;

	public static void main(String[] args) throws Exception {
		EchoClient echoClient = new EchoClient("localhost", 33750);
		echoClient.start();
		echoClient.start();
		echoClient.start();
	}

	public EchoClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	private void start() throws Exception {
		log.info("Connect to {}:{}", hostname, port);
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.remoteAddress(new InetSocketAddress(hostname, port))
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
									.addLast(new EchoClientHandler());
						}
					});
			ChannelFuture f = b.connect().sync();
			f.channel().closeFuture().sync();
		}
		finally {
			group.shutdownGracefully().sync();
		}
	}

}
