package com.xkom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xkom.entity.PriceEntity;
import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value(staticConstructor = "create")
@ToString
public class Price {

    private final BigDecimal price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private final LocalDateTime timeStamp;

    public static Price fromEntity(PriceEntity entity) {
        return new Price(entity.getPrice(), entity.getTimestamp());
    }
}
