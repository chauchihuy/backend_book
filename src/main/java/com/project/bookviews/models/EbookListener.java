package com.project.bookviews.models;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EbookListener {
    private static final Logger logger = LoggerFactory.getLogger(EbookListener.class);

    @PrePersist
    public void prePersist(Ebook ebook) {
        logger.info("prePersist");
    }

    @PostPersist //save = persist
    public void postPersist(Ebook ebook) {
        logger.info("postPersist");
    }

    @PreUpdate
    public void preUpdate(Ebook ebook) {
        logger.info("preUpdate");
    }

    @PostUpdate
    public void postUpdate(Ebook ebook) {
        logger.info("postUpdate");
    }

    @PreRemove
    public void preRemove(Ebook ebook) {
        logger.info("preRemove");
    }

    @PostRemove
    public void postRemove(Ebook ebook) {
        logger.info("postRemove");
    }
}
