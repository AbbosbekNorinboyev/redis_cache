package uz.brb.redis_cache.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import uz.brb.redis_cache.dto.response.Response;
import uz.brb.redis_cache.dto.response.UserResponse;
import uz.brb.redis_cache.entity.AuthUser;

@Component
public interface UserService {

    Response<?> get(Long id);

    Response<?> getAll(Pageable pageable);

    Response<?> update(UserResponse userResponse);

    Response<?> me(AuthUser authUser);

    Response<?> roleStatistics();

    Response<?> search(String fullName, String username);
}