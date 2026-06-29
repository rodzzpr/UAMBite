package com.example.uambite.controller;

import com.example.uambite.dto.ImagenData;
import com.example.uambite.dto.ImagenResponse;
import com.example.uambite.dto.request.IngredienteExtraRequest;
import com.example.uambite.dto.response.IngredienteExtraResponse;
import com.example.uambite.service.IngredienteExtraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/ingredienteextra")
@RequiredArgsConstructor
@Tag(name = "Ingredientes Extra", description = "Catálogo de ingredientes extra")
public class IngredienteExtraController {

    private final IngredienteExtraService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los ingredientes extra")
    public ResponseEntity<Page<IngredienteExtraResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un ingrediente extra por ID")
    public ResponseEntity<IngredienteExtraResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Crear un nuevo ingrediente extra. ADMIN o LOCAL.")
    public ResponseEntity<IngredienteExtraResponse> save(@Valid @RequestBody IngredienteExtraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Actualizar un ingrediente extra. ADMIN o LOCAL.")
    public ResponseEntity<IngredienteExtraResponse> update(@PathVariable UUID id,
                                                          @Valid @RequestBody IngredienteExtraRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Eliminar un ingrediente extra. ADMIN o LOCAL.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Subir o reemplazar la imagen del ingrediente extra. ADMIN o LOCAL.")
    public ResponseEntity<ImagenResponse> uploadImagen(@PathVariable UUID id,
                                                       @RequestParam("file") MultipartFile file) {
        service.setImagen(id, file);
        return ResponseEntity.ok(ImagenResponse.of("/ingredienteextra/" + id + "/imagen"));
    }

    @GetMapping("/{id}/imagen")
    @Operation(summary = "Obtener la imagen del ingrediente extra (público)")
    public ResponseEntity<byte[]> getImagen(@PathVariable UUID id) {
        ImagenData data = service.getImagen(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(data.contentType()))
                .body(data.datos());
    }

    @DeleteMapping("/{id}/imagen")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOCAL')")
    @Operation(summary = "Eliminar la imagen del ingrediente extra. ADMIN o LOCAL.")
    public ResponseEntity<Void> deleteImagen(@PathVariable UUID id) {
        service.removeImagen(id);
        return ResponseEntity.noContent().build();
    }
}

