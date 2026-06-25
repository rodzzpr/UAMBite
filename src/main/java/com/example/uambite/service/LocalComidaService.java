package com.example.uambite.service;

import com.example.uambite.model.LocalComida;
import com.example.uambite.repository.LocalComidaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalComidaService {

    private final LocalComidaRepository repository;

    public LocalComidaService(LocalComidaRepository repository) {
        this.repository = repository;
    }

    public List<LocalComida> getAll() {
        return repository.findAll();
    }

    public LocalComida save(LocalComida localComida) {
        return repository.save(localComida);
    }

}