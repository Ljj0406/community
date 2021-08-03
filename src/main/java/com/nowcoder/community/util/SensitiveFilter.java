package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换符
    private static final String REPLACEMENT = "***";
    //初始化根节点
    private TireNode root = new TireNode();

    @PostConstruct
    public void init() {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败", e.getMessage());
        }
    }
    //将敏感词添加到前缀树中去
    private void addKeyword(String keyword){
        TireNode tmpNode = root;
        for (int i = 0 ; i<keyword.length();i++){
            char c = keyword.charAt(i);
            TireNode subNode = tmpNode.getSubNode(c);
            if (subNode == null){
                //初始化子节点
                subNode = new TireNode();
                tmpNode.addSubNode(c,subNode);
            }
            //指向子节点，进行下一轮循环
            tmpNode = subNode;
            //设置结束标识
            if (i == keyword.length()-1){
                tmpNode.setKeywordEnd(true);
            }
        }
    }
        //过滤方法
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        TireNode tmpNode = root;
        int begin = 0;
        int position = 0;
        StringBuilder sb = new StringBuilder();
        while (position < text.length()){
            char c = text.charAt(position);
            if (isSymbol(c)){
                if (tmpNode == root){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tmpNode = tmpNode.getSubNode(c);
            if (tmpNode == null){
                sb.append(text.charAt(begin));
                position = ++begin;
                tmpNode = root;
            }else if (tmpNode.isKeywordEnd()){
                sb.append(REPLACEMENT);
                begin = ++position;
                tmpNode = root;
            }else {
                position++;
                continue;
            }
        }
        //将最后一批字符记录结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c< 0x2E80 || c> 0x9FFF);
    }

    //定义前缀树
    private class TireNode {
        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //关键词结束标准
        private boolean isKeywordEnd = false;
        //子节点(key为下级节点字符，V为下级节点)
        private Map<Character, TireNode> subNodes = new HashMap<>();

        public void addSubNode(Character c, TireNode node) {
            subNodes.put(c, node);
        }

        public TireNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
