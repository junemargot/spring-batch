package hello.springbatch.batch;

import hello.springbatch.entity.AfterEntity;
import hello.springbatch.repository.AfterRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.processing.Processor;
import java.io.IOException;

@Configuration
@Slf4j
public class ThirdBatch {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final AfterRepository afterRepository;

  public ThirdBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, AfterRepository afterRepository) {
    this.jobRepository = jobRepository;
    this.platformTransactionManager = platformTransactionManager;
    this.afterRepository = afterRepository;
  }

  @Bean
  public Job thirdJob() {
    log.info("Third Job started");

    return new JobBuilder("thirdJob", jobRepository)
            .start(thirdStep())
            .build();
  }

  @Bean
  public Step thirdStep() {
    log.info("Third Step started");

    return new StepBuilder("thirdStep", jobRepository)
            .<Row, AfterEntity> chunk(10, platformTransactionManager)
            .reader(excelReader())
            .processor(thirdProcessor())
            .writer(thirdAfterWriter())
            .build();
  }

  @Bean
  public ItemStreamReader<Row> excelReader() {
    log.info("Excel Reader started");

    try {
      return new ExcelRowReader("/Users/margotinjune/Documents/texts.xls");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  public ItemProcessor<Row, AfterEntity> thirdProcessor() {
    log.info("Third Processor started");

    return new ItemProcessor<Row, AfterEntity>() {

      @Override
      public AfterEntity process(Row item) {

        AfterEntity afterEntity = new AfterEntity();
        afterEntity.setUsername(item.getCell(0).getStringCellValue());

        return afterEntity;
      }
    };
  }

  @Bean
  public RepositoryItemWriter<AfterEntity> thirdAfterWriter() {

    return new RepositoryItemWriterBuilder<AfterEntity>()
            .repository(afterRepository)
            .methodName("save")
            .build();
  }
}