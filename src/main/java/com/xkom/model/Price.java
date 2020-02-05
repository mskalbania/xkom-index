package com.xkom.model;

import com.xkom.entity.PriceEntity;
import lombok.ToString;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value(staticConstructor = "create")
@ToString
public class Price {

    private final BigDecimal price;
    private final LocalDateTime timeStamp;

    public static Price fromEntity(PriceEntity entity) {
        return new Price(entity.getPrice(), entity.getTimestamp());
    }
}
