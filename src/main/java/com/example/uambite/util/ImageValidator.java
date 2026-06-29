package com.example.uambite.util;

import com.example.uambite.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public final class ImageValidator {

    public static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private ImageValidator() {}

    public static void validar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El archivo está vacío",
                    HttpStatus.BAD_REQUEST, "IMAGE_EMPTY");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new BusinessException("La imagen supera el máximo de 5MB",
                    HttpStatus.PAYLOAD_TOO_LARGE, "IMAGE_TOO_LARGE");
        }
        String tipo = file.getContentType();
        if (tipo == null || !ALLOWED_TYPES.contains(tipo.toLowerCase())) {
            throw new BusinessException("Tipo de imagen no permitido (solo jpg, png, webp)",
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE, "IMAGE_UNSUPPORTED_TYPE");
        }
    }
}
