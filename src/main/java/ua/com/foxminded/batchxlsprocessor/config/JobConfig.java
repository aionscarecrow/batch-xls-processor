package ua.com.foxminded.batchxlsprocessor.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.excel.ExcelFileParseException;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.listener.CustomSkipListener;
import ua.com.foxminded.batchxlsprocessor.listener.JobCompletionNotificationListener;
import ua.com.foxminded.batchxlsprocessor.mapper.ProductExcelRowMapper;
import ua.com.foxminded.batchxlsprocessor.processor.ProductItemProcessor;
import ua.com.foxminded.batchxlsprocessor.writer.NoOperationItemWriter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class JobConfig {

    private static final int CHUNK_SIZE = 100;
    private static final int SKIP_LIMIT = 300;

    @Bean
    @StepScope
    public PoiItemReader<Product> xlsReader(@Value("#{jobParameters['file.input']}") String input,
                                            ProductExcelRowMapper mapper) {

        PoiItemReader<Product> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource(input));
        reader.setRowMapper(mapper);
        return reader;
    }

    @Bean
    public org.springframework.validation.Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public Validator<Product> springValidator() {
        SpringValidator<Product> springValidator = new SpringValidator<>();
        springValidator.setValidator(validator());
        return springValidator;
    }

    @Bean
    public ValidatingItemProcessor<Product> validatingItemProcessor() {
        return new ValidatingItemProcessor<>(springValidator());
    }

    @Bean
    public CompositeItemProcessor<Product, Product> compositeItemProcessor(ProductItemProcessor productItemProcessor) {
        CompositeItemProcessor<Product, Product> compositeItermProcessor = new CompositeItemProcessor<>();
        List<ItemProcessor<Product, Product>> delegates = new ArrayList<>();
        delegates.add(validatingItemProcessor());
        delegates.add(productItemProcessor);
        compositeItermProcessor.setDelegates(delegates);
        return compositeItermProcessor;
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   JobCompletionNotificationListener jobCompletionListener,
                   Step getProductSummary) {

        return jobBuilderFactory.get("xlsProcessingJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener)
                .start(getProductSummary)
                .build();
    }

    @Bean
    public Step getProductSummary(StepBuilderFactory stepBuilderFactory,
                                  ItemReader<Product> xlsReader,
                                  CompositeItemProcessor<Product, Product> processor,
                                  NoOperationItemWriter writer,
                                  CustomSkipListener skipListener) {

        return stepBuilderFactory.get("getProductSummary")
                .<Product, Product> chunk(CHUNK_SIZE)
                .reader(xlsReader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(ExcelFileParseException.class)
                .skip(ValidationException.class)
                .skipLimit(SKIP_LIMIT)
                .listener(skipListener)
                .build();
    }
}
