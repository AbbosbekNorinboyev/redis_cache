package uz.brb.redis_cache.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.brb.redis_cache.dto.response.Response;
import uz.brb.redis_cache.entity.ApiLog;
import uz.brb.redis_cache.repository.ApiLogRepository;
import uz.brb.redis_cache.service.ApiLogService;

import java.time.LocalDateTime;
import java.util.List;

import static uz.brb.redis_cache.util.Util.localDateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ApiLogServiceImpl implements ApiLogService {
    private final ApiLogRepository apiLogRepository;

    @Override
    public Response<?> saveLog(String username, String method, String path, LocalDateTime time, long duration) {
        ApiLog apiLog = ApiLog.builder()
                .username(username)
                .method(method)
                .path(path)
                .timestamp(time)
                .durationMs(duration)
                .build();
        apiLogRepository.save(apiLog);
        return Response.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .success(true)
                .message("ApiLog successfully saved")
                .timestamp(localDateTimeFormatter(LocalDateTime.now()))
                .build();
    }

    @Override
    @Cacheable(value = "apiLogs", key = "#root.methodName")
    public Response<?> getAll(Pageable pageable) {
        List<ApiLog> apiLogs = apiLogRepository.findAll(pageable).getContent();
        return Response.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .success(true)
                .message("ApiLog list successfully found")
                .data(apiLogs)
                .timestamp(localDateTimeFormatter(LocalDateTime.now()))
                .build();
    }
}
