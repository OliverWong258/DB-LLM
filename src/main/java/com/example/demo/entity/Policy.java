package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import org.json.JSONArray;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    private Integer id;               // 政策ID
    private String type;              // 类型
    private Date date;                // 日期
    private String dayOfTheWeek;      // 星期几
    private String agency;            // 机构
    private String subagency;         // 子机构
    private JSONArray subjectJson;           // 主题（JSON 格式）
    private String chineseSubject;    // 中文主题（JSON 格式）
    private String cfr;               // CFR
    private String depdoc;            // DEP DOC
    private String frdoc;             // FR DOC
    private String bilcod;            // BIL COD
    private String summary;           // 摘要
    private String chineseSummary;    // 中文摘要 
    private String content;           // 内容

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