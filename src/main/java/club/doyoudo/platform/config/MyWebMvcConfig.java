package club.doyoudo.platform.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加虚拟路径映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:E:/laboratory/照片核对项目/Collate-Platform/static/images/");
        registry.addResourceHandler("/videos/**").addResourceLocations("file:E:/laboratory/照片核对项目/Collate-Platform/static/videos/");
        registry.addResourceHandler("/others/**").addResourceLocations("file:E:/laboratory/照片核对项目/Collate-Platform/static/others/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fjc = new FastJsonConfig();
        // 配置序列化策略
        fjc.setSerializerFeatures(SerializerFeature.BrowserCompatible);
        fastJsonConverter.setFastJsonConfig(fjc);
        converters.add(0, fastJsonConverter);
    }
}