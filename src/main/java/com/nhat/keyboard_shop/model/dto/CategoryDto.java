package com.nhat.keyboard_shop.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long categoryId;

    @NotEmpty
    @Length(min = 3)
    private String name;
    private boolean isEdit = false;
}
