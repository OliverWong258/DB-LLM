package com.example.demo.mapper;

import com.example.demo.entity.Policy;

import java.util.List;
import java.sql.Date;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Options;


@Mapper
public interface PolicyMapper {

    /**
     * 添加数据
     */
    @Insert("INSERT INTO Policies (type, date, day_of_the_week, agency, subagency, subject, chineseSubject, cfr, depdoc, frdoc, bilcod, summary, chinese_summary, content) " +
            "VALUES (#{type}, #{date}, #{dayOfTheWeek}, #{agency}, #{subagency}, #{subjectJson}, #{chineseSubject}, #{cfr}, #{depdoc}, #{frdoc}, #{bilcod}, #{summary}, #{chineseSummary}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertDocument(Policy policy);

    /**
     * 根据可选条件查询政策列表
     * @param keywords 关键词
     * @param department 部门
     * @param publishDate 发布日期
     * @return 政策列表
     */
    List<Policy> searchPolicies(
            @Param("keywords") String keywords,
            @Param("department") String department,
            @Param("publishDate") Date publishDate);
}
