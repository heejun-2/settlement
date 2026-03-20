package com.batch.settlement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@ToString
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName; // 주문자명
    private String storeName; // 가맹점명
    private Integer amount; // 주문금액
    private LocalDate orderDate; // 주문일자 (yyyy-mm-dd)

    public Orders(Long id, String customerName, String storeName, Integer amount, LocalDate orderDate) {
        this.id = id;
        this.customerName = customerName;
        this.storeName = storeName;
        this.amount = amount;
        this.orderDate = orderDate;
    }
}
