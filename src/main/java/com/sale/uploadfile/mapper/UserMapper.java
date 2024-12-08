package com.sale.uploadfile.mapper;

import com.sale.uploadfile.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select id, username, password from user_info where id = #{id}")
    public User getUserInfo(int id);
}
