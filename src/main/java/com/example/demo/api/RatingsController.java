package com.example.demo.api;

import com.example.demo.model.Rating;
import com.example.demo.repository.RatingsRepository;
import com.example.demo.repository.TalksRepository;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/ratings")
public class RatingsController {
    private static final Logger LOG = getLogger(RatingsController.class);
    private final KafkaTemplate<String, Rating> kafkaTemplate;

    private final RatingsRepository ratingsRepository;

    private final TalksRepository talksRepository;

    public RatingsController(KafkaTemplate<String, Rating> kafkaTemplate, RatingsRepository ratingsRepository, TalksRepository talksRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.ratingsRepository = ratingsRepository;
        this.talksRepository = talksRepository;
    }

    @PostMapping
    public ResponseEntity<Object> recordRating(@RequestBody Rating rating) throws Exception {
        if (!talksRepository.exists(rating.getTalkId())) {
            return ResponseEntity.notFound().build();
        }
        LOG.info("Recording rating {}", rating);
        kafkaTemplate.send("ratings", rating).get(5, SECONDS);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public Map<Integer, Integer> getRatings(@RequestParam String talkId) {
        LOG.info("Retrieving ratings for {}", talkId);
        return ratingsRepository.findAll(talkId);
    }
}
