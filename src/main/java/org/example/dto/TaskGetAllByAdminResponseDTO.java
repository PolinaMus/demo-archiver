package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskGetAllByAdminResponseDTO {
    private String id;
    private long userId;
    private String userLogin;
    private boolean status;
}
