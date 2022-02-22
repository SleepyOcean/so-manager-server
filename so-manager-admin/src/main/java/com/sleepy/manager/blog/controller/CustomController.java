package com.sleepy.manager.blog.controller;

import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.blog.common.CommonVO;
import com.sleepy.manager.blog.common.UnionResponse;
import com.sleepy.manager.blog.processor.DataProcessor;
import com.sleepy.manager.blog.service.CustomService;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.generation.domain.*;
import com.sleepy.manager.generation.service.*;
import com.sleepy.manager.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gehoubao
 * @create 2021-10-20 12:23
 **/
@CrossOrigin
@RestController
@RequestMapping("/api")
public class CustomController extends BaseController {

    @Autowired
    CustomService customService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IUserFavService userFavService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IUserLikeService userLikeService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IShareService shareService;

    @Autowired
    private IAppVersionService appVersionService;

    @Autowired
    private IHotSearchService hotSearchService;

    @Autowired
    private DataProcessor dataProcessor;

    @GetMapping("/home/banner")
    public UnionResponse getArticleBanner() {
        String banner = customService.getArticleBanner();
        return new UnionResponse.Builder().data(banner).status(HttpStatus.OK).build();
    }

    @PostMapping("/home/banner")
    public UnionResponse editArticleBannerWithAuth(@RequestBody CommonVO vo) {
        String banner = customService.editArticleBanner(vo.getHomeBanner());
        return new UnionResponse.Builder().data(banner).status(HttpStatus.OK).build();
    }

    @GetMapping("/aboutus")
    public UnionResponse getAboutUs() {
        AssembledData aboutUs = customService.getAboutUs();
        return new UnionResponse.Builder().data(aboutUs).status(HttpStatus.OK).build();
    }

    @PostMapping("/aboutus")
    public UnionResponse editAboutUsWithAuth(@RequestBody CommonVO vo) {
        String aboutUsStr = customService.editAboutUs(vo.getAppInfo());
        return new UnionResponse.Builder().data(aboutUsStr).status(HttpStatus.OK).build();
    }

    @GetMapping("/policy/private")
    public UnionResponse getPrivatePolicy() {
        AssembledData aboutUsStr = customService.getPrivatePolicy();
        return new UnionResponse.Builder().data(aboutUsStr).status(HttpStatus.OK).build();
    }

    @PostMapping("/policy/private")
    public UnionResponse editPrivatePolicyWithAuth(@RequestBody CommonVO vo) {
        String aboutUsStr = customService.editPrivatePolicy(vo.getPrivatePolicy());
        return new UnionResponse.Builder().data(aboutUsStr).status(HttpStatus.OK).build();
    }

    @GetMapping("/policy/service")
    public UnionResponse getServicePolicy() {
        AssembledData aboutUsStr = customService.getServicePolicy();
        return new UnionResponse.Builder().data(aboutUsStr).status(HttpStatus.OK).build();
    }

    @PostMapping("/policy/service")
    public UnionResponse editServicePolicyWithAuth(@RequestBody CommonVO vo) {
        String aboutUsStr = customService.editServicePolicy(vo.getServicePolicy());
        return new UnionResponse.Builder().data(aboutUsStr).status(HttpStatus.OK).build();
    }

    @PostMapping("/upload")
    public UnionResponse saveImage(MultipartFile file, String imageHost, String articleId) throws IOException {
        String dirPath = "avatar";
        if (!StringUtils.isEmpty(articleId)) {
            dirPath = "article" + File.separator + articleId;
        }
        String relPath = customService.uploadFile(file, dirPath);
        return new UnionResponse.Builder().status(HttpStatus.OK).data(imageHost + relPath).build();
    }

    @GetMapping("/me")
    public UnionResponse getMyProfile(HttpServletRequest request) {
        SysUser user = userService.selectUserById(getUserId());
        return new UnionResponse.Builder().data(user).status(HttpStatus.OK).build();
    }

