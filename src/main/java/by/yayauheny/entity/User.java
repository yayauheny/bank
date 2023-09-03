package by.yayauheny.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class User {

    private Integer id;
    private String fullName;
    private LocalDate birthDate;
    private String address;
    private List<Account> accounts;
}
