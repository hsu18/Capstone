package com.example.capstone.service;

import com.example.capstone.domain.*;
import com.example.capstone.domain.Date;
import com.example.capstone.repository.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Service
public class CategoryService {

    private final CertRepository certRepository;
    private final UserRepository userRepository;
    private final UserViewRepository userViewRepository;
    private final EsCertRepository esCertRepository;
    private final EsDateRepository esDateRepository;
    private final EsViewRepository esViewRepository;
    private final EsUserRepository esUserRepository;

    private final CrawlingService crawlingService;

    @Autowired
    public CategoryService(CertRepository certRepository, UserRepository userRepository,
                           UserViewRepository userViewRepository, EsCertRepository esCertRepository,
                           EsDateRepository esDateRepository, EsViewRepository esViewRepository,
                           EsUserRepository esUserRepository, CrawlingService crawlingService) {
        this.certRepository = certRepository;
        this.userRepository = userRepository;
        this.userViewRepository = userViewRepository;
        this.esCertRepository = esCertRepository;
        this.esDateRepository = esDateRepository;
        this.esViewRepository = esViewRepository;
        this.esUserRepository = esUserRepository;

        this.crawlingService = crawlingService;
    }

    //태그 분할
    public String[] getTags(String tag){
        return tag.split(",");
    }

    //모든 자격증 조회
    public List<EsCert> getEsCerts(){ return esCertRepository.findAll(); }

    //자격증 검색
    public List<EsCert> getCertsByName(String name){
        return esCertRepository.findEsCertsByName(name);
    }

    // tag를 갖고있는 자격증 정보 추출
    public List<EsCert> getCertsByTag(String tag){
        return esCertRepository.findEsCertsByTag(tag);
    }

    // category를 갖고있는 자격증 정보 추출
    public List<EsCert> getCertsByCategory(String category){
        return esCertRepository.findEsCertsByMain(category);
    }
    public List<EsCert> getCertsByCategory(String category, int mini) {
        List<String> subs = crawlingService.findSubs(category);
        String sub = subs.get(mini - 1);

        return esCertRepository.findEsCertsBySub(sub);
    }

    // category와 name을 갖고있는 자격증 정보 추출
    public List<EsCert> getCertsByNameAndCategory(String searchname, String category){
        return esCertRepository.findEsCertByNameAndMain(searchname, category);
    }
    public List<EsCert> getCertsByNameAndCategory(String searchname, String category, int mini){
        List<String> subs = crawlingService.findSubs(category);
        String sub = subs.get(mini - 1);

        return esCertRepository.findEsCertByNameAndSub(searchname, sub);
    }

    // 자격증 id 기준으로 Cert, Date에서 정보 추출 후 AllCert 객체에 저장 후 리턴
    public AllCert getAllCertById(String id){
        // id로 EsCert 정보 추출 후 Cert로 변환
        EsCert esCert = esCertRepository.findEsCertById(id);
        Cert cert = new Cert(esCert);

        // id로 EsDate 정보 추출 후 Date로 변환
        List<EsDate> esDates = esDateRepository.findEsDatesByCertid(Integer.parseInt(id));
        List<Date> dates = new ArrayList<>();

        for(EsDate esDate : esDates){
            dates.add(new Date(esDate, cert));
        }

        // Cert, List<Date>를 AllCert에 저장 후 리턴
        return new AllCert(cert, dates);
    }

    // 자격증 조회수 증가 및 사용자 별 자격증 기록 남기기
    public void view_increase(int certid, int userid){

        int is_user = 0;

        // certid로 EsCert 정보 추출 후 Cert로 변환
        EsCert esCert = esCertRepository.findEsCertById(Integer.toString(certid));
        Cert cert = new Cert(esCert);

        // Cert table의 views 올리기
        int view = cert.getViews();
        cert.setViews(++view);
        certRepository.saveAndFlush(cert);
        esCertRepository.refresh();

        // 인수로받은 userid로 유저목록 조회 후 등록되지 않았으면 return
        List<User> users = userRepository.findAll();
        for(User user : users) {
            if (user.getId() == userid)
                is_user = 1;
        }
        if(is_user == 0) return;

        // 모든 EsView 정보 추출 후 List에 저장
        List<EsView> esViews = esViewRepository.findAll();

        // 등록된 user이면 user별 조회 자격증 기록
        // (자격증, 유저) 쌍의 조회 정보가 이미 들어가있으면 조회수 +1
        for(EsView esView : esViews){
            if((esView.getUserid() == userid) && (esView.getCertid() == certid)) {
                UserView userView = new UserView(esView);
                view = userView.getViews();
                userView.setViews(++view);
                userViewRepository.saveAndFlush(userView);
                return;
            }
        }

        // (자격증, 유저) 쌍의 조회 정보가 없으면 새로 생성
        UserView userView = new UserView();
        userView.setUserid(userid);
        userView.setCertid(certid);
        userView.setViews(1);
        userViewRepository.saveAndFlush(userView);
    }

