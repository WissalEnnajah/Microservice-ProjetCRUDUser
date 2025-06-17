package com.lahroubasmae.logservicefinal.repository;

import  com.lahroubasmae.logservicefinal.model.Log;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LogRepository extends MongoRepository<Log, String> {
    List<Log> findByUserEmail(String email);
}