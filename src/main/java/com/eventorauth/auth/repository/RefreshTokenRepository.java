package com.eventorauth.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.eventorauth.auth.dto.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
