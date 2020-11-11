package com.cus.gf_work.wrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cus.gf_work.dto.PageDTO;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页包装类
 * @author zhaojiejun
 * @date 2020/9/1 9:34 下午
 **/
public class PageWrapper<T> {
    private final Page<T> page;

    public PageWrapper(Page<T> page) {
        this.page = page;
    }

    /**
     * 将MP分页转换成其余数据结构的分页
     * @param mapper function接口
     * @param <R> 另外数据格式
     * @return 转换后的分页列表
     */
    public <R> PageDTO<R> convertPage(Function<? super T, ? extends R> mapper) {
        long size = page.getSize();
        long current = page.getCurrent();
        if (null==page.getRecords()) {
            return PageWrapper.buildEmptyPage(size, current);
        }
        //转换
        long total = page.getTotal();
        List<R> collect = page.getRecords().stream().map(mapper).collect(Collectors.toList());
        PageDTO.PageDTOBuilder<R> builder = PageDTO.builder();
        builder.pageSize(size)
                .pageNo(current)
                .total(total);
        return builder.data(collect).build();
    }

    /**
     * 构造空的分页对象
     * @param pageSize 每页条数
     * @param pageNo 当前页码
     * @param <R> 范型
     * @return 返回空的分页对象
     */
    private static <R> PageDTO<R> buildEmptyPage(Long pageSize, Long pageNo) {
        return PageDTO.<R>builder().data(new ArrayList<>()).pageSize(pageSize).pageNo(pageNo)
                .total(NumberUtils.LONG_ZERO).build();
    }
}
