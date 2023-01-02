/**
 * Copyright (c) 2014-2015, Javier Vaquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
package es.magDevs.myLibrary.web.conf;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.view.LiteDeviceDelegatingViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


/**
 * Configuracion de Spring mediante anotaciones
 * 
 * @author javier.vaquero
 *
 */
@EnableWebMvc @Configuration
@ComponentScan(value={"es.magDevs.myLibrary.*"})
public class Config implements WebMvcConfigurer {
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	//Establece directorios de recursos
        registry.addResourceHandler(new String[]{"/img/**"}).addResourceLocations(new String[]{"/img/"});
        registry.addResourceHandler(new String[]{"/js/**"}).addResourceLocations(new String[]{"/js/"});
        registry.addResourceHandler(new String[]{"/css/**"}).addResourceLocations(new String[]{"/css/"});
    }

	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean(name={"messageSource"})
    public ResourceBundleMessageSource getMessageSource() {
    	//Establece los ficheros de mensajes
        ResourceBundleMessageSource resource = new ResourceBundleMessageSource();
        resource.setBasenames(new String[]{"Messages", "configuration"});
        return resource;
    }

    @Bean(name={"localeResolver"})
    public SessionLocaleResolver getSessionLocaleResolver() {
    	//Para intenacionalizacion
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(new Locale("es"));
        return sessionLocaleResolver;
    }

    @Bean(name={"localeChangeInterceptor"})
    public LocaleChangeInterceptor getLocaleChangeInterceptor() {
		// Para intenacionalizacion, fijamos un interceptor que la gestionara y
		// configuramos el parametro en el que se recibira el idioma 
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

    @Bean(name={"templateResolver"})
    public ServletContextTemplateResolver getServletContextTemplateResolver() {
    	//Gestor de plantillas, fijamos el tipo a HTML 5 y la codificacion a UTF-8
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean(name={"templateEngine"})
    public SpringTemplateEngine getSpringTemplateEngine() {
    	//El motor de plantillas sera el de Thymeleaf
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.setTemplateResolver(getServletContextTemplateResolver());
        return springTemplateEngine;
    }

    @Bean
    public LiteDeviceDelegatingViewResolver getLiteDeviceDelegatingViewResolver() {
        ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
        thymeleafViewResolver.setViewClass(CustomThymeleafView.class);
        thymeleafViewResolver.setTemplateEngine(getSpringTemplateEngine());
        thymeleafViewResolver.setCharacterEncoding("UTF-8");
        thymeleafViewResolver.setContentType("text/html;charset=UTF-8");
        LiteDeviceDelegatingViewResolver delegatingViewResolver = new LiteDeviceDelegatingViewResolver(thymeleafViewResolver);
        delegatingViewResolver.setMobilePrefix("mobile/");
        delegatingViewResolver.setTabletPrefix("mobile/");
        return delegatingViewResolver;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DeviceResolverHandlerInterceptor());
        registry.addInterceptor(getLocaleChangeInterceptor());
    }
}