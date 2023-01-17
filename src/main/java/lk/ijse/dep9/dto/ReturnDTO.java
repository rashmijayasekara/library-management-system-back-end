package lk.ijse.dep9.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnDTO {
    private String memberID;
    private List<ReturnItem> items=new ArrayList<>();
}
