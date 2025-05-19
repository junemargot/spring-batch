package hello.springbatch.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainController {

  private final JobLauncher jobLauncher;
  private final JobRegistry jobRegistry;
  private final JobRepository jobRepository;

  public MainController(JobLauncher jobLauncher, JobRegistry jobRegistry, JobRepository jobRepository) {
    this.jobLauncher = jobLauncher;
    this.jobRegistry = jobRegistry;
    this.jobRepository = jobRepository;
  }

  @GetMapping("/first")
  public String firstApi(@RequestParam("value") String value) throws Exception{

    JobParameters jobParameters = new JobParametersBuilder()
            .addString("date", value)
//            .addLong("time", System.currentTimeMillis()) // 현재 시간을 추가하여 항상 고유한 파라미터 생성
            .toJobParameters();

    jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);

    return "FIRST BATCH PROCESS OK";
  }

  @GetMapping("/second")
  public String secondApi(@RequestParam("value") String value) throws Exception{

    JobParameters jobParameters = new JobParametersBuilder()
            .addString("date", value)
            .toJobParameters();

    jobLauncher.run(jobRegistry.getJob("secondJob"), jobParameters);

    return "SECOND BATCH PROCESS OK";
  }

  @GetMapping("/third")
  public String thirdApi(@RequestParam("value") String value) throws Exception{

    JobParameters jobParameters = new JobParametersBuilder()
            .addString("date", value)
            .toJobParameters();

    jobLauncher.run(jobRegistry.getJob("thirdJob"), jobParameters);

    return "THIRD BATCH PROCESS OK";
  }
}
