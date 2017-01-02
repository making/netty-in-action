package com.example;

import java.nio.charset.Charset;

import com.example.logging.Logger;
import com.example.logging.Loggers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

// Marks this class as one whose instances can be shared among channels
@ChannelHandler.Sharable
public final class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Logger log = Loggers.getLogger(EchoClientHandler.class);

	/**
	 * Called after the connection to the server is established
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// When notified that the channel is active, sends a message
		ctx.channel()
				.writeAndFlush(
						Unpooled.copiedBuffer("Netty rocks!", Charset.defaultCharset()))
				.addListener(future -> {
					if (future.isSuccess()) {
						log.info("Write successful");
					}
					else {
						log.error("Write error", future.cause());
					}
				});
	}

	/**
	 * Called when a message is received from the server
	 * @param ctx
	 * @param in
	 * @throws Exception
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		// Logs a dump of the received message
		log.info("Client received: {}", in.toString(Charset.defaultCharset()));
	}

	/**
	 * Called if an exception is raised during processing
	 * @param ctx
	 * @param cause
	 * @throws Exception
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// On exception, logs the error and closes channel
		log.error("exception caught!!", cause);
		ctx.close();
	}
}
