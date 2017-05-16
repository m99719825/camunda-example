package example.approval;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableProcessApplication("VacationRequest")
public class VacationRequestApplication {

    private static int PORT;

    private static final Logger LOGGER = LoggerFactory.getLogger(VacationRequestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(VacationRequestApplication.class, args);
        LOGGER.info("You can reach the web app under: http://localhost:{}/", PORT);
    }


    @Component
    public static class ServletContainerListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

        @Override
        public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
            PORT = event.getEmbeddedServletContainer().getPort();
        }
    }
}
