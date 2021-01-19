package ua.com.foxminded.batchxlsprocessor.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.excel.ExcelFileParseException;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.mapper.ProductExcelRowMapper;

@Configuration
@EnableBatchProcessing
public class JobConfig {

    private static final int SKIP_LIMIT = 300;

    @Value("${file.input}")
    private String fileInput;

    @Value("${file.output}")
    private String fileOutput;

    @Bean
    public ItemReader<Product> reader() {
        PoiItemReader<Product> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource(fileInput));
        reader.setRowMapper(new ProductExcelRowMapper());
        return reader;
    }

    @Bean
    public FlatFileItemWriter<Product> writer() {
        return new FlatFileItemWriterBuilder<Product>()
                .name("productItemWriter")
                .resource(new FileSystemResource(fileOutput))
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
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
    public ItemProcessor<Product, Product> validatingItemProcessor() {
        ValidatingItemProcessor<Product> validatingItemProcessor = new ValidatingItemProcessor<>(springValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    @Bean
    public Job job(Step step1, JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("xlsProcessingJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, SkipListener<Product, Product> skipListener) {
        return stepBuilderFactory.get("step1")
                .<Product, Product> chunk(1)
                .reader(reader())
                .processor(validatingItemProcessor())
                .writer(writer())
                .faultTolerant()
                .skip(ExcelFileParseException.class)
                .skipLimit(SKIP_LIMIT)
                .listener(skipListener)
                .build();
    }
}
