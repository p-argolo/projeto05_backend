package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.dto.LocationDTO;
import com.BaneseLabes.LocalSeguro.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
public class MetadataController {
    @Autowired
    private MetadataService metadataService;

    //Cria token assinado e criptografado para enviar ao banco
    @PostMapping("/metadata")
    public String gerarMetadados(@RequestHeader("Authorization") String authHeader, @RequestBody  LocationDTO locationDTO) throws Exception {
        String tokenUser = authHeader.replace("Bearer ", "");
        return metadataService.createResponse(tokenUser,locationDTO);
    }
}

