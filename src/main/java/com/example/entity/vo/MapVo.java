package com.example.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapVo {
    String name;
    Integer value;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MapVo) {
            MapVo mapVo = (MapVo) obj;
            return name.equals(mapVo.getName());
        }
        return false;
    }

}
