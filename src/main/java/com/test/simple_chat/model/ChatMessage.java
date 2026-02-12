package com.test.simple_chat.model;

import java.io.Serializable;

public record ChatMessage(
        String participant,
        String message,
        String timestamp
) implements Serializable {}