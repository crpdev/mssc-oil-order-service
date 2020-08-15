package com.crpdev.msscoilorder.web.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class OilOrderPagedList extends PageImpl<OilOrderDto> {

    public OilOrderPagedList(List<OilOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public OilOrderPagedList(List<OilOrderDto> content) {
        super(content);
    }
}
