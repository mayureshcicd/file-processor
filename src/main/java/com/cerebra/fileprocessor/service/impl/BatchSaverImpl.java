package com.cerebra.fileprocessor.service.impl;

import com.cerebra.fileprocessor.entity.ProcessedMessage;
import com.cerebra.fileprocessor.service.BatchSaver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchSaverImpl implements BatchSaver {

    private static final int BATCH_SIZE = 1000;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveBatch(List<ProcessedMessage> batch) {
        log.info("Saving batch of size: {}", batch.size());

        try {
            for (int i = 0; i < batch.size(); i++) {
                entityManager.persist(batch.get(i));

                // Flush and clear the persistence context periodically
                if (i > 0 && i % BATCH_SIZE == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            // Final flush and clear
            entityManager.flush();
            entityManager.clear();

            log.info("Batch saved successfully.");
        } catch (Exception e) {
            log.error("Error saving batch: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }

}
