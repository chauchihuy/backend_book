package com.project.bookviews.models;

public enum KindOfBook {
    FREE("Miễn Phí"),
    FOR_SALE("Bán"),
    MEMBERSHIP("Hội Viên");

    private final String description;

    KindOfBook(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
