package com.cerebra.fileprocessor.service.impl;

import com.cerebra.fileprocessor.common.PageableObject;
import com.cerebra.fileprocessor.common.ResponseUtil;
import com.cerebra.fileprocessor.config.ConfigProperties;
import com.cerebra.fileprocessor.dto.ProcessFileDto;
import com.cerebra.fileprocessor.entity.ProcessFile;
import com.cerebra.fileprocessor.entity.ProcessedMessage;
import com.cerebra.fileprocessor.entity.User;
import com.cerebra.fileprocessor.events.FileUploadedEvent;
import com.cerebra.fileprocessor.exceptions.RestApiException;
import com.cerebra.fileprocessor.repository.ProcessFileRepository;
import com.cerebra.fileprocessor.repository.ProcessedMessageRepository;
import com.cerebra.fileprocessor.repository.UserRepository;
import com.cerebra.fileprocessor.response.RestApiResponse;
import com.cerebra.fileprocessor.service.BatchSaver;
import com.cerebra.fileprocessor.service.FileProcessingService;
import com.cerebra.fileprocessor.service.JwtService;
import com.cerebra.fileprocessor.service.NotificationService;
import com.cerebra.fileprocessor.util.FileParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingServiceImpl implements FileProcessingService {

    private static final int BATCH_SIZE = 1000;
    private final UserRepository userRepository;
    private final ProcessFileRepository processFileRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final JwtService jwtService;
    private final ConfigProperties configProperties;
    private final PageableObject pageableObject;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final BatchSaver batchSaver;
    private final Executor asyncExecutor;

    @Override
    @Async("fileProcessingExecutor")
    public void processFileAsync(ProcessFile processFile) {
        log.info("your notify section started............................ {} ", processFile.getSystemFileName());
        fileProcessScheduler(ResponseUtil.getFileBytes(processFile.getSystemFileName(), configProperties.getUploadDirectory()), processFile);
    }

    @Override
    @CacheEvict(value = {"process", "processAll"}, allEntries = true)
    public void fileProcessScheduler(byte[] fileBytes, ProcessFile processFile) {
        List<ProcessedMessage> processedMessageList = new ArrayList<>();
        try {

            List<String> messages = FileParser.parseFile(fileBytes, ResponseUtil.getFileExtension(processFile.getSystemFileName()));
            log.info("Starting file processing...");
            log.info("Total rows in file: {}", messages.size());
            messages.forEach(message -> {
                processedMessageList.add(
                        ProcessedMessage.builder()
                                .message(message)
                                .processFile(processFile)
                                .build());
            });

            processFile.setProcessed(true);

        } catch (Exception e) {
            log.info("Exception occurred Process file messages............................ {} ", e.getMessage());
            processFile.setProcessed(false);
            processedMessageList.clear();
            processedMessageList.add(
                    ProcessedMessage.builder()
                            .message(String.format("%s File has Invalid Content.", processFile.getOriginalFileName()))
                            .processFile(processFile)
                            .build());
        } finally {
            if (!processedMessageList.isEmpty()) {
                log.info("Save processed Message List Total rows in file: {}", processedMessageList.size());
                processFile(processedMessageList, processFile);

            }

        }

    }

    @Override
    public RestApiResponse<String> processFile(HttpServletRequest requestH, MultipartFile fileProcess) {
        String email = jwtService.getAttributeValue(jwtService.getAuthorization(requestH), "email");
        String fileName = ResponseUtil.validation(fileProcess);
        return userRepository.findByEmail(email)
                .map(user -> saveFileMetadata(user, fileProcess, fileName)
                )
                .orElseThrow(() -> new RestApiException("Invalid user", HttpStatus.BAD_REQUEST));
    }

    @Override
    @Transactional
    public RestApiResponse<Page<ProcessFileDto>> getProcessFile(HttpServletRequest requestH, Pageable pageable) {
        String email = jwtService.getAttributeValue(jwtService.getAuthorization(requestH), "email");
        return getProcessFile(email, pageable);
    }

    @Cacheable(value = {"process"}, key = "T(java.util.Objects).hash(#email , #pageable.pageNumber, #pageable.pageSize)",
            unless = "@cacheHelper.isResultEmpty(#result)")
    private RestApiResponse<Page<ProcessFileDto>> getProcessFile(String email, Pageable pageable) {
        Map<String, String> validSort = new HashMap<>();
        validSort.put("id", "id");
        pageable = pageableObject.isValid(pageable, validSort, "id");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException("Invalid user", HttpStatus.BAD_REQUEST));
        Page<ProcessFile> process = null;

        if (user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            process = processFileRepository.findAll(pageable);
        } else {
            process = processFileRepository.findByUserId(user.getId(), pageable);
        }

        Page<ProcessFileDto> processList = pageableObject.mapPage(process, ProcessFileDto.class);

        return ResponseUtil.getResponse(processList, "Process File List");
    }

    @Transactional
    private RestApiResponse<String> saveFileMetadata(User user, MultipartFile fileProcess, String fileName) {
        ProcessFile fileEntity = ProcessFile.builder().originalFileName(fileProcess.getOriginalFilename())
                .systemFileName(ResponseUtil.uploadFile(fileProcess, StringUtils.isEmpty(configProperties.getUploadDirectory()) ? ResponseUtil.UPLOAD_DIR : configProperties.getUploadDirectory()))
                .fileSize(ResponseUtil.getReadableFileSize(fileProcess.getSize()))
                .uploadDate(LocalDateTime.now())
                .processed(false)
                .user(user).build();
        processFileRepository.saveAndFlush(fileEntity);
        // Publish an event AFTER returning a response
        eventPublisher.publishEvent(new FileUploadedEvent(this, fileEntity));
        return ResponseUtil.getResponse(String.format("%s File is uploaded and processing started.", fileName));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"process", "processAll"}, key = "#id", allEntries = true)
    public RestApiResponse<String> deleteProcessFile(Long id) {

        return ResponseUtil.getResponse(processFileRepository.findById(id)
                .map(process -> {
                    processedMessageRepository.deleteByProcessFileId(process.getId());
                    ResponseUtil.deleteFile(process.getSystemFileName(), StringUtils.isEmpty(configProperties.getUploadDirectory()) ? ResponseUtil.UPLOAD_DIR : configProperties.getUploadDirectory());
                    processFileRepository.delete(process);
                    return "Process file deleted successfully";
                })
                .orElseThrow(() -> new RestApiException("Process file not found", HttpStatus.BAD_REQUEST)));
    }


    private void saveBatch(List<ProcessedMessage> batch) {
        batchSaver.saveBatch(batch);
    }

    @Async("asyncExecutor")
    private CompletableFuture<Void> saveProcessedMessages(List<ProcessedMessage> processedMessageList) {
        return CompletableFuture.runAsync(() -> {
            List<List<ProcessedMessage>> batches = splitIntoBatches(processedMessageList);
            batches.forEach(this::saveBatch);
        }, asyncExecutor);
    }

    private void processFile(List<ProcessedMessage> processedMessageList, ProcessFile processFile) {
        // Start saving asynchronously and wait for it to complete
        CompletableFuture<Void> saveFuture = saveProcessedMessages(processedMessageList);

        saveFuture.thenRun(() -> {
            try {
                // After saving completes, execute the remaining part
                log.info("All transactions successfully saved, processFile {} ", processFile);
                processFileRepository.saveAndFlush(processFile);
                log.info("All transactions successfully saved, now notifying user!");

                notificationService.notifyFileProcessStatusToMonitor(
                        processFile.getUser().getEmail(),
                        String.format("Hi, %s your %s File is successfully processed.",
                                processFile.getUser().getFirstName(), processFile.getOriginalFileName())
                );

                Optional<User> user = userRepository.findByUserType("ADMIN");
                user.ifPresent(u -> notificationService.notifyFileProcessStatusToMonitor(u.getEmail(), ""));
            } catch (Exception ex) {
                log.error("Error occurred after saving transactions: {}", ex.getMessage());
            }
        }).exceptionally(ex -> {
            log.error("Error occurred while processing file: {}", ex.getMessage());
            return null;
        });
    }

    private List<List<ProcessedMessage>> splitIntoBatches(List<ProcessedMessage> list) {
        List<List<ProcessedMessage>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            batches.add(list.subList(i, Math.min(i + BATCH_SIZE, list.size())));
        }
        return batches;
    }
}