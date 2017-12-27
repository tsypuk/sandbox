package ua.in.smartjava.data;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Customer {
    private int id;
    private String name;
    private String email;

}
