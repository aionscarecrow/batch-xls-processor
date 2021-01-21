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
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
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
import ua.com.foxminded.batchxlsprocessor.processor.ProductProcessor;
import ua.com.foxminded.batchxlsprocessor.writer.ProductLogWriter;

@Configuration
@EnableBatchProcessing
public class JobConfig {

    private static final int SKIP_LIMIT = 300;

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
    
    //HERE
    @Bean
    @StepScope
    public SingleItemPeekableItemReader<Product> peekItemReader() {
    	SingleItemPeekableItemReader<Product> reader = new SingleItemPeekableItemReader<>();
    	reader.setDelegate(reader());
    	return reader;
    }
    
    //HERE
    @Bean
    public ItemWriter<Product> productLogWriter() {
    	return new ProductLogWriter();
    }
    
    //HERE
    @Bean
    public ItemProcessor<Product, Product> productProcessor() {
    	
    	return new ProductProcessor(peekItemReader(), productLogWriter());
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
        validatingItemProcessor.setFilter(false);
        return validatingItemProcessor;
    }
    
    // HERE. WHY?
//    @Bean
//    public ItemProcessListener<Product, Product> processListener() {
//    	ItemProcessListener<Product, Product> processListener = new ProductProcessListener();
//    	return processListener;
//    }

    @Bean
    public Job job(Step step1, JobBuilderFactory jobBuilderFactory,
			JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("xlsProcessingJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }
    
    //HERE. REMOVE?
//    @Bean
//    public ChunkMonitor chunkMonitor() {
//    	ChunkMonitor chunkMonitor = new ChunkMonitor();
//		chunkMonitor.registerItemStream(peekItemReader());
//		return chunkMonitor;
//    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, CustomSkipListener skipListener) {
        return stepBuilderFactory.get("step1")
                .<Product, Product> chunk(3) // HERE
//                .reader(reader()) // HERE
                .reader(peekItemReader())
//                .processor(validatingItemProcessor()) //HERE
                .processor(productProcessor())
//                .writer(mapProductWriter) HERE
                .writer(productLogWriter())
                .faultTolerant()
                .skip(ExcelFileParseException.class)
                .skip(ValidationException.class)
                .skipLimit(SKIP_LIMIT)
                .listener(skipListener)
//                .listener(processListener)
                .build();
    }
}
