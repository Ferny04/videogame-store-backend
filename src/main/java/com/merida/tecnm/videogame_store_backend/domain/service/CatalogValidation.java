package com.merida.tecnm.videogame_store_backend.domain.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

final class CatalogValidation {

    private CatalogValidation() {
    }

    static void required(Object value, String field) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
        }
    }

    static void requiredText(String value, int maxLength, String field) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
        }
        optionalText(value, maxLength, field);
    }

    static void optionalText(String value, int maxLength, String field) {
        if (value != null && (value.codePointCount(0, value.length()) > maxLength || value.indexOf('\0') >= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    field + " must contain at most " + maxLength + " characters and no null character");
        }
    }

    static void clientId(String value) {
        requiredText(value, 20, "clientId");
        if (value.equals(".") || value.equals("..")
                || value.chars().anyMatch(character -> "/\\;%".indexOf(character) >= 0
                || Character.isISOControl(character))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientId must be valid as a URL segment");
        }
    }
}
