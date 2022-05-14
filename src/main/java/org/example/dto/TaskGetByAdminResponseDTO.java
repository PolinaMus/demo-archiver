package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskGetByAdminResponseDTO {
    private String id;
    private String userLogin;
    private boolean status;
}
