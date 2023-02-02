package link.sendwish.backend.job;

import link.sendwish.backend.common.exception.ScrapingException;
import link.sendwish.backend.entity.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManagerFactory;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaderJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;
    private int num = 50;

    @Bean
    public Job jpaPagingItemReaderJob() {
        num = num ++;
        log.info(">>>>> {}", num);
        String name = "jpaPagingItemReaderJob" + num;
        return jobBuilderFactory.get(name)
                .start(jpaPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jpaPagingItemReaderStep() {
        return stepBuilderFactory.get("jpaPagingItemReaderStep")
                .<Item, Item>chunk(chunkSize)
                .reader(jpaPagingItemReader())
                .processor(jpaItemProcessor())
                .writer(jpaPagingItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Item> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Item>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p FROM Item p")
                .build();
    }

    @Bean
    public ItemProcessor<Item, Item> jpaItemProcessor() {
        return item -> {
            // item의 originUrl로 파싱 후 price변경사항 있으면 넘기기
            Integer price = item.getPrice();
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("url", item.getOriginUrl());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            JSONObject jsonObject = null;

            log.info("====START PARSING====");
            try{
                jsonObject = new JSONObject(
                        restTemplate.postForObject("http://13.209.229.237:5000/webscrap", entity, String.class));
            }catch (Exception e){
                log.info(String.valueOf(e));
                throw new ScrapingException();
            }
            log.info("====FINISH PARSING====");

            log.info("=== title : {}", jsonObject.getString("title"));
            log.info("=== price : {}", jsonObject.getInt("price"));
            log.info("=== img : {}", jsonObject.getString("img"));
            Integer newPrice = jsonObject.getInt("price");
            boolean isIgnoreTarget = price == newPrice;

            if(isIgnoreTarget){
                log.info(">>>>>>>>> item name={}, isIgnoreTarget={}", item.getName(), isIgnoreTarget);
                return null;
            }
            log.info("[update target]");
            item.updatePrice(newPrice);
            return item;
        };
    }

    @Bean
    public ItemWriter<Item> jpaPagingItemWriter() {
        JpaItemWriter<Item> jpaItemWriter = new JpaItemWriter<Item>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        try {
            jpaItemWriter.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jpaItemWriter;
    }
}