package com.cerebra.fileprocessor.service;

import com.cerebra.fileprocessor.entity.ProcessedMessage;

import java.util.List;

public interface BatchSaver {
     void saveBatch(List<ProcessedMessage> batch);
}
