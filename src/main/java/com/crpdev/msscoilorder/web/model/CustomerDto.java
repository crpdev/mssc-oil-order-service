package com.crpdev.msscoilorder.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerDto extends BaseItem {

    @Builder
    public CustomerDto(UUID id, Integer version, OffsetDateTime createDate, OffsetDateTime lastModifiedDate, String name) {
        super(id, version, createDate, lastModifiedDate);
        this.name = name;
    }

    private String name;

}
