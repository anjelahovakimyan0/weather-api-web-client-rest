package am.itspace.photoshootprojectmanagementrest.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponseDto {

    private Object data;

    private int size;

    private int page;

    private long totalElements;

}
