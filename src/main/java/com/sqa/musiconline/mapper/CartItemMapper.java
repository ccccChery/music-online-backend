package com.sqa.musiconline.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqa.musiconline.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
