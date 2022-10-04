package com.example.gebiwangshushuxilie;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.gebiwangshushuxilie", appContext.getPackageName());
    }

    @Test
    public void a(){
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<meta charset=\"utf-8\">\n" +
                "\t<meta name=\"viewport\" content=\"initial-scale=1, maximum-scale=1, user-scalable=no\">\n" +
                "\t<meta name=\"format-detection\" content=\"telephone=no\">\n" +
                "\t<title>登录</title>\n" +
                "\t<link rel=\"stylesheet\" href=\"/static/css/index/common.css\"/>\n" +
                "\t<link rel=\"stylesheet\" href=\"/static/css/apprentice/login.css?v=3.6\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div style=\"display: none\">\n" +
                "\t<input class=\"user_code\" type=\"hidden\" value=\"678c95a4e3c0eec2f81ef1ef1552a15f\">\n" +
                "\t<input class=\"user_oid\" type=\"hidden\" value=\"52918\">\n" +
                "\t<input class=\"user_mid\" type=\"hidden\" value=\"112644\">\n" +
                "\t<input class=\"user_token\" type=\"hidden\" value=\"571a76b31dc41d8085fbc06cdbf1bd68\">\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"login_wapp\">\n" +
                "\t<div>\n" +
                "\t\t<img src=\"/static/images/login/logo_u27.png\">\n" +
                "\t</div>\n" +
                "\n" +
                "\t<div class=\"login_logo\">\n" +
                "\t\t<img src=\"/static/images/login/u29.png\">\n" +
                "\t</div>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<div>\n" +
                "\t<div class=\"apprentice_master_wapp display-flexbox\">\n" +
                "\t\t<div class=\"apprentice_login  login_master_btn\"  login_bs=\"1\">徒弟登录</div>\n" +
                "\t\t<div></div>\n" +
                "\t\t<div class=\"apprentice_master\" login_bs=\"2\">师傅登录</div>\n" +
                "\t</div>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<div>\n" +
                "\t<div class=\"loginContent mlp20 mrp20\">\n" +
                "\t\t<div>\n" +
                "\t\t\t<div class=\"logErr\"></div>\n" +
                "\t\t</div>\n" +
                "\t\t<div class=\"loginContent_bg display-flexbox\">\n" +
                "\t\t\t<div>\n" +
                "\t\t\t\t<img src=\"/static/images/login/u14.png\">\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<div class=\"flexbox-children loginInput_wrapp\" id=\"userName\">\n" +
                "\t\t\t\t<input class=\"userName loginInput loginContent_bg\" type=\"text\" name=\"userName\" placeholder=\"账号\">\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t\t<div class=\"loginContent_bg display-flexbox mtp20\">\n" +
                "\t\t\t<div>\n" +
                "\t\t\t\t<img src=\"/static/images/login/u16.png\">\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<div class=\"flexbox-children loginInput_wrapp\" id=\"password\">\n" +
                "\t\t\t\t<input class=\"password loginInput loginContent_bg\" type=\"password\" name=\"password\" placeholder=\"密码\">\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t</div>\n" +
                "\n" +
                "\n" +
                "\t<div>\n" +
                "\t\t<div class=\"loginBtn\">登录</div>\n" +
                "\t</div>\n" +
                "\n" +
                "\t<div>\n" +
                "\t\t<div class=\"register_now\">\n" +
                "\t\t\t<a href=\"/index/Apprentice/register.html\">注册</a>\n" +
                "\t\t</div>\n" +
                "\t</div>\n" +
                "\n" +
                "\t<div class=\"display-flexbox mtp20\">\n" +
                "\t\t<div class=\"display-flexbox flexbox-children mlp20 mrp20\">\n" +
                "\t\t\t<div id=\"remember_password_wrapp\"><input class=\"remember_password\" type=\"checkbox\" value=\"1\" name=\"remember_password\"/></div>\n" +
                "\t\t\t<div class=\"plp10\">记住密码</div>\n" +
                "\t\t</div>\n" +
                "\n" +
                "\t\t<div class=\"forget_password_btn mrp15\">\n" +
                "\t\t\t<a href=\"javaScript:\" class=\"\">忘记密码？</a>\n" +
                "\t\t</div>\n" +
                "\t</div>\n" +
                "</div>\n" +
                "<script type=\"text/javascript\">\n" +
                "\tvar pupilrloginurl = \"/index/Apprentice/getlogin.html\";\n" +
                "</script>\n" +
                "<script src=\"http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js\"></script>\n" +
                "<script src=\"/static/js/jquery.cookie.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"/static/js/common.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"/static/js/common/mobile-detect.min.js?v=3.1\"></script>\n" +
                "<script type=\"text/javascript\" src=\"/static/js/translate.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"/static/js/apprentice/pupilrLogin.js?v=3.2\"></script>\n" +
                "</body>\n" +
                "</html>";

        Document document = Jsoup.parse(html);
        //已确认京东和淘宝都是商品图都是main_link，关键词都是key_word_hidden
//        String e = document.select("img[class=main_link]").attr("src");
//        System.out.println("--------------"+e);

        Elements e = document.select("div[class=loginBtn]");
        System.out.println("--------------"+e);
//        Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
//        for (Element image : images)
//        {
//            System.out.println("src : " + image.attr("src"));
//        }




    }
}