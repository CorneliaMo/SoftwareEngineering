package com.szu.afternoon5.softwareengineeringbackend.listener;

import com.szu.afternoon5.softwareengineeringbackend.event.*;
import com.szu.afternoon5.softwareengineeringbackend.service.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.*;

@Slf4j
@Component
public class MetricsEventConsumer {

    private final MetricsService metricsService;

    public MetricsEventConsumer(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentCreated(CommentCreatedEvent e) {
        metricsService.onCommentDelta(e.commentId(), e.postId(), e.userId(), +1);
    }

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentDeleted(CommentDeletedEvent e) {
        metricsService.onCommentDelta(e.commentId(), e.postId(), e.userId(), -1);
    }

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPostCreated(PostCreatedEvent e) {
        metricsService.onPostDelta(e.postId(), e.userId(), +1);
    }

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPostDeleted(PostDeletedEvent e) {
        metricsService.onPostDelta(e.postId(), e.userId(), -1);
    }

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRatingCreated(RatingCreatedEvent e) {
        metricsService.onRatingDelta(e.ratingId(), e.postId(), e.userId(), +1);
    }

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRatingDeleted(RatingDeletedEvent e) {
        metricsService.onRatingDelta(e.ratingId(), e.postId(), e.userId(), -1);
    }
}
