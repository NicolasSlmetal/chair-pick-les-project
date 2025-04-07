package com.chairpick.ecommerce.utils.pagination;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PageOptions {

    private int page;
    private int size;

    public int getOffset() {
        if (page < 1) {
            return 0;
        }
        if (size < 1) {
            return 0;
        }
        return (page - 1) * size;
    }

}
