package com.rayen.walletManagement.entity;

import com.rayen.walletManagement.model.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Entity
@Table(name = "devise_wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviseWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "deviseWallet")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Wallet wallet;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "devise_wallet_balances", joinColumns = @JoinColumn(name = "devise_wallet_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "amount", precision = 19, scale = 8)
    @Builder.Default
    private Map<CurrencyCode, BigDecimal> balances = new EnumMap<>(CurrencyCode.class);

    public void initializeBalances() {
        for (CurrencyCode currency : CurrencyCode.values()) {
            balances.putIfAbsent(currency, BigDecimal.ZERO);
        }
    }

    public BigDecimal getBalance(CurrencyCode currency) {
        return balances.getOrDefault(currency, BigDecimal.ZERO);
    }

    public void deposit(CurrencyCode currency, BigDecimal amount) {
        balances.put(currency, getBalance(currency).add(amount));
    }

    public void withdraw(CurrencyCode currency, BigDecimal amount) {
        BigDecimal current = getBalance(currency);
        if (current.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient " + currency + " balance");
        }
        balances.put(currency, current.subtract(amount));
    }
}
