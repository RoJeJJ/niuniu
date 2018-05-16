package com.ruidi.mybatis.service;

import com.ruidi.mybatis.MyBatisHelper;
import com.ruidi.mybatis.dao.ServerConfigMapper;
import org.apache.ibatis.session.SqlSession;

public class ServerConfigService {
    public static String password(){
        try (SqlSession sqlSession = MyBatisHelper.instance().openSession()) {
            ServerConfigMapper mapper = sqlSession.getMapper(ServerConfigMapper.class);
            return mapper.selectPassword();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static int loginEnable(){
        try (SqlSession sqlSession = MyBatisHelper.instance().openSession()){
            ServerConfigMapper mapper = sqlSession.getMapper(ServerConfigMapper.class);
            return mapper.selectLoginEnable();
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
