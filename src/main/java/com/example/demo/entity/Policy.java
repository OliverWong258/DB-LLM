package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
//import org.json.JSONArray;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 政策实体，封装了一个政策
 */
public class Policy {
    private Integer id;               // 政策ID
    private String type;              // 类型
    private Date date;                // 日期
    private String dayOfTheWeek;      // 星期几
    private String agency;            // 机构
    private String subagency;         // 子机构
    private String subjectJson;       // 英文主题
    private String chineseSubject;    // 中文主题
    private String cfr;               // CFR
    private String depdoc;            // DEP DOC
    private String frdoc;             // FR DOC
    private String bilcod;            // BIL COD
    private String summary;           // 英文摘要
    private String chineseSummary;    // 中文摘要 
    private String content;           // 完整的XML内容

    @Override
    public String toString() {
        return "Policy{" +
                //"type='" + type + '\'' +
                ", date='" + date + '\'' +
                //", dayOfWeek='" + dayOfTheWeek + '\'' +
                ", agency='" + agency + '\'' +
                //", subagency='" + subagency + '\'' +
                //", subject='" + subjectJson + '\'' +
                //", cfr='" + cfr + '\'' +
                //", depdoc='" + depdoc + '\'' +
                //", frdoc='" + frdoc + '\'' +
                //", bilcod='" + bilcod + '\'' +
                ", summary='" + summary + '\'' +
                //", content='" + content + '\'' +
                '}';
    }
}