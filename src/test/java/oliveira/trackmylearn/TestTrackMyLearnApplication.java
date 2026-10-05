package oliveira.trackmylearn;

import org.springframework.boot.SpringApplication;

public class TestTrackMyLearnApplication {

    public static void main(String[] args) {
        SpringApplication.from(TrackMyLearnApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
