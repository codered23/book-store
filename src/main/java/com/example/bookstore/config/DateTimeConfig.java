package com.example.bookstore.config;

import java.time.format.DateTimeFormatter;

public class DateTimeConfig {
    public static final DateTimeFormatter format = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");
}
