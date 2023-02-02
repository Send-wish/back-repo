package link.sendwish.backend.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final JpaPagingItemReaderJobConfiguration jpaPagingItemReaderJobConfiguration;

    @Scheduled(cron = "0 0 1 * * *")
    public void runJob() {
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            jobLauncher.run(jpaPagingItemReaderJobConfiguration.jpaPagingItemReaderJob(), jobParameters);
        } catch (Exception e) {
            log.error(">>>>> Error", e);
        }
    }

}
