package com.tracko.backend.scheduler;

import com.tracko.backend.model.Quotation;
import com.tracko.backend.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuotaSlaCheckJob implements Job {

    private final QuotationRepository quotationRepository;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting quotation SLA check job");

        List<Quotation> nearingBreach = quotationRepository
            .findBySlaDeadlineBeforeAndSlaBreached(
                LocalDateTime.now().plusHours(2), false);

        for (Quotation quotation : nearingBreach) {
            if (quotation.getSlaDeadline().isBefore(LocalDateTime.now())) {
                quotation.setSlaBreached(true);
                quotationRepository.save(quotation);
                log.warn("SLA breached for quotation: {}", quotation.getQuotationNumber());
            }
        }

        log.info("Quotation SLA check completed. Found {} quotations nearing breach", nearingBreach.size());
    }
}
