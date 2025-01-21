package com.eventorauth.oauth.config;

import java.util.Map;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eventorauth.oauth.adapter.OauthAdapter;
import com.eventorauth.oauth.repository.InMemoryOauthRepository;

@Configuration
@EnableConfigurationProperties(OauthProperties.class)
public class OauthConfig {

	private final OauthProperties properties;

	public OauthConfig(OauthProperties properties) {
		this.properties = properties;
	}

	@Bean
	public InMemoryOauthRepository inMemoryOauthRepository() {
		Map<String, OauthProvider> oauthProviders = OauthAdapter.getOauthProviders(properties);
		return new InMemoryOauthRepository(oauthProviders);
	}
}
