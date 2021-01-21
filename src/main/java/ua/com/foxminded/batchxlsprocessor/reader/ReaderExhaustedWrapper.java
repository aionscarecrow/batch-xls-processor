package ua.com.foxminded.batchxlsprocessor.reader;

import java.util.Arrays;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * A Reader Implementation for 
 * http://stackoverflow.com/questions/8837487/how-to-process-logically-related-rows-after-itemreader-in-springbatch.
 * 
 * Wraps another reader and peeks ahead for each read to know beforehand if the 
 * delegate reader is exhausted. Propagates the information to the StepExecutionContext.
 * 
 * Is stateful and not threadsafe.
 * 
 * @author Michael R. Lange <michael.r.lange@langmi.de>
 */
public class ReaderExhaustedWrapper<T> implements ItemReader<T> {

    private ItemReader<T> delegate;
    private T nextItem = null;
    private boolean exhausted = false;
    
    private static final Logger log = LoggerFactory.getLogger(ReaderExhaustedWrapper.class);
    
    @Autowired
    AbstractApplicationContext context;
    
    
    

//@Override
	public T readz() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Arrays.stream(context.getBeanNamesForType(ItemReader.class)).forEach(System.out::println);
		return null;
	}

	    @Override
    public T read() throws Exception {
    	log.info("read() called");
    	
    	if(!this.exhausted) {
    		T returnItem = null;
    		if(Objects.isNull(nextItem)) {
    			returnItem = delegate.read();
    		} else {
    			returnItem = nextItem;
    		}
    		nextItem = delegate.read();
    		this.exhausted = Objects.isNull(nextItem);
    		
    		log.info("Returning item: {}", returnItem);
    		return returnItem;
    	} else {
    		log.info("Delegate exhausted. Returning null.");
    		return null;
    	}
        // check if delegate already exhausted to avoid an unnecessary delegate.read()
//        if (!this.exhausted) {
//            T returnItem;
//            // next filled ? 
//            if (nextItem != null) {
//                returnItem = nextItem;
//                nextItem = null;
//            } else {
//                // standard read
//                returnItem = delegate.read();
//                log.info("read: " + returnItem);
//            }
//
//            // try to peek one item ahead
//            nextItem = delegate.read();
//            // last item reached?
//            if (nextItem == null) {
//                this.exhausted = true;
//            }
//            log.info("returning {}", returnItem);
//            return returnItem;
//        } else {
//        	log.info("returning null");
//            return null;
//        }
    }
    
    public boolean isExhausted() {
    	return this.exhausted;
    }

    public void setDelegate(ItemReader<T> delegate) {
        this.delegate = delegate;
    }
}
