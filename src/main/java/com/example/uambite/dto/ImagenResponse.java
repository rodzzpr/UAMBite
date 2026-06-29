package com.example.uambite.dto;

public record ImagenResponse(String imagenUrl, boolean tieneImagen) {

    public static ImagenResponse of(String url) {
        return new ImagenResponse(url, true);
    }
}
