package org.example.nexignbootcamptask.service.impl;

import org.example.nexignbootcamptask.dto.CDRGenerationRequest;
import org.example.nexignbootcamptask.dto.CDRGenerationResponse;
import org.example.nexignbootcamptask.entity.CDRecord;
import org.example.nexignbootcamptask.entity.Subscriber;
import org.example.nexignbootcamptask.entity.enums.CallType;
import org.example.nexignbootcamptask.exception.ResourceNotFoundException;
import org.example.nexignbootcamptask.exception.ServiceException;
import org.example.nexignbootcamptask.repository.CDRecordRepository;
import org.example.nexignbootcamptask.repository.SubscriberRepository;
import org.example.nexignbootcamptask.service.CDRService;
import org.example.nexignbootcamptask.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class CDRServiceImpl implements CDRService {

    private final CDRecordRepository cdRecordRepository;
    private final SubscriberRepository subscriberRepository;

    private static final String reportsDirectory = "reports";

    private static final DateTimeFormatter dateTimeFormater = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final String[] MSISDN_LIST = {
            "79001112233", "79002223344", "79003334455", "79004445566", "79005556677",
            "79006667788", "79007778899", "79008889900", "79009990011", "79000001122"
    };

    @Autowired
    public CDRServiceImpl(CDRecordRepository cdRecordRepository,
                          SubscriberRepository subscriberRepository) {
        this.cdRecordRepository = cdRecordRepository;
        this.subscriberRepository = subscriberRepository;

        try {
            Path reportsPath = Paths.get(reportsDirectory);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create reports directory", e);
        }
    }

    @Transactional
    public void initializeSubscribers() {
        if (subscriberRepository.count() == 0) {
            List<Subscriber> subscribers = new ArrayList<>();

            for (String msisdn : MSISDN_LIST) {
                subscribers.add(Subscriber.builder().msisdn(msisdn).build());
            }

            subscriberRepository.saveAll(subscribers);
        }
    }

    @Transactional
    public void generateCDRecords() {
        List<Subscriber> subscribers = subscriberRepository.findAll();

        if (subscribers.isEmpty()) {
            throw new IllegalStateException("No subscribers found. Cannot generate CDR records");
        }

        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();

        int numberOfRecords = ThreadLocalRandom.current().nextInt(500, 1001);

        List<CDRecord> cdrRecords = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfRecords; i++) {
            Subscriber caller = subscribers.get(random.nextInt(subscribers.size()));

            Subscriber receiver;
            do {
                receiver = subscribers.get(random.nextInt(subscribers.size()));
            } while (receiver.getMsisdn().equals(caller.getMsisdn()));

            LocalDateTime callStart = getRandomDateTimeInRange(startDate, endDate);

            LocalDateTime callEnd = addRandomDuration(callStart, 10, 1800);

            CallType callType = random.nextBoolean() ? CallType.OUTCOMING : CallType.INCOMING;

            CDRecord cdRecord = CDRecord.builder()
                    .callType(callType)
                    .callingSubscriber(caller)
                    .receivingSubscriber(receiver)
                    .callStart(callStart)
                    .callEnd(callEnd)
                    .build();

            cdrRecords.add(cdRecord);
        }

        cdrRecords.sort(Comparator.comparing(CDRecord::getCallStart));

        cdRecordRepository.saveAll(cdrRecords);
    }

    public List<CDRecord> getCDRecordsBySubscriberAndDateRange(String msisdn, LocalDateTime startDate, LocalDateTime endDate) {
        ValidationUtil.validateMsisdn(msisdn);
        ValidationUtil.validateDateRange(startDate, endDate);

        List<CDRecord> records = cdRecordRepository.findAllBySubscriberMsisdnAndDateRange(msisdn, startDate, endDate);

        if (records.isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format("CDR records for subscriber %s in date range %s - %s",
                            msisdn, startDate, endDate));
        }

        return records;
    }

    public List<CDRecord> getOutgoingCallsBySubscriber(String msisdn, Integer month) {
        ValidationUtil.validateMsisdn(msisdn);
        if (month != null) {
            ValidationUtil.validateMonth(month);
            return cdRecordRepository.findByCallingSubscriber_MsisdnAndCallTypeAndMonth(msisdn, CallType.OUTCOMING, month);
        }
        return cdRecordRepository.findByCallingSubscriber_MsisdnAndCallType(msisdn, CallType.OUTCOMING);
    }

    public List<CDRecord> getIncomingCallsBySubscriber(String msisdn, Integer month) {
        ValidationUtil.validateMsisdn(msisdn);
        if (month != null) {
            ValidationUtil.validateMonth(month);
            return cdRecordRepository.findByReceivingSubscriber_MsisdnAndCallTypeAndMonth(msisdn, CallType.INCOMING, month);
        }
        return cdRecordRepository.findByReceivingSubscriber_MsisdnAndCallType(msisdn, CallType.INCOMING);
    }

    @Transactional
    public CDRGenerationResponse generateCDRReport(CDRGenerationRequest request) {
        validateCDRRequest(request);

        if (!subscriberRepository.existsById(request.getMsisdn())) {
            throw new ResourceNotFoundException("Subscriber", "msisdn", request.getMsisdn());
        }

        try {
            UUID requestId = UUID.randomUUID();

            String filename = request.getMsisdn() + "_" + requestId + ".csv";

            List<CDRecord> records = getCDRecordsBySubscriberAndDateRange(
                    request.getMsisdn(), request.getStartDate(), request.getEndDate());

            Path filePath = Paths.get(reportsDirectory, filename);

            writeCDRReportToCSV(records, filePath.toString());

            return CDRGenerationResponse.builder()
                    .requestId(requestId)
                    .build();
        } catch (Exception e) {
            throw new ServiceException("Failed to generate CDR report: " + e.getMessage(), e);
        }
    }

    private void writeCDRReportToCSV(List<CDRecord> records, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT)) {

            for (CDRecord record : records) {
                csvPrinter.printRecord(
                        record.getCallType().getIndex(),
                        record.getCallingSubscriber().getMsisdn(),
                        record.getReceivingSubscriber().getMsisdn(),
                        dateTimeFormater.format(record.getCallStart()),
                        dateTimeFormater.format(record.getCallEnd())
                );
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new ServiceException("Error writing CDR report to CSV", e);
        }
    }

    private void validateCDRRequest(CDRGenerationRequest request) {
        ValidationUtil.validateMsisdn(request.getMsisdn());
        ValidationUtil.validateDateRange(request.getStartDate(), request.getEndDate());
    }

    private LocalDateTime getRandomDateTimeInRange(LocalDateTime start, LocalDateTime end) {
        long startEpochSecond = start.toEpochSecond(java.time.ZoneOffset.UTC);
        long endEpochSecond = end.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpochSecond = ThreadLocalRandom.current().nextLong(startEpochSecond, endEpochSecond);

        return LocalDateTime.ofEpochSecond(randomEpochSecond, 0, java.time.ZoneOffset.UTC);
    }

    private LocalDateTime addRandomDuration(LocalDateTime dateTime, int minSeconds, int maxSeconds) {
        int randomSeconds = ThreadLocalRandom.current().nextInt(minSeconds, maxSeconds + 1);
        return dateTime.plusSeconds(randomSeconds);
    }
}