    @GetMapping("/me/fav")
    public UnionResponse getMyFav(HttpServletRequest request) {
        SysUser user = userService.selectUserById(getUserId());
        UserFav fav = new UserFav(user.getUserId());
        List<UserFav> userFavList = userFavService.selectUserFavList(fav);
        Article article = new Article();
        String[] articleIds = userFavList.stream().map(userFav -> {
            return userFav.getArticleId().toString();
        }).collect(Collectors.toList()).toArray(new String[0]);
        List<Article> articleList = new ArrayList<>();
        if (articleIds.length > 0) {
            article.setIds(articleIds);
            articleList = articleService.selectArticleList(article);
        }
        return new UnionResponse.Builder().data(dataProcessor.processArticles(articleList)).status(HttpStatus.OK).build();
    }

    @GetMapping("/me/comment")
    public UnionResponse getMyComment(HttpServletRequest request) {
        SysUser user = userService.selectUserById(getUserId());
        Comment comment = new Comment(user.getUserId());
        List<Comment> userFavList = commentService.selectCommentList(comment);
        return new UnionResponse.Builder().data(userFavList).status(HttpStatus.OK).build();
    }

    @GetMapping("/me/like")
    public UnionResponse getMyLike(HttpServletRequest request) {
        SysUser user = userService.selectUserById(getUserId());
        UserLike like = new UserLike(user.getUserId());
        List<UserLike> userLikeList = userLikeService.selectUserLikeList(like);
        Article article = new Article();
        String[] articleIds = userLikeList.stream().map(userLike -> {
            return userLike.getArticleId().toString();
        }).collect(Collectors.toList()).toArray(new String[0]);
        List<Article> articleList = new ArrayList<>();
        if (articleIds.length > 0) {
            article.setIds(articleIds);
            articleList = articleService.selectArticleList(article);
        }
        return new UnionResponse.Builder().data(dataProcessor.processArticles(articleList)).status(HttpStatus.OK).build();
    }


    @GetMapping("/me/share")
    public UnionResponse getMyShare(HttpServletRequest request) {
        SysUser user = userService.selectUserById(getUserId());
        Share cond = new Share();
        cond.setUserId(user.getUserId());
        List<Share> result = shareService.selectShareList(cond);
        Article article = new Article();
        String[] articleIds = result.stream().map(share -> share.getArticleId().toString()).collect(Collectors.toList()).toArray(new String[0]);
        List<Article> articleList = new ArrayList<>();
        if (articleIds.length > 0) {
            article.setIds(articleIds);
            articleList = articleService.selectArticleList(article);
        }
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getMyShare successfully").data(dataProcessor.processArticles(articleList)).build();
    }

    @PutMapping("/me")
    public UnionResponse updateMyProfile(HttpServletRequest request, @RequestBody CommonVO vo) {
        String nickname = vo.getNickName();
        String avatar = vo.getAvatar();
        String gender = vo.getGender();

        SysUser user = userService.selectUserById(getUserId());
        if (!StringUtils.isEmpty(nickname)) {
            user.setUserName(nickname);
        }
        if (!StringUtils.isEmpty(avatar)) {
            user.setAvatar(avatar);
        }
        if (!StringUtils.isEmpty(gender)) {
            user.setSex(gender);
        }
        userService.updateUser(user);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("profile updated").build();
    }

    @PutMapping("/me/avatar")
    public UnionResponse updateAvatar(HttpServletRequest request, @RequestBody CommonVO vo) {
        String avatar = vo.getAvatar();

        SysUser user = userService.selectUserById(getUserId());
        if (!StringUtils.isEmpty(avatar)) {
            user.setAvatar(avatar);
            userService.updateUser(user);
            return new UnionResponse.Builder().status(HttpStatus.OK).message("updated my avatar").build();
        } else {
            return new UnionResponse.Builder().status(HttpStatus.BAD_REQUEST).message("avatar is null, cannot update").build();
        }
    }

