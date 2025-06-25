package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OperationService {
    @Autowired
    private UserService userService;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private SafetyPlaceService safetyPlaceService;

}
