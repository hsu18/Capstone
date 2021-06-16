package com.example.capstone.service;

import com.example.capstone.domain.*;
import com.example.capstone.repository.CertRepository;
import com.example.capstone.repository.DateRepository;
import com.example.capstone.repository.MapSubTagRepository;
import com.example.capstone.repository.MetaRepository;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CrawlingService {

    public List<Category> categories = new ArrayList<>();
    public List<MapSubTag> allsubs = new ArrayList<>();
    public RestHighLevelClient elasticsearchClient;
    private final CertRepository certRepository;
    private final DateRepository dateRepository;
    private final MetaRepository metaRepository;
    private final MapSubTagRepository mapSubTagRepository;

    @Autowired
    CrawlingService(CertRepository certRepository, DateRepository dateRepository,
                    MetaRepository metaRepository, RestHighLevelClient elasticsearchClient, MapSubTagRepository mapSubTagRepository) {
        this.certRepository = certRepository;
        this.dateRepository = dateRepository;
        this.metaRepository = metaRepository;
        this.elasticsearchClient = elasticsearchClient;
        this.mapSubTagRepository = mapSubTagRepository;
    }

    public List<String> findSubs(String category) {
        if (category.equals("기타"))
            return null;
        String[] subs = null;
        for (Category c : categories) {
            if (c.getCategoryName().equals(category))
                subs = c.getSubCategoryName().split(",");
        }
        if (subs == null)
            return null;

        return Arrays.asList(subs);
    }

    public void saveCertification(Cert cert, List<Date> dates) {
        cert.setDates(dates);
        certRepository.saveAndFlush(cert);

        for (Date d : dates) {
            d.setCert(cert);
            dateRepository.saveAndFlush(d);
        }
    }

    public void crawling() {
        int jmCd = 1;
        String jmCd_fomatted;
        String certUrl;
        String dateUrl;
        String infoUrl;
        String[] subcategroies = "경영 회계 사무 금융·보험 문화·예술 음악 미용 공예 스포츠 전기 전자 정보 통신 IT 기계 화학 과학 환경 의료 사회복지 법 언어 교육 조리 식품 생산 영업·판매 농림어업 광업 재료 의류 인쇄·사진 운전 건설 경비 안전 기타".split(" ");

        for (String s : subcategroies)
            allsubs.add(mapSubTagRepository.findMapSubTagBySubcategoryName(s));

        while (jmCd < 10000) {
            System.out.println("[" + jmCd + "번째 크롤링...]");

            if (jmCd < 1000) {
                jmCd_fomatted = String.format("%04d", jmCd);
                certUrl = "https://www.q-net.or.kr/crf005.do?id=crf00503&gSite=Q&jmCd=" + jmCd_fomatted + "&qualgb=S";
                dateUrl = "https://www.q-net.or.kr/crf005.do?id=crf00503s02&gSite=Q&gId=" + "&jmCd=" + jmCd_fomatted + "&jmInfoDivCcd=" + "B0";
                infoUrl = "https://www.q-net.or.kr/crf005.do?id=crf00503s02&gSite=Q&gId=" + "&jmCd=" + jmCd_fomatted + "&jmInfoDivCcd=" + "A0";
            } else {
                certUrl = "https://www.q-net.or.kr/crf005.do?id=crf00503&gSite=Q&jmCd=" + jmCd + "&qualgb=S";
                dateUrl = "https://www.q-net.or.kr/crf005.do?id=crf00503s02&gSite=Q&gId=" + "&jmCd=" + jmCd + "&jmInfoDivCcd=" + "B0";
                infoUrl = "https://www.q-net.or.kr/crf005.do?id=crf00503s02&gSite=Q&gId=" + "&jmCd=" + jmCd + "&jmInfoDivCcd=" + "A0";
            }

            Meta meta = new Meta();

            if (jmCd == 5415 || jmCd == 8918 || jmCd == 9101 || jmCd == 9439 || jmCd == 9440 || ((9749 < jmCd) && (jmCd < 9753)) || ((9550 < jmCd) && (jmCd < 9563))) {
                meta.setIs_succeed("FAIL");
                meta.setUrl(certUrl);
                metaRepository.saveAndFlush(meta);
                jmCd++;
                continue;
            }

            try {
                Document certDoc = Jsoup.connect(certUrl).get();
                Document dateDoc = Jsoup.connect(dateUrl).get();
                Document infoDoc = Jsoup.connect(infoUrl).get();

                //자격증이름 =============================================================================
                Elements name = certDoc.select("#jmName");
                String nameStr = name.text();

                if (!nameStr.equals("") && !nameStr.endsWith("기능사보") && !nameStr.endsWith("다기능기술자")) {
                    //System.out.println("자격증이름> " + nameStr + "(" + jmCd + ")");

                    //관련부처 =============================================================================
                    Elements ministry = certDoc.select(".tab_info dd:nth-child(6)");
                    String ministryStr = ministry.text();
                    if (ministryStr.length() > 2)
                        ministryStr = ministryStr.substring(2);
                    else
                        ministryStr = "";

                    //시행기관 =============================================================================
                    Elements agency = certDoc.select(".tab_info dd:nth-child(8)");
                    String agencyStr = agency.text();
                    if (agencyStr.length() > 2)
                        agencyStr = agencyStr.substring(2);
                    else
                        agencyStr = "";

                    //응시비용 =============================================================================
                    Elements dlInfo = dateDoc.select(".dlInfo");
                    String costStr = "";

                    if (!dlInfo.text().equals("")) { //dlInfo가 있는경우
                        Elements dt_rowList = dlInfo.select("dt");
                        Elements dd_rowList = dlInfo.select("dd");
                        List<String> dts = dt_rowList.eachText();

                        //dt에 수수료항목이 있는지, 있다면 해당 dt의 인덱스구하기
                        for (int idx = 0; idx < dts.size(); idx++) {
                            if (dts.get(idx).contains("수수료")) {
                                costStr = crawl_info(idx, dd_rowList);
                            }
                        }
                    }

                    //개요, 수행직무, 진로 및 전망
                    Elements dlInfo2 = infoDoc.select(".dlInfo");
                    String summaryStr = "";
                    String dutyStr = "";
                    String careerStr = "";

                    if (!dlInfo2.text().equals("")) { //dlInfo가 있는경우
                        Elements dt_rowList2 = dlInfo2.select("dt");
                        Elements dd_rowList2 = dlInfo2.select("dd");
                        List<String> dts2 = dt_rowList2.eachText();

                        for (int idx2 = 0; idx2 < dts2.size(); idx2++) {
                            if (dts2.get(idx2).contains("개요"))
                                summaryStr = crawl_info(idx2, dd_rowList2);
                            else if (dts2.get(idx2).contains("진로"))
                                careerStr = crawl_info(idx2, dd_rowList2);
                            else if (dts2.get(idx2).contains("수행"))
                                dutyStr = crawl_info(idx2, dd_rowList2);
                        }
                    }

                    //상세정보형식 ================
                    //Elements confirm = dateDoc.select(".dlInfo dt");
                    //System.out.println("\t상세정보포맷> " + confirm.eachText());

                    Cert cert = new Cert();
                    cert.setName(nameStr);
                    cert.setMinistry(ministryStr);
                    cert.setAgency(agencyStr);
                    cert.setCost(costStr);
                    cert.setViews(0);
                    cert.setUrl(certUrl);
                    cert.setTag("");
                    cert.setMain("");
                    cert.setSub("");
                    cert.setSummary(summaryStr);
                    cert.setDuty(dutyStr);
                    cert.setCareer(careerStr);

                    analyze(cert, nameStr);

//                    AnalyzeRequest request = new AnalyzeRequest().index("certindex").analyzer("nori").text(nameStr);
//                    AnalyzeResponse response = elasticsearchClient.indices().analyze(request, RequestOptions.DEFAULT);
//                    List<AnalyzeResponse.AnalyzeToken> analyzeTokens = response.getTokens();
//                    for (AnalyzeResponse.AnalyzeToken analyzeToken : analyzeTokens) {
//                        String token = analyzeToken.getTerm();
//                        set_category(token, cert);
//                        cert.setTag(cert.getTag() + token + ",");
//                    }
//                    int num = cert.getTag().length();
//                    if(num!=0)
//                        cert.setTag(cert.getTag().substring(0, num-1));

                    //시험일정이 있는지 확인
                    Elements Table = dateDoc.select("div.tbl_normal");
                    List<Date> dates = new ArrayList<>();

                    //테이블이 있는 경우
                    if (!(Table.text().equals(""))) {
                        Elements term = Table.select("table tbody tr");

                        String termStr = term.text();
                        if (termStr.contains("없")) { //테이블은 있는데 시험일정이 없는 경우
                            Date date = new Date();
                            date.setDivision("");
                            date.setApply("");
                            date.setDate("");
                            date.setNotif("");
                            dates.add(date);
                            saveCertification(cert, dates);

                        } else { //테이블도 있고 시험일정도 있는경우

                            //시험일정테이블 확인 ====================
                            Elements termTable = Table.select("table thead th");
                            List<String> termTableList = termTable.eachText();
                            //System.out.println("\t시험일정포맷> " + termTableList);

                            Elements rowList = Table.select("table tbody tr");
                            if (!(termTableList.get(1).contains("필기"))) {
                                // *필기,실기 나뉘어있지 않을때
                                for (Element row : rowList) {
                                    Elements cellList = row.select("td");
                                    Date date = new Date();
                                    date.setDivision(cellList.get(0).text());
                                    date.setApply(cellList.get(1).text());
                                    date.setDate(cellList.get(3).text());
                                    date.setNotif(cellList.get(6).text());
                                    dates.add(date);
                                }

                            } else {
                                // *필기,실기 나뉘어있을때
                                for (Element row : rowList) {
                                    Elements cellList = row.select("td");
                                    Date date = new Date();
                                    date.setDivision(cellList.get(0).text() + "(필기)");
                                    date.setApply(cellList.get(1).text());
                                    date.setDate(cellList.get(2).text());
                                    date.setNotif(cellList.get(3).text());
                                    dates.add(date);

                                    Date date2 = new Date();
                                    date2.setDivision(cellList.get(0).text() + "(실기)");
                                    date2.setApply(cellList.get(4).text());
                                    date2.setDate(cellList.get(5).text());
                                    date2.setNotif(cellList.get(6).text());
                                    dates.add(date2);
                                }
                            }
                            saveCertification(cert, dates);
                        }
                    } else { //테이블이 아예 없는경우
                        Date date = new Date();
                        date.setDivision("");
                        date.setApply("");
                        date.setDate("");
                        date.setNotif("");
                        dates.add(date);
                        saveCertification(cert, dates);
                    }
                }

                meta.setIs_succeed("SUCCESS");
                meta.setUrl(certUrl);
                metaRepository.saveAndFlush(meta);

                jmCd++;

            } catch (IOException e) {
                meta.setIs_succeed("FAIL");
                meta.setUrl(certUrl);
                metaRepository.saveAndFlush(meta);

                //e.printStackTrace();
                jmCd++;
            }
        }
    }

    public void init_Category() {
        categories.add(new Category("상경계", "경영,회계,사무,금융·보험"));
        categories.add(new Category("예체능", "문화·예술,음악,미용,공예,스포츠,인쇄·사진"));
        categories.add(new Category("공학", "전기,전자,정보,통신,IT,기계"));
        categories.add(new Category("환경·과학", "화학,과학,환경"));
        categories.add(new Category("복지·교육", "의료,사회복지,법,언어,교육"));
        categories.add(new Category("음식", "조리,식품"));
        categories.add(new Category("생산·영업", "생산,영업·판매,농림어업,광업,재료,의류"));
        categories.add(new Category("운전·건설", "운전,건설"));
        categories.add(new Category("경비·안전", "경비,안전"));
        categories.add(new Category("기타", null));
    }

    public void set_category(String tag, Cert cert) {
        for (MapSubTag m : allsubs) {
            List<String> tags = Arrays.asList(m.getTag().split(","));
            if (tags.contains(tag)) {
                if (cert.getSub().contains(m.getSubcategoryName())) //서브카테고리 있는경우
                    continue;
                if (cert.getMain().contains(m.getCategoryName())) {
                    cert.setSub(cert.getSub() + "," + m.getSubcategoryName());
                    continue;
                }
                cert.setMain(cert.getMain() + "," + m.getCategoryName());
                cert.setSub(cert.getSub() + "," + m.getSubcategoryName());
            }
        }
    }

    public String crawl_info(int idx, Elements rowList) {
        String dd_content2 = rowList.get(idx).text();//수수료 dd추출
        if (dd_content2.contains("html")) {  //수수료 dd가 iframe+textarea(html) 형식인경우
            Document dd_doc2 = Jsoup.parse(dd_content2);
            Elements dd_body2 = dd_doc2.select("body");
            return dd_body2.text();
        } else  //수수료 dd가 html형식이 아닌경우
            return dd_content2;
    }

    public void analyze(Cert cert, String str) {
        try {
                AnalyzeRequest request = new AnalyzeRequest().index("certindex").analyzer("nori").text(str);
                AnalyzeResponse response = elasticsearchClient.indices().analyze(request, RequestOptions.DEFAULT);
                List<AnalyzeResponse.AnalyzeToken> analyzeTokens = response.getTokens();
                for (AnalyzeResponse.AnalyzeToken analyzeToken : analyzeTokens) {
                    String token = analyzeToken.getTerm();
                    set_category(token, cert);
                    cert.setTag(cert.getTag() + token + ",");
                }
                int num = cert.getTag().length();
                if (num != 0)
                    cert.setTag(cert.getTag().substring(0, num - 1));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
