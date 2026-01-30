package com.savvy.gradeservice.seed;
import com.savvy.gradeservice.entity.Subject;
import com.savvy.gradeservice.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class SubjectSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(SubjectSeeder.class);
    
    private final SubjectRepository subjectRepository;
    
    public SubjectSeeder(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("=== SubjectSeeder starting ===");
        
        // Check existing subjects
        long count = subjectRepository.count();
        logger.info("Current subject count in database: {}", count);
        
        if (count > 0) {
            logger.info("Subjects already exist, skipping seeding");
            return;
        }
        
        logger.info("Seeding subjects...");
        
        Subject[] subjects = {
            new Subject("MATH", "Mathematics"),
            new Subject("ENG", "English"),
            new Subject("VIE", "Vietnamese"),
            new Subject("CHE", "Chemistry"),
            new Subject("PHY", "Physics"),
            new Subject("BIO", "Biology"),
            new Subject("HIS", "History"),
            new Subject("GEO", "Geography"),
            new Subject("IT", "Information Technology"),
            new Subject("PE", "Physical Education")
        };
        
        for (Subject subject : subjects) {
            Subject saved = subjectRepository.save(subject);
            logger.info("Created subject: id={}, code={}, name={}", saved.getId(), saved.getCode(), saved.getName());
        }
        
        long finalCount = subjectRepository.count();
        logger.info("Subject seeding completed. Total subjects: {}", finalCount);
    }
}
