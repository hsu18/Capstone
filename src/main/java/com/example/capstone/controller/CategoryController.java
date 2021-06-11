package com.example.capstone.controller;

import com.example.capstone.domain.AllCert;
import com.example.capstone.domain.Category;
import com.example.capstone.domain.Cert;
import com.example.capstone.domain.EsCert;
import com.example.capstone.service.CategoryService;
import com.example.capstone.service.CrawlingService;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class CategoryController {

    public RestHighLevelClient elasticsearchClient;
    private final CategoryService categoryService;
    private final CrawlingService crawlingService;

    @Autowired
    public CategoryController(RestHighLevelClient elasticsearchClient,
                              CategoryService categoryService,
                              CrawlingService crawlingService) {
        this.elasticsearchClient = elasticsearchClient;
        this.categoryService = categoryService;
        this.crawlingService = crawlingService;
        crawlingService.init_Category();
        crawlingService.crawling();
    }

    // 메인 페이지 mapping
    @GetMapping("/")
    public String main(Model model){
        model.addAttribute("allCategories", crawlingService.categories);
        return "main";
    }

    // 카테고리 페이지 mapping
    @RequestMapping(value = "/category/{category}/{mini}", method = RequestMethod.GET)
    public String category(@RequestParam(value = "page", defaultValue = "0") int page,
                           @PathVariable(value = "category") String category,
                           @PathVariable(value = "mini") int mini, Model model){
        if(mini == 0) {
            model.addAttribute("certs", categoryService.getCertsByCategory(category));
            model.addAttribute("pagedCerts", categoryService.getEsCertsByCategory(page, category));
        }
        else {
            model.addAttribute("certs", categoryService.getCertsByCategory(category, mini));
            model.addAttribute("pagedCerts", categoryService.getEsCertsByCategory(page, category, mini));
        }
        model.addAttribute("maxPage", 5);

        model.addAttribute("category", category);
        model.addAttribute("subs", crawlingService.findSubs(category));
        model.addAttribute("tag", "");

        model.addAttribute("mini", mini);
        model.addAttribute("searchword", "");

        model.addAttribute("selpage", page);

        model.addAttribute("allCategories", crawlingService.categories);
        return "categorylist";
    }

    // 카테고리 상세정보 페이지 mapping
    @RequestMapping("/category")
    public String detailCategory(@RequestParam("certid") int certid,
                                 @RequestParam("userid") int userid, Model model){
        AllCert allCert = categoryService.getAllCertById(Integer.toString(certid));
        categoryService.view_increase(certid, userid);
        String[] tags = null;
        if(allCert.getCert().getTag() != null)
            tags = categoryService.getTags(allCert.getCert().getTag());
        List<Cert> relatedCerts = categoryService.relatedCerts(Integer.toString(certid));
        List<Cert> recommandCerts = categoryService.recommendCerts(Integer.toString(certid),
                Integer.toString(userid));

        model.addAttribute("allCert", allCert);
        model.addAttribute("tags", tags);
        model.addAttribute("relatedCerts", relatedCerts);
        model.addAttribute("recommandCerts", recommandCerts);
        model.addAttribute("allCategories", crawlingService.categories);
        return "detail";
    }

    // 검색기능 - 페이징 들어가야함
    @RequestMapping(value="/search", method = {RequestMethod.POST, RequestMethod.GET})
    public String searchCert(@RequestParam(value = "page", defaultValue = "0") int page,
                             @ModelAttribute(name = "searchname") String searchname, Model model){

        model.addAttribute("certs", categoryService.getCertsByName(searchname));
        model.addAttribute("pagedCerts", categoryService.getEsCertsByName(page, searchname));
        model.addAttribute("maxPage", 5);

        model.addAttribute("searchword", searchname);
        model.addAttribute("category", "");
        model.addAttribute("subs", "");
        model.addAttribute("tag", "");
        model.addAttribute("mini", "");

        model.addAttribute("selpage", page);
        model.addAttribute("allCategories", crawlingService.categories);

        return "categorylist";
    }

    @RequestMapping(value="/{category}/search", method = {RequestMethod.POST, RequestMethod.GET})
    public String searchCert(@RequestParam(value = "page", defaultValue = "0") int page,
                             @PathVariable(value = "category") String category,
                             @ModelAttribute(name = "searchname") String searchname,
                             @ModelAttribute(name = "mini") int mini, Model model){

        if(mini == 0){
            model.addAttribute("certs", categoryService.getCertsByNameAndCategory(searchname, category));
            model.addAttribute("pagedCerts", categoryService.searchEsCertsByCategory(page, searchname, category));
        }
        else{
            model.addAttribute("certs", categoryService.getCertsByNameAndCategory(searchname, category, mini));
            model.addAttribute("pagedCerts", categoryService.searchEsCertsByCategory(page, searchname, category, mini));
        }

        model.addAttribute("maxPage", 5);

        model.addAttribute("searchword", searchname);
        model.addAttribute("category", category);
        model.addAttribute("subs", crawlingService.findSubs(category));
        model.addAttribute("tag", "");
        model.addAttribute("mini", mini);

        model.addAttribute("selpage", page);
        model.addAttribute("allCategories", crawlingService.categories);

        return "categorylist";
    }

    @RequestMapping(value="/search/{tag}", method = {RequestMethod.POST, RequestMethod.GET})
    public String searchCertByTag(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @PathVariable(value = "tag") String tag, Model model){

        model.addAttribute("certs", categoryService.getCertsByTag(tag));
        model.addAttribute("pagedCerts", categoryService.getEsCertsByTag(page, tag));
        model.addAttribute("maxPage", 5);

        model.addAttribute("searchword", "");
        model.addAttribute("category", "");
        model.addAttribute("subs", "");
        model.addAttribute("tag", tag);
        model.addAttribute("mini", "");

        model.addAttribute("selpage", page);
        model.addAttribute("allCategories", crawlingService.categories);

        return "categorylist";
    }

    // my page mapping
    @RequestMapping("/mypage")
    public String mypage(@RequestParam("userid") int userid, Model model){
        List<Cert> userRecCerts = categoryService.userRecCerts(Integer.toString(userid));

        model.addAttribute("userid", userid);
        model.addAttribute("allCategories", crawlingService.categories);
        model.addAttribute("userRecCerts", userRecCerts);

        return "mypage";
    }

    //    아래 기능은
    //    추가 구현 필요
    // login page mapping
    @RequestMapping("/login")
    public String login(){
        return "login";
    }
}