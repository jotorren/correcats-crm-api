package cat.corredors.backoffice.users.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;

@Component
@Slf4j
public class DataPublisher implements Consumer<FluxSink<String>> {

	private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
	private final Executor executor = Executors.newSingleThreadExecutor();
	
	public boolean push(String message) {
		return queue.offer(message);
	}
	
	@Override
	public void accept(FluxSink<String> sink) {
		this.executor.execute(() -> {
			while (true) {
				try {
					String message = queue.take();
					sink.next(message);
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}
			}
		});
	}
}
