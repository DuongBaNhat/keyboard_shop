package com.nhat.keyboard_shop.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private int customerId;
    @NotEmpty
    private String name;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Length(min = 6)
    private String password;
    @NotEmpty
    @Pattern(regexp = "^0\\d{9}$")
    private String phone;
    @NotEmpty
    private String address;
    private boolean gender;
    private String image;
    private Date registerDate;
    private Boolean status;
    private boolean isEdit = false;
}