    public Page<EsCert> getEsCertsByCategory(int startAt, String category){
        Pageable pageable = PageRequest.of(startAt, 10);
        Pageable pageable2 = PageRequest.of(startAt, 10, Sort.by(new Sort.Order(Sort.Direction.DESC,"name")));
        return esCertRepository.findEsCertsByMain(pageable2, category);
    }

    public Page<EsCert> getEsCertsByCategory(int startAt, String category, int mini){
        List<String> subs = crawlingService.findSubs(category);
        String sub = subs.get(mini - 1);

        Pageable pageable = PageRequest.of(startAt, 10);
        Pageable pageable2 = PageRequest.of(startAt, 10, Sort.by(new Sort.Order(Sort.Direction.DESC,"name")));
        return esCertRepository.findEsCertsBySub(pageable2, sub);
    }

    public Page<EsCert> getEsCertsByTag(int startAt, String tag){
        Pageable pageable = PageRequest.of(startAt, 10);
        Pageable pageable2 = PageRequest.of(startAt, 10, Sort.by(new Sort.Order(Sort.Direction.DESC,"name")));
        return esCertRepository.findEsCertsByTag(pageable2, tag);
    }

    public Page<EsCert> getEsCertsByName(int startAt, String name){
        Pageable pageable = PageRequest.of(startAt, 10);
        Pageable pageable2 = PageRequest.of(startAt, 10, Sort.by(new Sort.Order(Sort.Direction.DESC,"name")));
        Pageable pageable3 = PageRequest.of(startAt, 10, Sort.by("_score").descending());
        return esCertRepository.findEsCertsByName(pageable2, name);
    }

    public Page<EsCert> searchEsCertsByCategory(int startAt, String searchname, String category){
        Pageable pageable = PageRequest.of(startAt, 10);
        Pageable pageable2 = PageRequest.of(startAt, 10, Sort.by(new Sort.Order(Sort.Direction.DESC,"name")));
        return esCertRepository.findEsCertByNameAndMain(pageable2, searchname, category);
    }
    public Page<EsCert> searchEsCertsByCategory(int startAt, String searchname, String category, int mini){
        List<String> subs = crawlingService.findSubs(category);
        String sub = subs.get(mini - 1);

        Pageable pageable = PageRequest.of(startAt, 10);
        Pageable pageable2 = PageRequest.of(startAt, 10, Sort.by(new Sort.Order(Sort.Direction.DESC,"name")));
        return esCertRepository.findEsCertByNameAndSub(pageable2, searchname, sub);
    }

