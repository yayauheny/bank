package by.yayauheny.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {

    private Integer id;
    private String fullName;
    private LocalDate birthDate;
    private String address;
    private Integer accountId;
}
