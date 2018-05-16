package com.ruidi.mybatis.dao;


public interface ServerConfigMapper {
    String selectPassword() throws  Exception;
    int selectLoginEnable() throws Exception;
}
