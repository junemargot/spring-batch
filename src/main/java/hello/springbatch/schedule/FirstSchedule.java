package hello.springbatch.schedule;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class FirstSchedule {

  private final JobLauncher jobLauncher;
  private final JobRegistry jobRegistry;

  public FirstSchedule(JobLauncher jobLauncher, JobRegistry jobRegistry) {
    this.jobLauncher = jobLauncher;
    this.jobRegistry = jobRegistry;
  }

  // 매 분마다 10초에 실행, 한국 시간대를 기준으로 실행
  @Scheduled(cron = "10 * * * * *", zone = "Asia/Seoul")
  public void runFirstJob() throws Exception {
    System.out.println("first schedule started");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    String date = dateFormat.format(new Date());

    JobParameters jobParameters = new JobParametersBuilder()
            .addString("date", date)
            .toJobParameters();

    jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);
  }
}
