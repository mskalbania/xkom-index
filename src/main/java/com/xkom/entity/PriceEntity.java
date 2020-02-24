package com.xkom.entity;

import com.xkom.model.Price;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public static PriceEntity fromModel(Price price) {
        PriceEntity entity = new PriceEntity();
        entity.setPrice(price.getPrice());
        entity.setTimestamp(price.getTimeStamp());
        return entity;
    }
}
