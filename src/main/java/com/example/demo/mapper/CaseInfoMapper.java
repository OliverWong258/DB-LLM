package com.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CaseInfoMapper {

    /**
     * 添加数据
     */
    @Insert("INSERT INTO Cases (type, chineseSubject, chineseSummary, content) " +
            "VALUES (#{type}, #{chineseSubject}, #{chineseSummary}, #{content})")
    void insertCaseInfo(
            @Param("type") String type,
            @Param("chineseSubject") String chineseSubject,
            @Param("chineseSummary") String chineseSummary,
            @Param("content") String content
    );
}
