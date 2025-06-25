package com.BaneseLabes.LocalSeguro.dto;

import com.BaneseLabes.LocalSeguro.model.Authorization;

public record MetadataInfoDTO(Boolean InSafetyPlace,
                              Authorization authorization,
                              Boolean canMakePix,
                              Boolean canMakeLoan,
                              Boolean canMakeBankSplit,
                              Boolean canMakeTed
                             ) {
}