    @PutMapping("/user/block")
    public UnionResponse addUserToBlackListWithAuth(@RequestBody CommonVO vo) {
        if (null == vo.getUserId()) {
            return new UnionResponse.Builder().message("Please provide userId!").status(HttpStatus.BAD_REQUEST).build();
        }
        SysUser user = userService.selectUserById(vo.getUserId());
        if (null == user) {
            return new UnionResponse.Builder().message("User is not exist!").status(HttpStatus.BAD_REQUEST).build();
        }
        user.setStatus("2");
        userService.updateUser(user);
        return new UnionResponse.Builder().message("Block user successfully.").status(HttpStatus.OK).build();
    }

    @PutMapping("/user/unblock")
    public UnionResponse unblockUserWithAuth(@RequestBody CommonVO vo) {
        if (null == vo.getUserId()) {
            return new UnionResponse.Builder().message("Please provide userId!").status(HttpStatus.BAD_REQUEST).build();
        }
        SysUser user = userService.selectUserById(vo.getUserId());
        if (null == user) {
            return new UnionResponse.Builder().message("User is not exist!").status(HttpStatus.BAD_REQUEST).build();
        }
        user.setStatus("0");
        userService.updateUser(user);
        return new UnionResponse.Builder().message("Unblock user successfully.").status(HttpStatus.OK).build();
    }

    @GetMapping("/app-version")
    public UnionResponse getAppVersion(@RequestParam(value = "appVersion", required = false) Long appVersion,
                                       @RequestParam(value = "description", required = false) String description) {
        AppVersion app = new AppVersion();
        app.setAppVersion(appVersion);
        app.setDescription(description);
        List<AppVersion> appVersionList = appVersionService.selectAppVersionList(app);
        return new UnionResponse.Builder().status(HttpStatus.OK).data(appVersionList).build();
    }

    @PostMapping("/app-version")
    public UnionResponse addAppVersion(@RequestBody AppVersion appVersion) {
        appVersionService.insertAppVersion(appVersion);
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }

    @PutMapping("/app-version")
    public UnionResponse updateAppVersion(@RequestBody AppVersion appVersion) {
        appVersionService.updateAppVersion(appVersion);
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }

    @DeleteMapping("/app-version/{id}")
    public UnionResponse deleteAppVersion(@PathVariable("id") Long id) {
        appVersionService.deleteAppVersionById(id);
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }

    @GetMapping("/app-version/latest")
    public UnionResponse getLatestAppVersion() {
        AppVersion app = new AppVersion();
        List<AppVersion> appVersionList = appVersionService.selectAppVersionList(app);
        AppVersion latestAppVersion = appVersionList.isEmpty() ? null : appVersionList.get(0);
        return new UnionResponse.Builder().status(HttpStatus.OK).data(latestAppVersion).build();
    }

    @GetMapping("/hot-search")
    public UnionResponse getHotSearch(@RequestParam(value = "keyword", required = false) String keyword) {
        HotSearch hotSearch = new HotSearch();
        hotSearch.setKeyword(keyword);
        List<HotSearch> result = hotSearchService.selectHotSearchList(hotSearch);
        return new UnionResponse.Builder().status(HttpStatus.OK).data(result).build();
    }

    @PostMapping("/hot-search")
    public UnionResponse addHotSearch(@RequestBody HotSearch hotSearch) {
        hotSearchService.insertHotSearch(hotSearch);
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }

    @PutMapping("/hot-search")
    public UnionResponse updateHotSearch(@RequestBody HotSearch hotSearch) {
        hotSearchService.updateHotSearch(hotSearch);
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }

    @DeleteMapping("/hot-search/{id}")
    public UnionResponse deleteHotSearch(@PathVariable("id") Long id) {
        hotSearchService.deleteHotSearchById(id);
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }
}