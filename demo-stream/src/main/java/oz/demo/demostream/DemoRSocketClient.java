package oz.demo.demostream;

public class DemoRSocketClient {

//	public static void main(String[] args) throws Exception {
//		RSocket socket = RSocketConnector.connectWith(TcpClientTransport.create("localhost", 55555)).log()
//				.retryWhen(Retry.backoff(5, Duration.ofSeconds(1))).block();
//
//		socket.requestResponse(DefaultPayload.create("\"blah blah\""))
//				.map(Payload::getDataUtf8)
//				.subscribe(System.out::println);
//		Thread.sleep(1000);
//	}

}
