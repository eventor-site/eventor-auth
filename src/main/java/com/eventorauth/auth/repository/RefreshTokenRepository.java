package com.eventorauth.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.sikyeojoauth.auth.dto.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
