package com.company.ordermanagement.model.dto.request;

import com.company.ordermanagement.model.entity.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class CreateCustomerRequest {

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private final String email;

    @NotBlank(message = "firstName is required")
    private final String firstName;

    @NotBlank(message = "lastName is required")
    private final String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "phone must be a valid international format")
    private final String phone;

    private final Customer.CustomerTier tier;
}
