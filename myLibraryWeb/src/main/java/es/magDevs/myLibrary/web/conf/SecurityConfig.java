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
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.magDevs.myLibrary.model.Constants.SECTION;

/**
 * Clases para configurar la seguridad de la aplicacion
 * 
 * @author javier.vaquero
 * 
 */
@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		auth.inMemoryAuthentication().passwordEncoder(encoder)
				.withUser("user").password("$2a$10$u/gTxtfnnenV6H.WbGJ5p.dapEUibiD1lw8jvsyEJYTUZfzYSwVmy")
				.roles("USER").and()
				.withUser("josea").password("$2a$10$JgxHKBSw9z0XutV1.VIXGOS8WS1BFIFcGC2a5c1L7iHofrFQUAa32")
				.roles("USER").and()
				.withUser("admin").password("$2a$10$u/gTxtfnnenV6H.WbGJ5p.dapEUibiD1lw8jvsyEJYTUZfzYSwVmy")
				.roles("USER", "ADMIN");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry r = http
				.authorizeRequests();
		//Permitimos a cualquiera ver listados
		// Recorremos todas las secciones disponibles
		for (SECTION section : SECTION.values()) {
			r.regexMatchers(HttpMethod.GET, "/"+section.get()).permitAll();
			r.regexMatchers(HttpMethod.GET, "/"+section.get()+"next").permitAll();
			r.regexMatchers(HttpMethod.GET, "/"+section.get()+"previous").permitAll();
			r.regexMatchers(HttpMethod.GET, "/"+section.get()+"start").permitAll();
			r.regexMatchers(HttpMethod.GET, "/"+section.get()+"end").permitAll();
			r.regexMatchers(HttpMethod.GET, "/"+section.get()+"pageSize").permitAll();
			r.regexMatchers(HttpMethod.POST, "/"+section.get()+"search").permitAll();
			r.regexMatchers(HttpMethod.POST, "/"+section.get()+"read").permitAll();
			r.regexMatchers(HttpMethod.GET, "/"+section.get()+"\\?language=[a-zA-Z]{2}").permitAll();
		}
		//Permitimos acceso a la pantalla de login a cualquiera
		r.regexMatchers(HttpMethod.GET, "/loginForm").permitAll();
		r.regexMatchers("/login?logout").permitAll();
		//Permitimos a cualquiera obtener los recursos (/img, /js o /css)
		r.antMatchers("/img/**").permitAll();
		r.antMatchers("/js/**").permitAll();
		r.antMatchers("/css/**").permitAll();
		//Permitimos a cualquiera ver la pagina inicial
		r.antMatchers("/").permitAll();
		// Hay que ser usuario para cualquier otra accion
		r.antMatchers("/**").hasRole("USER");
		http.formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/books", false);
		http.logout().logoutSuccessUrl("/loginForm?logout").permitAll();
		http.rememberMe().tokenValiditySeconds(Integer.MAX_VALUE);
		http.csrf();
	}
}