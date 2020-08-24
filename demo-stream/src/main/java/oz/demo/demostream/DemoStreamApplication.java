package oz.demo.demostream;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class DemoStreamApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoStreamApplication.class, "--spring.cloud.function.definition=uppercase");

	}

	@Bean
	public Function<Flux<String>, Flux<String>> uppercase() {
		return flux -> flux.map(v -> {
			System.out.println("Uppercasing " + v);
			return v.toUpperCase();
		});
	}
	
//	@Bean
//	public Function<Flux<String>, Flux<Message<String>>> uppercase() {
//		return flux -> flux.map(v -> {
//			System.out.println("Uppercasing " + v);
//			return v.toUpperCase();
//		});
//	}


	@Bean
	public ApplicationRunner applicationRunner(StreamBridge bridge) {
		return new ApplicationRunner() {

			@Override
			public void run(ApplicationArguments args) throws Exception {
				for (int i = 0; i < 10; i++) {
					Thread.sleep(1000);
					bridge.send("uppercase-in-0", "Hello Liiklus " + i);
				}
			}
		};
	}
}

