package lk.ijse.dep9.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItem {
    private Integer issueNote;
    private String isbn;
}
