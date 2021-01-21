package ua.com.foxminded.batchxlsprocessor.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.listener.CustomSkipListener;
import ua.com.foxminded.batchxlsprocessor.mapper.ProductExcelRowMapper;
import ua.com.foxminded.batchxlsprocessor.processor.ProductItemProcessor;
import ua.com.foxminded.batchxlsprocessor.writer.MapProductWriter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class JobConfig {

    private static final int CHUNK_SIZE = 1200;
    private static final int SKIP_LIMIT = 300;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ProductItemProcessor productItemProcessor;

    @Autowired
    private MapProductWriter writer;

    @Autowired
    private CustomSkipListener skipListener;

    @Value("${file.input}")
    private String fileInput;

    @Bean
    public ItemReader<Product> reader() {
        PoiItemReader<Product> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource(fileInput));
        reader.setRowMapper(new ProductExcelRowMapper());
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
        ValidatingItemProcessor<Product> validatingItemProcessor = new ValidatingItemProcessor<>(springValidator());
        validatingItemProcessor.setFilter(false);
        return validatingItemProcessor;
    }

    @Bean
    public CompositeItemProcessor<Product, Product> compositeItemProcessor() {
        CompositeItemProcessor<Product, Product> compositeItermProcessor = new CompositeItemProcessor<>();
        List<ItemProcessor<Product, Product>> delegates = new ArrayList<>();
        delegates.add(validatingItemProcessor());
        delegates.add(productItemProcessor);
        compositeItermProcessor.setDelegates(delegates);
        return compositeItermProcessor;
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("xlsProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(getProductSummary())
                .build();
    }

    @Bean
    public Step getProductSummary() {
        return stepBuilderFactory.get("getProductSummary")
                .<Product, Product> chunk(CHUNK_SIZE)
                .reader(reader())
                .processor(compositeItemProcessor())
                .writer(writer)
                .faultTolerant()
                .skip(ExcelFileParseException.class)
                .skip(ValidationException.class)
                .skipLimit(SKIP_LIMIT)
                .listener(skipListener)
                .build();
    }
}
