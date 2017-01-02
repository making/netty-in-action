package com.example;

import java.net.InetSocketAddress;

import com.example.logging.Logger;
import com.example.logging.Loggers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class EchoServer {
	private static final Logger log = Loggers.getLogger(EchoServer.class);
	private final int port;

	public EchoServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {
		EchoServer echoServer = new EchoServer(33750);
		echoServer.start();
	}

	public void start() throws Exception {
		log.info("Listening {} port", port);
		final EchoServerHandler serverHandler = new EchoServerHandler();
		// Creates the EventLoopGroup
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			// Creates the ServerBootstrap
			ServerBootstrap b = new ServerBootstrap();
			b.group(group)
					// Specifies the use of an NIO transport Channel
					.channel(NioServerSocketChannel.class)
					// Sets the socket address using the specified port
					.localAddress(new InetSocketAddress(port))
					// Adds an EchoServerHandler to the Channel's ChannelPipeline
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// EchoServerHandler is @Sharable so we can always use the
							// same one.
							ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
									.addLast(serverHandler);
						}
					});
			// Binds the server asynchronously; sync() waits for the bind to complete
			ChannelFuture f = b.bind().sync();
			// Gets the CloseFuture of the Channel and block the current thread until it's
			// complete
			f.channel().closeFuture().sync();
		}
		finally {
			// Shut down the EventLoopGroup, releasing all resources
			group.shutdownGracefully().sync();
		}
	}
}
