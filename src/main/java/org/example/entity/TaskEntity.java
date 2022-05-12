package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskEntity {
    private String id;
    private long userId;
    private String userLogin;
    private boolean status;
}
