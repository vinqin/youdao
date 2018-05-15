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
    private static SqlSession sqlSession = null;


    public static BasicTranslation getBasicTranslation(String query) {
        //此方法只在程序最开始处执行一次
        BasicTranslation basicTranslation = null;

        try {
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resources));
            sqlSession = factory.openSession(true);

            if (query == null || query.equals("")) {
                return null;
            }

            BasicTranslationMapper basicMapper = sqlSession.getMapper(BasicTranslationMapper.class);
            basicTranslation = basicMapper.getBasicTranslationByQuery(query);

            if (basicTranslation != null) {
                List<BasicExplains> explains = basicMapper.getBasicExplainsByBid(basicTranslation.getId());
                basicTranslation.setExplainsList(explains);
                List<WebTranslation> web = basicMapper.getWebTranslationByBid(basicTranslation.getId());
                basicTranslation.setWebTranslationList(web);

                updateBasicTranslation(basicMapper, basicTranslation);
            }

        } catch (IOException e) {
            return null;
        } finally {
            /*
            if (sqlSession != null) {
                sqlSession.close();
            }
            */
        }

        return basicTranslation;

    }


    private static void updateBasicTranslation(BasicTranslationMapper mapper, BasicTranslation basic) {
        basic.setDate(new Date());//最后一次查询时间设置为当前时间
        basic.setCount(basic.getCount() + 1);//增加一次查询次数
        mapper.updateBasicTranslation(basic);
    }


    public static boolean addBasicTranslation(BasicTranslation basic) {


        BasicTranslationMapper basicMapper = sqlSession.getMapper(BasicTranslationMapper.class);
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

        sqlSession.close();

        boolean addStatus = flag || ((n1 + n2) > 0);
        return addStatus;

    }

    public static Boolean updateBasicTranslationById(BasicTranslation basic) {
        if (sqlSession == null) {
            try {
                SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream
                        (resources));
                sqlSession = factory.openSession(true);
            } catch (IOException e) {
                return false;
            }
        }

        BasicTranslationMapper mapper = sqlSession.getMapper(BasicTranslationMapper.class);
        boolean flag = mapper.updateBasicTranslationById(basic);

        sqlSession.close();
        return flag;

    }

    public Integer getBasicTranslationCountsPlusOne() {
        BasicTranslationMapper mapper = sqlSession.getMapper(BasicTranslationMapper.class);
        return mapper.getBasicTranslationCounts() + 1;
    }


}
