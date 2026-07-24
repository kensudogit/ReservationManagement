package com.receivables.controller;

import com.receivables.dto.ReceivableRequest;
import com.receivables.dto.ReceivableResponse;
import com.receivables.service.ReceivableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receivables")
@RequiredArgsConstructor
public class ReceivableController {

    private final ReceivableService receivableService;

    @GetMapping
    public List<ReceivableResponse> list() {
        return receivableService.findAll();
    }

    @GetMapping("/{id}")
    public ReceivableResponse get(@PathVariable Long id) {
        return receivableService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReceivableResponse create(@Valid @RequestBody ReceivableRequest request) {
        return receivableService.create(request);
    }
}
