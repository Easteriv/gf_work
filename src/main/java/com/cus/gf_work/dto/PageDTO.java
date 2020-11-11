package com.cus.gf_work.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaojiejun
 * @version V1.0
 * @date 2020/7/10 10:30 下午
 * @description 分页实体类
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageDTO<T> implements Serializable {
    private static final long serialVersionUID = -4413709443022377584L;
    /**
     * 分页大小
     */
    private Long pageSize;
    /**
     * 页码，从1开始
     */
    private Long pageNo;
    /**
     * 总记录条数
     */
    private Long total;
    /**
     * 当前页数据
     */
    private List<T> data;

    public <R> PageDTO<R> map(Function<? super T, ? extends R> mapper) {
        return PageDTO.<R>builder()
                .pageSize(pageSize)
                .pageNo(pageNo)
                .total(total)
                .data(Optional.ofNullable(data).orElse(new ArrayList<>())
                        .stream().map(mapper)
                        .collect(Collectors.toList())).build();
    }

}
