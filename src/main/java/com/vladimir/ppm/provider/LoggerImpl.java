package com.vladimir.ppm.provider;

import com.vladimir.ppm.domain.Acts;
import com.vladimir.ppm.domain.LogRecord;
import com.vladimir.ppm.domain.Objects;
import com.vladimir.ppm.repository.LoggerRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
@EnableScheduling
public class LoggerImpl implements Logger {
    private final LoggerRepository repository;
    private final SettingsProvider settingsProvider;

    public LoggerImpl(LoggerRepository repository, SettingsProvider settingsProvider) {
        this.repository = repository;
        this.settingsProvider = settingsProvider;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void cleanUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -settingsProvider.getLogLifeTime());
        Date cleanUpDate = calendar.getTime();
        long countBefore = repository.count();
        repository.deleteAllByDateBefore(cleanUpDate);
        long countAfter = repository.count();
        log("System", Acts.DELETE, Objects.LOGS, "Logs", new Date(),
                "Deleted " + (countBefore - countAfter) + " records older than " + cleanUpDate);
    }

    @Override
    public void log(String user, Acts act, Objects object, String objName, Date date, String comment) {
        LogRecord record = new LogRecord(user, act, object, objName, date, comment);
        repository.save(record);
    }
}