    // 연관자격증 알고리즘
    public List<Cert> relatedCerts(String id){

        String sel_tags[];

        EsCert esCert = esCertRepository.findEsCertById(id);
        List<EsCert> esCerts = new ArrayList<>();

        sel_tags = esCert.getTag().split(",");
        HashMap<String, Integer> relate_count = new HashMap<>();

        for(String t : sel_tags)
            esCerts.addAll(esCertRepository.findEsCertsByTag(t));

        for(EsCert e : esCerts){
            if(relate_count.containsKey(e.getId())){
                relate_count.put(e.getId(), relate_count.get(e.getId())+1);
            }
            else {
                relate_count.put(e.getId(), 1);
            }
        }

        List<Map.Entry<String, Integer>> count_sort = new ArrayList<>(relate_count.entrySet());

        Collections.sort(count_sort, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (o1.getValue() < o2.getValue()) return 1;
                else if (o1.getValue().equals(o2.getValue())) {
                    if (esCertRepository.findEsCertById(o1.getKey()).getViews() < esCertRepository.findEsCertById(o2.getKey()).getViews())
                        return 1;
                    else if (esCertRepository.findEsCertById(o1.getKey()).getViews() > esCertRepository.findEsCertById(o2.getKey()).getViews()) return -1;
                    else return 0;
                }
                else return -1;
            }
        });

        List<Cert> certs = new ArrayList<>();

        for(Map.Entry c : count_sort){
            if(c.getKey().equals(esCert.getId())) continue;
            if(certs.size() >= 5) break;
            certs.add(new Cert(esCertRepository.findEsCertById(String.valueOf(c.getKey()))));
        }

        return certs;
    }
    public List<Cert> recommendCerts(String certid, String userid){
        // 현재 자격증을 본 user들의 view 목록
        List<EsView> esViews = esViewRepository.findEsViewsByCertid(Integer.parseInt(certid));
        List<EsView> recViews = new ArrayList<EsView>();

        for(EsView view : esViews){
            if (view.getUserid() == Integer.parseInt(userid)) continue;
            recViews.addAll(esViewRepository.findEsViewsByUserid(view.getUserid())); // 자격증을 본 user id
        }

        HashMap<String, Integer> recommendCount = new HashMap<>();

        for(EsView view : recViews){
            if(recommendCount.containsKey(view.getCertid()))
                recommendCount.put(Integer.toString(view.getCertid()), recommendCount.get(view.getCertid()) + view.getViews());
            else
                recommendCount.put(Integer.toString(view.getCertid()), view.getViews());
        }

        List<Map.Entry<String, Integer>> recommendSort = new ArrayList<>(recommendCount.entrySet());

        Collections.sort(recommendSort, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() < o2.getValue() ? 1 : -1;
            }
        });

        List<Cert> certs = new ArrayList<>();

        for(Map.Entry c : recommendSort){
            if(c.getKey().equals(esCertRepository.findEsCertById(certid).getId())) continue;
            if(certs.size() >= 3) break;
            certs.add(new Cert(esCertRepository.findEsCertById(String.valueOf(c.getKey()))));

        }

        return certs;

    }

    public List<Cert> userRecCerts(String userid) {
        String myTags[];
        EsUser esUser = esUserRepository.findEsUserById(userid);
        List<EsUser> esUsers = esUserRepository.findAll();
        myTags = esUser.getUser_tag().split(","); //현재 접속 중인 user의 tag 배열

        HashMap<String, Integer> usrTagCount = new HashMap<>();
        for (EsUser another : esUsers) { //모든 user의 태그를 하나씩 비교... (이게 과연 맞는가)
            if (another.getId().equals(userid)) continue; //for문의 user가 자기 자신이면 pass
            for (String myT : myTags) {
                if (another.getUser_tag().contains(myT)) { // 단어가 포함되어 있는지로 찾기
                    if (usrTagCount.containsKey(another.getId()))
                        usrTagCount.put(another.getId(), usrTagCount.get(another.getId()) + 1);
                    else
                        usrTagCount.put(another.getId(), 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> usrTagSort = new ArrayList<>(usrTagCount.entrySet());
        // 유사도순으로 정렬인데 어차피 총 자격증 합 구해서 정렬하니 빼도 될듯
        /*Collections.sort(usrTagSort, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() < o2.getValue() ? 1 : -1;
            }
        });*/

        HashMap<String, Integer> usrRecCount = new HashMap<>(); // 각 자격증의 총 유사도 HashMap

        for (Map.Entry recUser : usrTagSort) {
            List<EsView> tagCerts = esViewRepository.findEsViewsByUserid(Integer.parseInt(recUser.getKey().toString()));
            for (EsView tagCert : tagCerts) { // 유저 태그 겹치는 수와 곱하기 해서 총 유사도 구하기
                if (usrRecCount.containsKey(Integer.toString(tagCert.getCertid()))) {
                    usrRecCount.put(Integer.toString(tagCert.getCertid()),
                            usrRecCount.get(Integer.toString(tagCert.getCertid())) +
                                    (tagCert.getViews() * Integer.parseInt(recUser.getValue().toString())));
                } else {
                    usrRecCount.put(Integer.toString(tagCert.getCertid()),
                            tagCert.getViews() * Integer.parseInt(recUser.getValue().toString()));
                }
            }
        }

        List<Map.Entry<String, Integer>> usrRecSort = new ArrayList<>(usrRecCount.entrySet());
        Collections.sort(usrRecSort, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() < o2.getValue() ? 1 : -1;
            }
        });

        List<Cert> certs = new ArrayList<>();
        for (Map.Entry c : usrRecSort) {
            certs.add(new Cert(esCertRepository.findEsCertById(c.getKey().toString())));
            if(certs.size() >= 6) break;
        }

        return certs;
    }
}