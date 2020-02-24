package com.example.ourprojecttest;
public class MyEamil {

    public static Boolean sendMail(String Email,String num) {

        boolean flag = false;
        //这个类主要是设置邮件
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost("smtp.163.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        mailInfo.setUserName("hzy2290225756@163.com");
        mailInfo.setPassword("shananhai123");//这里要注意。不能是邮箱的密码，要是邮箱的授权码。
        mailInfo.setFromAddress("hzy2290225756@163.com");
        mailInfo.setToAddress(Email);
        mailInfo.setSubject("验证码");
        mailInfo.setContent("您的验证码为:"+num);
        //这个类主要来发送邮件
        SimpleMailSender sms = new SimpleMailSender();
        sms.sendTextMail(mailInfo);//发送文体格式
//         sms.sendHtmlMail(mailInfo);//发送html格式

        flag = true;

        return flag;

    }
}