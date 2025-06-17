package com.lahroubasmae.logservicefinal.controller;

import com.lahroubasmae.logservicefinal.model.Log;
import com.lahroubasmae.logservicefinal.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private LogService logService;

    // 📌 Endpoint principal pour les admins
    @PostMapping
    public ResponseEntity<Log> logAction(@RequestBody Log log) {
        Log savedLog = logService.saveLog(log);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }

    // ✅ Nouveau endpoint pour les services externes via WebClient
    @PostMapping("/external")
    public ResponseEntity<String> logFromOtherService(@RequestBody String message) {
        Log log = new Log();
        log.setAction(message); 
        log.setUserEmail("system"); 
        log.setTimestamp(new Date());
        logService.saveLog(log);
        return ResponseEntity.ok("Log reçu avec succès !");
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Log>> getAllLogs() {
        return ResponseEntity.ok(logService.getAllLogs());
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Log>> getLogsByUser(@PathVariable String email) {
        return ResponseEntity.ok(logService.getLogsByUser(email));
    }
}
