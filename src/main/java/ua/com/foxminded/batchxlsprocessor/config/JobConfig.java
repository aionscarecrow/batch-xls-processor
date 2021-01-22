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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.ExcelFileParseException;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import ua.com.foxminded.batchxlsprocessor.domain.Product;
import ua.com.foxminded.batchxlsprocessor.listener.CustomSkipListener;
import ua.com.foxminded.batchxlsprocessor.mapper.ProductExcelRowMapper;
import ua.com.foxminded.batchxlsprocessor.processor.ProductProcessor;
import ua.com.foxminded.batchxlsprocessor.reader.ExhaustedAwareItemReaderWrapper;
import ua.com.foxminded.batchxlsprocessor.service.ProductGroupingService;
import ua.com.foxminded.batchxlsprocessor.writer.ProductLogWriter;

@Configuration
@EnableBatchProcessing
public class JobConfig {

    private static final int SKIP_LIMIT = 300;

    @Value("${file.input}")
    private String fileInput;

    @Bean("reader")
    public ItemReader<Product> reader() {
    	
        PoiItemReader<Product> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource(fileInput));
        reader.setRowMapper(new ProductExcelRowMapper());
        return reader;
    }
    
    @Bean("readerWrapper")
    @StepScope
    public ExhaustedAwareItemReaderWrapper<Product> exhaustedWrapper(
    		@Qualifier("reader") ItemReader<Product> reader) {
    	
    	ExhaustedAwareItemReaderWrapper<Product> wrapper = 
    			new ExhaustedAwareItemReaderWrapper<>();
    	wrapper.setDelegate(reader);
    	return wrapper;
    }
    
    @Bean("logWriter")
    @StepScope
    public ItemWriter<Product> productLogWriter() {
    	return new ProductLogWriter();
    }
    

    @Bean
    public org.springframework.validation.Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean("batchValidator")
    public Validator<Product> batchValidator() {
    	
        SpringValidator<Product> springValidator = new SpringValidator<>();
        springValidator.setValidator(validator());
        return springValidator;
    }
    
    @Bean
    public ProductGroupingService groupingService() {
    	return new ProductGroupingService();
    }
    
    @Bean
    @Lazy
    public ItemProcessor<Product, Product> productProcessor(
    		@Qualifier("readerWrapper")ExhaustedAwareItemReaderWrapper<Product> reader, 
    		@Qualifier("logWriter")ItemWriter<Product> writer,
    		@Qualifier("batchValidator")Validator<Product> batchValidator
    		) {
    	
    	ValidatingItemProcessor<Product> processor = 
    			new ProductProcessor(reader, writer, batchValidator);
    	processor.setFilter(false);
    	return processor;
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
    public Step step1(
    		StepBuilderFactory stepBuilderFactory, 
    		CustomSkipListener skipListener, 
    		ItemProcessor<Product, Product> productProcessor,
    		ExhaustedAwareItemReaderWrapper<Product> wrapper) {
    	
        return stepBuilderFactory.get("step1")
                .<Product, Product> chunk(3)
                .reader(wrapper)
                .processor(productProcessor)
                .writer(productLogWriter())
                .faultTolerant()
                .skip(ExcelFileParseException.class)
                .skip(ValidationException.class)
                .skipLimit(SKIP_LIMIT)
                .listener(skipListener)
                .build();
    }
}
