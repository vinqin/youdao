package edu.stu.dao;

import edu.stu.bean.BasicExplains;
import edu.stu.bean.BasicTranslation;
import edu.stu.bean.WebTranslation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasicTranslationMapper {

    //BasicTranslation getBasicTranslationById(int id);

    BasicTranslation getBasicTranslationByQuery(String query);

    //注意：mybatis如果没有通过bId在数据库中找到记录，会返回一个已经实例化后的不带元素的List，而不会返回null
    List<BasicExplains> getBasicExplainsByBid(@Param("bId") int bId);

    List<WebTranslation> getWebTranslationByBid(@Param("bId") int bId);

    Boolean updateBasicTranslation(BasicTranslation basic);

    Boolean updateUrlOfBasicTranslationById(BasicTranslation basic);

    Boolean addBasicTranslation(BasicTranslation basic);

    Integer addBasicExplains(BasicTranslation basic);

    Integer addWebTranslation(BasicTranslation basic);

    //获取tbl_basic_translation表的总记录数
    Integer getBasicTranslationCounts();

}
