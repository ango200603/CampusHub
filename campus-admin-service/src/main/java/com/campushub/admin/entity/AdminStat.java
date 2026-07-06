package com.campushub.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStat {
    private String name;
    private Object value;
}
