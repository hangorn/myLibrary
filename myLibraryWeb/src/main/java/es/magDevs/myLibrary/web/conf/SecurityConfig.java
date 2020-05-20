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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import es.magDevs.myLibrary.model.Constants;

/**
 * Clases para configurar la seguridad de la aplicacion
 * 
 * @author javier.vaquero
 * 
 */
@Configuration
@EnableWebSecurity
@ComponentScan(value={"es.magDevs.myLibrary.*"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    @Qualifier(value="userDetailsService")
    UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("rawtypes")
	protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry r = http.authorizeRequests();
        //Para todas las secciones, siempre se tendra acceso a listar, consultar y cambiar de idioma
        for (Constants.SECTION section : Constants.SECTION.values()) {
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/" + section.get()})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/" + section.get() + "\\?"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/" + section.get() + "_next"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/" + section.get() + "_previous"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/" + section.get() + "_start"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/" + section.get() + "_end"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.POST, new String[]{"/" + section.get() + "_pageSize"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.POST, new String[]{"/" + section.get() + "_search"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.POST, new String[]{"/" + section.get() + "_read"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/" + section.get() + "_readid\\?readid=\\d+"})).permitAll();
            ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/" + section.get() + "\\?language=[a-zA-Z]{2}"})).permitAll();
        }
        //Siempre se tendra acceso a formulario para entrar y salir, y a la pagina inicial
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/loginForm"})).permitAll();
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.regexMatchers(HttpMethod.GET, new String[]{"/loginForm\\?"})).permitAll();
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.antMatchers(new String[]{"/"})).permitAll();
        //Siempre se tendra acceso a los fichero de recursos (imagenes, scripts y css)
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.antMatchers(new String[]{"/img/**"})).permitAll();
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.antMatchers(new String[]{"/js/**"})).permitAll();
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.antMatchers(new String[]{"/css/**"})).permitAll();
        //Para todo lo demas se necesita rol de usuario registrado
        ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)r.antMatchers(new String[]{"/**"})).hasRole("USER");
        
        //Cuando un usuario entre, se redirige a la seccion de libros
        http.formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/books", false);
        //Cuando un usuario sale, se redirige al formulario para entrar
        http.logout().logoutSuccessUrl("/loginForm?logout").permitAll();
        //Simpre se recordara al usuario
        http.rememberMe().tokenValiditySeconds(Integer.MAX_VALUE);
        http.csrf();
        
		// Añadimos un filtro de codificacion antes del de seguridad, esto no
		// tiene mucho que ver con la seguridad pero no hay otro sitio donde
		// ponerlo
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter,CsrfFilter.class);
    }
}