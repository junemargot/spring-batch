package hello.springbatch.batch;

import hello.springbatch.entity.WinEntity;
import hello.springbatch.repository.WinRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Configuration
public class SecondBatch {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final WinRepository winRepository;

  public SecondBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, WinRepository winRepository) {
    this.jobRepository = jobRepository;
    this.platformTransactionManager = platformTransactionManager;
    this.winRepository = winRepository;
  }

  @Bean
  public Job secondJob() {

    return new JobBuilder("secondJob", jobRepository)
            .start(secondStep())
            .build();
  }

  @Bean
  public Step secondStep() {

    return new StepBuilder("secondStep", jobRepository)
            // Step을 chunk 기반으로 정의. 데이터를 일정 단위(10개)로 읽어서 처리하고, 쓰는 작업을 반복한다.
            .<WinEntity, WinEntity> chunk(10, platformTransactionManager)
            .reader(winReader())         // 데이터를 읽어오는 역할
            .processor(trueProcessor())   // 읽어온 데이터를 가공/처리
            .writer(winWriter())          // 처리된 데이터를 저장/출력
            .build();
  }

  @Bean
  public RepositoryItemReader<WinEntity> winReader() {

    return new RepositoryItemReaderBuilder<WinEntity>()
            .name("winReader")                             // Reader 이름 지정, Step에서 상태 저장 등에 사용
            .pageSize(10)                                  // 한 번에 읽어올 데이터의 크기 지정(페이징 단위)
            .methodName("findByWinGreaterThanEqual")       // Repository에서 호출할 메서드 이름 지정
            .arguments(Collections.singletonList(10L))     // 위에서 지정한 메서드에 넘길 인자 값, win 값이 10 이상인 엔티티만 읽어온다.
            .repository(winRepository)                     // 실제로 데이터를 읽어올 Repository 객체 지정
            .sorts(Map.of("id", Sort.Direction.ASC))   // 데이터를 읽어올 때 정렬 기준 지정(ID 오름차순)
            .build();
  }

  @Bean
  public ItemProcessor<WinEntity, WinEntity> trueProcessor() {

    return item -> {
      item.setReward(true);

      return item;
    };
  }

  @Bean
  public RepositoryItemWriter<WinEntity> winWriter() {

    return new RepositoryItemWriterBuilder<WinEntity>()
            .repository(winRepository)
            .methodName("save")
            .build();
  }
}
