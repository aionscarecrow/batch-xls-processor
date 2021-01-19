package ua.com.foxminded.batchxlsprocessor.config;

import java.lang.reflect.InvocationTargetException;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.AbstractFileItemWriter;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.Validator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

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
    
    @Bean //HERE
    public ItemWriterWrapper getWrapperWriter(AbstractFileItemWriter<Product> writer) {
    	return new ItemWriterWrapper(writer);
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
    public Job job(Step step1, Step step2) {
        return jobBuilderFactory.get("xlsProcessingJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .next(step2)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemWriterWrapper writer, Step step2) {
        TaskletStep step = stepBuilderFactory.get("step1")
                .<Product, Product> chunk(1)
                .reader(reader())
                .processor(validatingItemProcessor())
                .writer(writer)
                .build();
        // HERE
        step.registerStepExecutionListener(new StepExecutionListener() {
					@Override
					public void beforeStep(StepExecution stepExecution) {}

					@Override
					public ExitStatus afterStep(StepExecution stepExecution) {
						System.out.println("From inside stepExecutionListener after step1");
						
						try {
							// BAD
							writer.getClass().getDeclaredMethod("printContents", new Class[] {}).invoke(writer);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException e) {
							System.out.println("Ops!: " + e.getMessage());
						}
						
						return ExitStatus.COMPLETED;
					}
                	
                });
        return step;
    }
    
    @Bean
    public Step step2(FlatFileItemWriter<Product> writer) {
    	return stepBuilderFactory.get("testStep")
    		.tasklet(new Tasklet() {

				@Override
				public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
					System.out.println("Executing from tasklet: " + Thread.currentThread().getName());
					
					return RepeatStatus.FINISHED;
				}
    			
    		})
            .build();
    	
    }
}
