package com.example.demo;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import java.util.ArrayList;

import com.example.demo.entity.CaseInfo;
import com.example.demo.mapper.CaseInfoMapper;
import com.example.demo.util.CaseExtractor;

@SpringBootTest
@Sql(scripts = "/schema.sql")  // 加载 schema.sql 脚本
public class CaseMapperTest {
    
    @Autowired
    private CaseInfoMapper caseInfoMapper;

    @Test
    public void testInsertCaseInfo(){
        ArrayList<CaseInfo> caseInfos = CaseExtractor.extractCaseInfo();

        for (CaseInfo caseInfo : caseInfos){
            try{
                caseInfoMapper.insertCaseInfo(caseInfo.getType(), 
                        caseInfo.getChineseSubject(), 
                        caseInfo.getChineseSummary(), 
                        caseInfo.getContent());
                System.out.println("案件插入成功");

            }catch(Exception e){
                e.printStackTrace();
                System.out.println("案件插入失败");
            }
        }
    }
}
