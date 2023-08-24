package by.yayauheny.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bank {

    private Integer id;
    private String name;
    private String address;
    private String department;
}
