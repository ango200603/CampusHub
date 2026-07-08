package com.campushub.common.api;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic page response body.
 *
 * @param <T> row type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long pageNo;
    private long pageSize;

    /**
     * Creates an empty page.
     *
     * @param pageNo page number
     * @param pageSize page size
     * @param <T> row type
     * @return empty page result
     */
    public static <T> PageResult<T> empty(long pageNo, long pageSize) {
        return PageResult.<T>builder()
                .records(Collections.emptyList())
                .total(0)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .build();
    }

    /**
     * Creates a page result.
     *
     * @param records row list
     * @param total total row count
     * @param pageNo page number
     * @param pageSize page size
     * @param <T> row type
     * @return page result
     */
    public static <T> PageResult<T> of(List<T> records, long total, long pageNo, long pageSize) {
        return PageResult.<T>builder()
                .records(records == null ? Collections.emptyList() : records)
                .total(total)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .build();
    }
}
