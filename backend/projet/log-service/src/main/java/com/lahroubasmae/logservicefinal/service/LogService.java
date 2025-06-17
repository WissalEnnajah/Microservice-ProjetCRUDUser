package com.lahroubasmae.logservicefinal.service;

import com.lahroubasmae.logservicefinal.model.Log;
import com.lahroubasmae.logservicefinal.repository.LogRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public Log saveLog(Log log) {
        log.setTimestamp(new Date()); // Ajout automatique de la date
        return logRepository.save(log);
    }

    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }

    public List<Log> getLogsByUser(String email) {
        return logRepository.findByUserEmail(email);
    }
}