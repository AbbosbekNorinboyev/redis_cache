package uz.brb.redis_cache.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.brb.redis_cache.dto.response.Response;
import uz.brb.redis_cache.entity.ApiLog;
import uz.brb.redis_cache.repository.ApiLogRepository;
import uz.brb.redis_cache.service.ApiLogService;
import uz.brb.redis_cache.service.logic.RedisCacheService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static uz.brb.redis_cache.util.Util.localDateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ApiLogServiceImpl implements ApiLogService {
    private final ApiLogRepository apiLogRepository;
    private final RedisCacheService redisCacheService;

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
    public Response<?> getAll(Pageable pageable) {
        List<ApiLog> apiLogs = apiLogRepository.findAll(pageable).getContent();
        for (ApiLog apiLog : apiLogs) {
            redisCacheService.saveData(String.valueOf(apiLog.getId()), apiLog, 10, TimeUnit.MINUTES);
        }
        List<Object> redisCacheAllData = redisCacheService.getAllData();
        return Response.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .success(true)
                .message("ApiLog list successfully found")
                .data(redisCacheAllData)
                .timestamp(localDateTimeFormatter(LocalDateTime.now()))
                .build();
    }
}
