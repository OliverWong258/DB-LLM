package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.CaseInfo;

@Mapper
/**
 * 与案件的mysql数据库交互
 */
public interface CaseInfoMapper {

    /**
     * 添加案件信息
     */
    @Insert("INSERT INTO Cases (type, chineseSubject, chineseSummary, content) " +
            "VALUES (#{type}, #{chineseSubject}, #{chineseSummary}, #{content})")
    void insertCaseInfo(
            @Param("type") String type,
            @Param("chineseSubject") String chineseSubject,
            @Param("chineseSummary") String chineseSummary,
            @Param("content") String content
    );

    /**
     * 根据案件编号查询案件，若编号为-1，则代表查询所有案件
     * 具体的查询语句在CaseInfoMapper.xml中
     * @param id 案件ID
     * @return 案件列表
     */
    List<CaseInfo> searchCases(
            @Param("caseID") int id);
}
