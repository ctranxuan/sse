package org.ws13.learning.sse.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author ctranxuan
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        /*
         * see http://stackoverflow.com/questions/29880336/spring-sseemitter-causes-cannot-forward-after-response-has-been-committed-except
         * Not really satisfactory from my point of view too...
         */
        configurer.setDefaultTimeout(1000000);
    }
}
