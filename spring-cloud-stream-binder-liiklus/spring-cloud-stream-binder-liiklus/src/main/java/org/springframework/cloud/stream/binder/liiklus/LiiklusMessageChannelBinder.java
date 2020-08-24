package org.springframework.cloud.stream.binder.liiklus;

import java.util.UUID;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.catalog.SimpleFunctionRegistry.FunctionInvocationWrapper;
import org.springframework.cloud.stream.binder.AbstractBinder;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.core.env.Environment;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.StringUtils;

import com.github.bsideup.liiklus.RSocketLiiklusClient;
import com.github.bsideup.liiklus.protocol.LiiklusEvent;
import com.github.bsideup.liiklus.protocol.PublishRequest;
import com.github.bsideup.liiklus.protocol.ReceiveReply.LiiklusEventRecord;
import com.github.bsideup.liiklus.protocol.ReceiveRequest;
import com.github.bsideup.liiklus.protocol.ReceiveRequest.ContentFormat;
import com.github.bsideup.liiklus.protocol.SubscribeReply;
import com.github.bsideup.liiklus.protocol.SubscribeRequest;
import com.github.bsideup.liiklus.protocol.SubscribeRequest.AutoOffsetReset;
import com.google.protobuf.ByteString;

import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class LiiklusMessageChannelBinder
	extends AbstractBinder<MessageChannel, ConsumerProperties, ProducerProperties> {
	
	private final FunctionCatalog catalog;
	
	public LiiklusMessageChannelBinder(FunctionCatalog catalog) {
		this.catalog = catalog;
	}

	@Override
	protected Binding<MessageChannel> doBindConsumer(String name, String group, MessageChannel inputTarget,
			ConsumerProperties properties) {
		String functionName = name.substring(0, name.indexOf("-"));
		Function<Publisher<?>, Publisher<?>> function = this.catalog.lookup(functionName);
		
		group = StringUtils.hasText(group) ? group : UUID.randomUUID().toString();
		SubscribeRequest subscribeAction = SubscribeRequest.newBuilder()
                .setTopic(name)
                .setGroup(group) // TODO replace
                .setAutoOffsetReset(AutoOffsetReset.LATEST)
                .build();

		TcpClientTransport transport = TcpClientTransport.create("localhost", 8081);
		RSocketLiiklusClient liiklusClient = new RSocketLiiklusClient(
                RSocketConnector.connectWith(transport).block()
        );
		
		
//		Flux<Message<Message<String>>> flux = IntegrationReactiveUtils.messageChannelToFlux(inputTarget);
	
		Disposable consumer = liiklusClient.subscribe(subscribeAction)
   			.map(SubscribeReply::getAssignment)
   			.doOnNext(assignment -> System.out.println("Assigned to partition " + assignment.getPartition()))
   			.flatMap(assignment -> liiklusClient
   					.receive(ReceiveRequest.newBuilder().setAssignment(assignment).setFormat(ContentFormat.LIIKLUS_EVENT).build())
//   					.doOnNext(receiveReply -> {
//   						LiiklusEventRecord liiklusEventRecord = receiveReply.getLiiklusEventRecord();
//   						LiiklusEvent liiklusEvent = liiklusEventRecord.getEvent();
//   						byte[] data = liiklusEvent.getData().toByteArray();
//						System.out.println("==> FROM KAFKA: " + liiklusEvent.getData().toStringUtf8());
//						Message<byte[]> message = MessageBuilder.withPayload(data).build();
//						inputTarget.send(message);
//   					})
   					.map(receiveReply -> {
   						LiiklusEventRecord liiklusEventRecord = receiveReply.getLiiklusEventRecord();
   						LiiklusEvent liiklusEvent = liiklusEventRecord.getEvent();
   						byte[] data = liiklusEvent.getData().toByteArray();
						System.out.println("==> FROM KAFKA: " + liiklusEvent.getData().toStringUtf8());
						Message<byte[]> message = MessageBuilder.withPayload(data).build();
						return message;
   					})
   			)
   			.transform(function)
   			.subscribe();
		
		
		Binding<MessageChannel> binding = new Binding<MessageChannel>() {
			@Override
			public void unbind() {
				consumer.dispose();
			}
		};
		return binding;
	}

	@Override
	protected Binding<MessageChannel> doBindProducer(String name, MessageChannel outboundBindTarget,
			ProducerProperties properties) {

		TcpClientTransport transport = TcpClientTransport.create("localhost", 8081);
		RSocketLiiklusClient liiklusClient = new RSocketLiiklusClient(RSocketConnector.connectWith(transport).block());

		MessageHandler handler = v -> {
			byte[] data = (byte[]) v.getPayload();
			LiiklusEvent event = LiiklusEvent.newBuilder()
				      .setId(UUID.randomUUID().toString())
				      .setType("com.example.event")
				      .setSource("/example")
				      .setDataContentType("application/json")
				      .setData(ByteString.copyFrom(data))
				      .build();
			PublishRequest request = PublishRequest.newBuilder().setTopic(name).setLiiklusEvent(event).build();
	       	liiklusClient.publish(request).block();
		};
		((SubscribableChannel) outboundBindTarget).subscribe(handler);

	
		Binding<MessageChannel> binding = new Binding<MessageChannel>() {

			@Override
			public void unbind() {
				((SubscribableChannel) outboundBindTarget).unsubscribe(handler);
			}
		};
		return binding;
	}

}
