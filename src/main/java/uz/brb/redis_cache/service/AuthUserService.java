package uz.brb.redis_cache.service;

import uz.brb.redis_cache.dto.request.LoginRequest;
import uz.brb.redis_cache.dto.request.RegisterRequest;
import uz.brb.redis_cache.dto.response.Response;

public interface AuthUserService {
    Response<?> register(RegisterRequest registerRequest);

    Response<?> login(LoginRequest loginRequest);
}