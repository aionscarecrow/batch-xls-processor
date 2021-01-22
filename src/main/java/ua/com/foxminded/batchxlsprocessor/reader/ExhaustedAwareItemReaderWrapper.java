package ua.com.foxminded.batchxlsprocessor.reader;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

public class ExhaustedAwareItemReaderWrapper<T> implements ItemReader<T>, ItemStream {

    private ItemReader<T> delegate;
    private T nextItem = null;
    private boolean exhausted = false;
    
    private static final Logger LOG = LoggerFactory.getLogger(ExhaustedAwareItemReaderWrapper.class);


	@Override
    public T read() throws Exception {
    	
    	if(!this.exhausted) {
    		T returnItem = null;
    		if(Objects.isNull(nextItem)) {
    			returnItem = delegate.read();
    		} else {
    			returnItem = nextItem;
    		}
    		nextItem = delegate.read();
    		this.exhausted = Objects.isNull(nextItem);
    		
    		if(LOG.isTraceEnabled()) {
    			LOG.trace("Returning item: {}", returnItem);
    		}
    		
    		return returnItem;
    	} else {
    		LOG.debug("Delegate exhausted. Returning null.");
    		return null;
    	}
    }
    
    public boolean isExhausted() {
    	return this.exhausted;
    }

    public void setDelegate(ItemReader<T> delegate) {
        this.delegate = delegate;
    }

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		((ItemStream)delegate).open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		((ItemStream)delegate).update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		((ItemStream)delegate).close();
	}
}
