package com.rag.platform.service.interaction;

import com.rag.platform.model.InteractionType;

/**
 * Observer pattern via Spring events: when an interaction is recorded,
 * listeners can react (ex: update recommendations) without tight coupling.
 */
public record UserInteractionRecordedEvent(
    Long userId,
    Long paperId,
    InteractionType type
) {}

