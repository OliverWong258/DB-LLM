package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.CaseInfo;

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

    /**
     * 根据可选条件查询案件列表
     * @param id 案件ID
     * @return 案件列表
     */
    List<CaseInfo> searchCases(
            @Param("caseID") int id);
}
