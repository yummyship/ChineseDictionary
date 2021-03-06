package com.zi.dian.net;

import android.util.Log;

import com.zi.dian.dao.model.ChineseCharacter;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by wangliang on 6/14/16.
 */
public class TaskZi extends LoadData {
    String TAG = "TaskZi";
    public TaskZi(String linkUrl) {
        url = linkUrl;
    }

    @Override
    public void run() {
        List<ChineseCharacter> chineseCharacterList = parserRadicals();
        getAppliction().getDaoManager().getTableZi().batchInsertData(chineseCharacterList);
        Log.d(TAG, "run: ");
    }

    @Override
    public void completeLoad(String str) {

    }

    private List<ChineseCharacter> parserRadicals() {
        List<ChineseCharacter> chineseCharacterList = parserZi();
        return chineseCharacterList;
    }


    private List parserZi() {
        List<ChineseCharacter> chineseCharacterList = new ArrayList<>();
        try {
            Parser htmlParser = new Parser(url);
            htmlParser.setEncoding("GBK");
            // 获取指定的 dd 节点，即 <dd> 标签
            NodeList divOfTab2 = htmlParser.extractAllNodesThatMatch(
                    new AndFilter(new TagNameFilter[]{(new TagNameFilter("dd"))}));
            if (divOfTab2 != null && divOfTab2.size() > 0) {
                for (int i = 0; i < divOfTab2.size(); ++i) {
                    // 获取指定 dd 标签
                    Node ddNode = divOfTab2.elementAt(i);
                    NodeList ddChildNodeList = ddNode.getChildren();

                    if (ddChildNodeList != null) {
                        //获取指定 dd 标签的子节点中的 <a> 节点
                        Node aNode = ddChildNodeList.elementAt(0);
                        // 在 <a> 节点的子节点中获取 Link 节点

                        String title = ((LinkTag) aNode).getAttribute("title");
                        String[] titles = title.split("&#10");

                        String[] spellings = titles[0].substring(3, titles[0].length()).split(",");
                        List<String> spellingList = Arrays.asList(spellings);
                        Set set = new HashSet();
                        set.add(spellingList);//去重
                        for(Iterator it = set.iterator(); it.hasNext(); ){
                            String spelling = it.next().toString();
                            ChineseCharacter chineseCharacter = new ChineseCharacter();
                            chineseCharacter.zi = ((LinkTag) aNode).getLinkText();
                            chineseCharacter.linkUrl = ((LinkTag) aNode).getLink();
                            chineseCharacter.radical = titles[1].split("：")[1];
                            chineseCharacter.stroke = Integer.valueOf(titles[2].split("：")[1]);
                            chineseCharacter.spelling = spelling;
                            chineseCharacterList.add(chineseCharacter);
                        }
                    }
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return chineseCharacterList;
    }
}
