package com.example.demo.util;

import com.example.demo.entity.Policy;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.json.JSONArray;
import org.xml.sax.InputSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;

public class XMLExtractor {
    private String inputFile;
    private static XMLExtractor instance;
    private static Set<String> validAgencies = new HashSet<>(); // 插入数据到表格
    static{
        validAgencies.add("bureau of industry and security");
        validAgencies.add("department of the treasury");
        validAgencies.add("department of state");
        validAgencies.add("department of justice");
    }

    private XMLExtractor(){
        if (instance != null) {
            throw new IllegalStateException("Already initialized.");
        }
    }

    public static synchronized XMLExtractor getInstance(String inputFilePath) {
        if (instance == null) {
            instance = new XMLExtractor();
        }
        instance.inputFile = inputFilePath;
        return instance;
    }

    private static String[] parseDate(String dateStr) throws Exception {// 提取日期的函数
        String[] dateParts = dateStr.split(", ");
        String dayOfWeek = dateParts[0];
        String dateFormatted = dateParts[1] + ", " + dateParts[2];

        // 将月份名称转换为数字格式以适应DATE格式
        Map<String, String> monthMap = new HashMap<>();
        monthMap.put("January", "01");
        monthMap.put("February", "02");
        monthMap.put("March", "03");
        monthMap.put("April", "04");
        monthMap.put("May", "05");
        monthMap.put("June", "06");
        monthMap.put("July", "07");
        monthMap.put("August", "08");
        monthMap.put("September", "09");
        monthMap.put("October", "10");
        monthMap.put("November", "11");
        monthMap.put("December", "12");

        for (Map.Entry<String, String> entry : monthMap.entrySet()) {
            dateFormatted = dateFormatted.replace(entry.getKey(), entry.getValue());
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = inputFormat.parse(dateFormatted);
        String dateSQL = outputFormat.format(date);

        return new String[]{dayOfWeek, dateSQL};
    }

    private static String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    private static String nodeToString(Element node) {
        try {
            javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(node);
            java.io.StringWriter writer = new java.io.StringWriter();
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
            transformer.transform(source, result);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Policy> generatePolicies(String type, NodeList nodeList, String date, String dayOfWeek, Set<String> validAgencies){
        List<Policy> policyList = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element parentElement = (Element) nodeList.item(i);// 获取全文信息
            NodeList items = parentElement.getElementsByTagName(type.toUpperCase());
            //System.out.println(items.getLength());

            for (int j = 0; j < items.getLength(); j++) {
                //遍历<RULES>中的每一个<RULE>
                Element element = (Element) items.item(j);
                String frdoc = getElementTextContent(element, "FRDOC");
                String bilcod = getElementTextContent(element, "BILCOD");// 获取信息
                Element preamb = (Element) element.getElementsByTagName("PREAMB").item(0);

                String agency = getElementTextContent(preamb, "AGENCY");
                String subagency = getElementTextContent(preamb, "SUBAGY");// 获取机构
                
                if (agency != null) agency = agency.toLowerCase();
                if (subagency != null) subagency = subagency.toLowerCase();

                if (validAgencies.contains(agency) || validAgencies.contains(subagency)) {
                    // 仅选择四个部门的信息，若希望先查看完整情况，请注释此行
                    String subjectText = getElementTextContent(preamb, "SUBJECT");
                    String[] subjects = subjectText != null ? subjectText.split("[,;]") : new String[0];
                    JSONArray subjectJson = new JSONArray(subjects);

                    String cfr = getElementTextContent(preamb, "CFR");
                    String depdoc = getElementTextContent(preamb, "DEPDOC");
                    
                    if (depdoc != null && depdoc.length() > 255) {
                        depdoc = depdoc.substring(0, 255); 
                    }// 该函数用于截断 depdoc 字段的值，防止其过长报错，可根据实际情况调整

                    Element sumElement = (Element) element.getElementsByTagName("SUM").item(0);
                    String summary = "";
                    if (sumElement != null) {
                        NodeList summaryNodes = sumElement.getElementsByTagName("P");
                        StringBuilder summaryBuilder = new StringBuilder();
                        for (int k = 0; k < summaryNodes.getLength(); k++) {
                            summaryBuilder.append(summaryNodes.item(k).getTextContent()).append(" ");
                        }
                        summary = summaryBuilder.toString().trim();
                    }// 获取摘要

                    agency = (agency != null) ? agency : "0";
                    subagency = (subagency != null) ? subagency : "0";
                    JSONArray subjectStr = (subjectJson.length() > 0) ? subjectJson : new JSONArray();
                    cfr = (cfr != null) ? cfr : "0";
                    depdoc = (depdoc != null) ? depdoc : "0";
                    frdoc = (frdoc != null) ? frdoc : "0";
                    bilcod = (bilcod != null) ? bilcod : "0";
                    summary = (summary.length() > 0) ? summary : "0";
                    String contentStr = nodeToString(element);
                    LocalDate localDate = LocalDate.parse(date);
                    java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

                    policyList.add(new Policy(null, type, sqlDate, dayOfWeek, agency, subagency, 
                               subjectStr, "", cfr, depdoc, frdoc, bilcod, summary, "", 
                               contentStr));
                }
            }
        }

        return policyList;
    }

    public List<Policy> extractPolicy(){
        try {
            // 预处理xml文件，将所有非UTF-8字符替换为空格
            // 1. 将文件内容读入字符串
            String content = new String(Files.readAllBytes(Paths.get(inputFile)), StandardCharsets.UTF_8);

            // 2. 使用正则表达式过滤非法字符为单个空格
            //    [^\p{Print}] 匹配所有不可打印字符，你可以根据需要调整此正则
            content = content.replaceAll("[^\\p{Print}]", " ");
            
            // 将连续的空格合并为一个空格
            content = content.replaceAll(" +", " ");
            
            // 3. 将清洗后的字符串转为输入流
            ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));



            // 解析XML文件
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(bais));
            doc.getDocumentElement().normalize();

            // 进入<FEDREG>标签（大标签）
            Element fedreg = (Element) doc.getElementsByTagName("FEDREG").item(0);

            // 提取日期和星期几
            String dateStr = fedreg.getElementsByTagName("DATE").item(0).getTextContent();
            String[] dateInfo = parseDate(dateStr);
            String dayOfWeek = dateInfo[0];
            String dateSQL = dateInfo[1];

            // 提取rules, prorules, Notices部分
            NodeList rulesList = fedreg.getElementsByTagName("RULES");
            NodeList prorulesList = fedreg.getElementsByTagName("PRORULES");
            NodeList noticesList = fedreg.getElementsByTagName("NOTICES");

            List<Policy> ruleList = generatePolicies("rule", rulesList, dateSQL, dayOfWeek, validAgencies);
            List<Policy> proruleList = generatePolicies("prorule", prorulesList, dateSQL, dayOfWeek, validAgencies);
            List<Policy> noticeList = generatePolicies("notice", noticesList, dateSQL, dayOfWeek, validAgencies);

            System.out.println("XML提取成功");
            return new ArrayList<Policy>(){{
                addAll(ruleList);
                addAll(proruleList);
                addAll(noticeList);
            }};
        }catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Policy>();
        }
    }
}