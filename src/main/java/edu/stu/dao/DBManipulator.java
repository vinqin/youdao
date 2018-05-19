package edu.stu.dao;

import edu.stu.bean.BasicExplains;
import edu.stu.bean.BasicTranslation;
import edu.stu.bean.WebTranslation;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DBManipulator {

    private static final String resources = "mybatis-config.xml";
    private SqlSession sqlSession;
    private BasicTranslationMapper basicMapper;

    private DBManipulator() throws IOException {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resources));
        sqlSession = factory.openSession(true);
        basicMapper = sqlSession.getMapper(BasicTranslationMapper.class);
    }

    public static DBManipulator getDBManipulator() {
        DBManipulator dbManipulator;
        try {
            dbManipulator = new DBManipulator();
        } catch (IOException e) {
            return null;
        }
        return dbManipulator;
    }

    public BasicTranslation getBasicTranslation(String query) {
        //此方法只在程序最开始处执行一次
        if (query == null || query.equals("")) {
            return null;
        }
        BasicTranslation basicTranslation = basicMapper.getBasicTranslationByQuery(query);
        if (basicTranslation != null) {
            List<BasicExplains> explains = basicMapper.getBasicExplainsByBid(basicTranslation.getId());
            basicTranslation.setExplainsList(explains);
            List<WebTranslation> webs = basicMapper.getWebTranslationByBid(basicTranslation.getId());
            basicTranslation.setWebTranslationList(webs);
        }
        return basicTranslation;
    }

    public void updateBasicTranslation(BasicTranslation basic) {
        basic.setDate(new Date());//最后一次查询时间设置为当前时间
        basic.setCount(basic.getCount() + 1);//增加一次查询次数
        basicMapper.updateBasicTranslation(basic);
    }

    public boolean addBasicTranslation(BasicTranslation basic) {
        boolean flag = basicMapper.addBasicTranslation(basic);
        int id = basic.getId();
        List<BasicExplains> basicExps = basic.getExplainsList();
        for (BasicExplains exp : basicExps) {
            exp.setbId(id);
        }
        List<WebTranslation> webs = basic.getWebTranslationList();
        if (webs != null) {
            for (WebTranslation web : webs) {
                web.setbId(id);
            }
        }
        int n1 = basicMapper.addBasicExplains(basic);
        int n2 = basicMapper.addWebTranslation(basic);
        return flag || ((n1 + n2) > 0);
    }

    public Boolean updateUrlOfBasicTranslationById(BasicTranslation basic) {
        boolean flag = basicMapper.updateUrlOfBasicTranslationById(basic);
        return flag;
    }

    public Integer getBasicTranslationCountsPlusOne() {
        return basicMapper.getBasicTranslationCounts() + 1;
    }


    public void releaseSession() {
        if (sqlSession != null) {
            sqlSession.close();
        }
    }


}
