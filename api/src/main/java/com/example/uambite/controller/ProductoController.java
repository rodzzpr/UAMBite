package com.example.uambite.controller;

import com.example.uambite.dto.ImagenData;
import com.example.uambite.dto.ImagenResponse;
import com.example.uambite.dto.request.ProductoRequest;
import com.example.uambite.dto.response.ProductoResponse;
import com.example.uambite.service.ProductoService;
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

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService service;

    @GetMapping("/all")
    @Operation(summary = "Listar todos los productos")
    public ResponseEntity<Page<ProductoResponse>> getAll(Pageable pageable, @RequestParam(required=false) UUID localComidaId, @RequestParam(required=false) String nombre, @RequestParam(required=false) BigDecimal maxPrecio) {
        return ResponseEntity.ok(service.getAll(localComidaId, nombre, maxPrecio, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por ID")
    public ResponseEntity<ProductoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditLocal(#request.localComidaId, principal.id)")
    @Operation(summary = "Crear un nuevo producto. ADMIN o encargado del local destino.")
    public ResponseEntity<ProductoResponse> save(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditProducto(#id, principal.id)")
    @Operation(summary = "Actualizar un producto. ADMIN o encargado del local del producto.")
    public ResponseEntity<ProductoResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditProducto(#id, principal.id)")
    @Operation(summary = "Eliminar un producto. ADMIN o encargado del local del producto.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditProducto(#id, principal.id)")
    @Operation(summary = "Subir o reemplazar la imagen del producto. ADMIN o dueño del local del producto.")
    public ResponseEntity<ImagenResponse> uploadImagen(@PathVariable UUID id,
                                                       @RequestParam("file") MultipartFile file) {
        service.setImagen(id, file);
        return ResponseEntity.ok(ImagenResponse.of("/producto/" + id + "/imagen"));
    }

    @GetMapping("/{id}/imagen")
    @Operation(summary = "Obtener la imagen del producto (público)")
    public ResponseEntity<byte[]> getImagen(@PathVariable UUID id) {
        ImagenData data = service.getImagen(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(data.contentType()))
                .body(data.datos());
    }

    @DeleteMapping("/{id}/imagen")
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.canEditProducto(#id, principal.id)")
    @Operation(summary = "Eliminar la imagen del producto. ADMIN o dueño del local del producto.")
    public ResponseEntity<Void> deleteImagen(@PathVariable UUID id) {
        service.removeImagen(id);
        return ResponseEntity.noContent().build();
    }
}

