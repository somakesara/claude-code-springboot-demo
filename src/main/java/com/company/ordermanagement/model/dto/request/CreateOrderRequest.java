package com.company.ordermanagement.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Jacksonized
public class CreateOrderRequest {

    @NotNull(message = "customerId is required")
    private final UUID customerId;

    @NotEmpty(message = "lineItems must not be empty")
    @Valid
    private final List<LineItemRequest> lineItems;

    @Getter
    @Builder
    @Jacksonized
    public static class LineItemRequest {

        @NotNull(message = "productId is required")
        private final UUID productId;

        @NotBlank(message = "productSku is required")
        private final String productSku;

        @NotBlank(message = "productName is required")
        private final String productName;

        @Min(value = 1, message = "quantity must be at least 1")
        @Max(value = 1000, message = "quantity must not exceed 1000")
        private final int quantity;

        @NotNull(message = "unitPrice is required")
        @DecimalMin(value = "0.01", message = "unitPrice must be positive")
        private final BigDecimal unitPrice;
    }
}
