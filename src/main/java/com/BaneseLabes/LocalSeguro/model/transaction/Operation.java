package com.BaneseLabes.LocalSeguro.model.transaction;

import com.BaneseLabes.LocalSeguro.model.Wifi.Wifi;
import com.BaneseLabes.LocalSeguro.model.location.Location;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "Transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Operation {
    private Double amount;
    private String senderId;
    @Indexed(unique = true)
    @Id
    private String transactionId;
    private String receiverId;
    private Location location;
    private LocalDateTime timestamp;
    private OperationType operationType;
    private Wifi wifi;

}


