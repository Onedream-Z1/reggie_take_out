package cn.xz.reggie.config;

import cn.xz.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 当程序时，我们的webMVC配置自动加载
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport{
    /**
     * 设置静态资源隐射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用功能Jackson将Java对象转换为JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将我们的消息转换器添加到HttpMessageConverter集合最终，并设置在第一个
        converters.add(0,messageConverter);
    }
}
