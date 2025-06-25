package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.config.JwtUtil;
import com.BaneseLabes.LocalSeguro.dto.LocationDTO;
import com.BaneseLabes.LocalSeguro.dto.MetadataInfoDTO;
import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class MetadataService {


    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwe.secret}")
    private String jweKey;

    @Value("${jws.secret}")
    private String jwsKey;

    @Autowired
    private UserService userService;
    @Autowired
    private SafetyPlaceService safetyPlaceService;

    // Gera um token JWE criptografado
    public String createResponse(String token, LocationDTO locationDTO) throws Exception {
        // Extrai informações do token JWT recebido
        Claims claims = jwtUtil.extractClaims(token);
        String clientId = claims.getSubject();

        // Obtém o CNPJ do token e valida
        Object cnpjObj = claims.get("CNPJ");
        if (!(cnpjObj instanceof String cnpj)) {
            throw new RuntimeException("Claim 'CNPJ' inválida ou ausente: " + cnpjObj);
        }

        // Busca o usuário vinculado ao clientId e CNPJ
        Optional<User> userOptional = userService.findByClientId(cnpj, clientId);
        User user = userOptional.get();

        // Garante que o usuário tenha locais seguros cadastrados
        if (user.getSafetyPlaces().isEmpty()) {
            throw new RuntimeException("Usuário não possui locais seguros cadastrados");
        }

        // Verifica as permissões de acordo com a localização
        Authorization safetyPlaceMatch = safetyPlaceService.safetyPlaceMatch(locationDTO, user);
        Boolean isInSafetyPlace = safetyPlaceService.isInSafetyPlace(locationDTO, user);

        // Cria objeto com as informações de autorização
        MetadataInfoDTO metadata = new MetadataInfoDTO(
                isInSafetyPlace,
                safetyPlaceMatch,
                safetyPlaceMatch.canMakePix(),
                safetyPlaceMatch.canMakeLoan(),
                safetyPlaceMatch.canMakeBankSplit(),
                safetyPlaceMatch.canMakeTed()
        );

        // Monta o conjunto de "claims"
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issueTime(new Date())
                .claim("IN_SAFETY_PLACE", metadata.InSafetyPlace())
                .claim("CAN_MAKE_PIX", metadata.canMakePix())
                .claim("CAN_MAKE_LOAN", metadata.canMakeLoan())
                .claim("CAN_MAKE_BANK_SPLIT", metadata.canMakeBankSplit())
                .claim("CAN_MAKE_TED", metadata.canMakeTed())
                .claim("CAN_REGISTER_VIRTUAL_CARD", metadata.authorization().canRegisterVirtualCard())
                .claim("CAN_CHANGE_PASSWORD", metadata.authorization().canChangePassword())
                .claim("PIX_HAS_LIMIT", metadata.authorization().pixHasLimit())
                .claim("LOAN_HAS_LIMIT", metadata.authorization().loanHasLimit())
                .claim("BANK_SPLIT_HAS_LIMIT", metadata.authorization().bankSplitHasLimit())
                .claim("TED_HAS_LIMIT", metadata.authorization().tedHasLimit())
                .claim("PIX_LIMIT", metadata.authorization().getPix())
                .claim("LOAN_LIMIT", metadata.authorization().getLoan())
                .claim("BANK_SPLIT_LIMIT", metadata.authorization().getBanksplit())
                .claim("TED_LIMIT", metadata.authorization().getTed())
                .build();

        // Assina o JWT com o jws.secret
        JWSSigner signer = new MACSigner(jwsKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );
        signedJWT.sign(signer);

        // Cria um objeto JWE (criptografado) contendo o JWT assinado
        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                        .contentType("JWT")
                        .build(),
                new Payload(signedJWT)
        );

        // Valida o tamanho da chave e criptografa o JWE
        byte[] chaveSecreta = jweKey.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (chaveSecreta.length != 32) {
            throw new IllegalArgumentException("Chave JWE deve ter 32 bytes (256 bits). Tamanho atual: " + chaveSecreta.length);
        }

        jweObject.encrypt(new DirectEncrypter(chaveSecreta));

        String tokenJwe = jweObject.serialize();

        // Verifica se tem exatamente 5 partes
        String[] parts = tokenJwe.split("\\.");
        if (parts.length != 5) {
            throw new RuntimeException("Token JWE inválido: esperado 5 partes, encontrado " + parts.length);
        }

        return tokenJwe;
    }
}
