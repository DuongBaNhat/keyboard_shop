package com.nhat.keyboard_shop.model;


import com.nhat.keyboard_shop.domain.entity.Category;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportInventory implements Serializable {
//    private Serializable group;
    private Category group;
    private Double sum;
    private Long count;

}
