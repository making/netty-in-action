package com.example;

import java.nio.charset.Charset;

import com.example.logging.Logger;
import com.example.logging.Loggers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

// Indicates that a ChannelHandler can be safely shared by multiple channels
@ChannelHandler.Sharable
public final class EchoServerHandler extends ChannelInboundHandlerAdapter {
	private final Logger log = Loggers.getLogger(EchoServerHandler.class);

	/**
	 * Called for each incoming message
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf in = (ByteBuf) msg;
		log.info("Server received: {}", in.toString(Charset.defaultCharset()));
		// Writes the received message to the sender without flushing the outbound
		// messages
		ctx.write(in);
	}

	/**
	 * Notifies the handler that the last call made to
	 * {@link #channelRead(ChannelHandlerContext, Object)} was the last message in the
	 * current batch
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// Flushes pending messages to the remote peer and closes the channel
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Called if an exception is thrown during the read operation
	 * @param ctx
	 * @param cause
	 * @throws Exception
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		log.error("exception caught!!", cause);
		// Closes the channel
		ctx.close();
	}
}
