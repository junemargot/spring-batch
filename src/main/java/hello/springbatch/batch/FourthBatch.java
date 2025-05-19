package hello.springbatch.batch;

import hello.springbatch.entity.BeforeEntity;
import hello.springbatch.repository.BeforeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.util.Map;

@Configuration
@Slf4j
public class FourthBatch {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final BeforeRepository beforeRepository;

  public FourthBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, BeforeRepository beforeRepository) {
    this.jobRepository = jobRepository;
    this.platformTransactionManager = platformTransactionManager;
    this.beforeRepository = beforeRepository;
  }

  @Bean
  public Job fourthJob() {
    log.info("Fourth Batch Job");

    return new JobBuilder("fourthJob", jobRepository)
            .start(fourthStep())
            .build();
  }

  @Bean
  public Step fourthStep() {
    log.info("Fourth Batch Step");

    return new StepBuilder("fourthStep", jobRepository)
            .<BeforeEntity, BeforeEntity> chunk(10, platformTransactionManager)
            .reader(fourthBeforeReader())
            .processor(fourthProcessor())
            .writer(excelWriter())
            .build();
  }

  @Bean
  public RepositoryItemReader<BeforeEntity> fourthBeforeReader() {

    RepositoryItemReader<BeforeEntity> reader = new RepositoryItemReaderBuilder<BeforeEntity>()
            .name("beforeReader")
            .pageSize(10)
            .methodName("findAll")
            .repository(beforeRepository)
            .sorts(Map.of("id", Sort.Direction.ASC))
            .build();

    // 전체 데이터셋에서 어디까지 수행했는지의 값을 저장하지 않음
    reader.setSaveState(false);

    return reader;
  }

  @Bean
  public ItemProcessor<BeforeEntity, BeforeEntity> fourthProcessor() {

    return item -> item;
  }

  @Bean
  public ItemStreamWriter<BeforeEntity> excelWriter() {

    try {
      return new ExcelRowWriter("/Users/margotinjune/Documents/result.xls");

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
