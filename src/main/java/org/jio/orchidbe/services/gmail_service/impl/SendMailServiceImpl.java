package org.jio.orchidbe.services.gmail_service.impl;/*  Welcome to Jio word
    @author: Jio
    Date: 10/27/2023
    Time: 2:56 PM
    
    ProjectName: fams-backend
    Jio: I wish you always happy with coding <3
*/


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.jio.orchidbe.models.users.User;
import org.jio.orchidbe.services.gmail_service.ISendMailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jio.orchidbe.utils.WebUtils.convertCurrentToLocalDateTimeWithZone;

@Service
@RequiredArgsConstructor
public class SendMailServiceImpl implements ISendMailService {
    private final JavaMailSender mailSender;
    private static int countResend = 0;
    private Map<HashMap<String, String>, LocalDateTime> verifyCodeMap = new HashMap<>();

    @Override
    public void sendMailCreatedUser(User user, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String emailContent = "Chúc mừng! Tài khoản của bạn đã được tạo thành công trong ứng dụng For Fresher Academy Management System (FAMS).\n" +
                    "\n" +
                    "Dưới đây là thông tin tài khoản của bạn:\n" +
                    "\n" +
                    "Tên dự án: For Fresher Academy Management System\n" +
                    "\n" +
                    "Tên người dùng: " + user.getName() + "\n" +
                    "\n" +
                    "Email: " + user.getEmail() + "\n" +
                    "\n" +
                    "Mật khẩu: " + password + "\n" +
                    "\n" +
                    "Vai Trò: \n" +
                    "\n" +
                    "Vui lòng lưu trữ thông tin này một cách an toàn. Để bảo đảm tính bảo mật, bạn nên thay đổi mật khẩu của mình sau khi đăng nhập lần đầu tiên. Dưới đây là cách để thay đổi mật khẩu:\n" +
                    "\n" +
                    "1. Đăng nhập vào tài khoản của bạn tại Trang chủ.\n" +
                    "\n" +
                    "2. Truy cập phần \"Thay đổi mật khẩu\" trong tài khoản của bạn.\n" +
                    "\n" +
                    "3. Nhập mật khẩu hiện tại và sau đó nhập mật khẩu mới của bạn.\n" +
                    "\n" +
                    "4. Lưu thay đổi.\n" +
                    "\n" +
                    "Nếu bạn gặp bất kỳ khó khăn nào trong việc thay đổi mật khẩu hoặc có bất kỳ câu hỏi nào về tài khoản của bạn, xin vui lòng liên hệ với chúng tôi.\n" +
                    "\n";

            emailContent += "Chúng tôi rất vui được chào đón bạn* vào FAMS và hy vọng bạn sẽ có trải nghiệm tốt khi sử dụng dịch vụ của chúng tôi.\n" +
                    "\n"
                    + new Date() + "\n"
                    + "Thân ái\n"
                    + "FAMS.";

            // gui meo
            helper.setTo(user.getEmail());
            helper.setSubject("FAMS - user_controller Account was created");
            helper.setText(emailContent);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void sendMail(User user, String title, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String emailContent = "FOrchid xin chào bạn "+user.getName()+" !(FOrchid).\n" +
                    "\n" +
                    "Dưới đây là nội dung thông báo tới bạn:\n" +
                    "\n" +
                    text
                        +
                    "\n";

            emailContent += "Chúng tôi rất vui được chào đón bạn* vào FOrchid và hy vọng bạn sẽ có trải nghiệm tốt khi sử dụng dịch vụ của chúng tôi.\n" +
                    "\n"
                    + convertCurrentToLocalDateTimeWithZone() + "\n"
                    + "Thân ái\n"
                    + "FOrchid.";

            // gui meo
            helper.setTo(user.getEmail());
            helper.setSubject(title);
            helper.setText(emailContent);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String sendVerifyCode(String email) {
        String verifyCode = RandomStringUtils.randomNumeric(6);
        HashMap<String, String> verify = new HashMap<>();
        verify.put(email, enVerifyCode(0, verifyCode));
        verifyCodeMap.put(verify, LocalDateTime.now().plusMinutes(1));
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Account Verification");
            helper.setText("Your authentication code is: " + verifyCode);


            ExecutorService executor = Executors.newSingleThreadExecutor();

            executor.submit(() -> {
                mailSender.send(message);
            });

            executor.shutdown();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return verifyCode;
    }



    public String resendVerifyCode(String userEmail) {

        HashMap<String, String> verify = getEntryByEmail(userEmail);
        if(verify == null){
            return "false by not send verify code before resend";
        }

        int count = deEnVerifyCode4Count(verify.get(userEmail));
        if (count >= 1) {
            LocalDateTime expirationTime = verifyCodeMap.get(verify);
            if (expirationTime == null || expirationTime.isAfter(LocalDateTime.now())) {
                return "false by resend still validated";
            }
            count++;
        }else {
            count++;
        }

        String newVerifyCode = RandomStringUtils.randomNumeric(6);

        verify.put(userEmail, enVerifyCode(count, newVerifyCode));
        // Lưu verify code mới và thời gian hết hạn mới vào map
        verifyCodeMap.put(verify, LocalDateTime.now().plusMinutes(1));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(userEmail);
            helper.setSubject("Account Verification - Resend Code:");
            helper.setText("Your new authentication code is: " + newVerifyCode);
            ExecutorService executor = Executors.newSingleThreadExecutor();

            // Gửi công việc gửi email đến ExecutorService
            executor.submit(() -> {
                mailSender.send(message);
            });

            // Đóng ExecutorService sau khi đã sử dụng
            executor.shutdown();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return newVerifyCode;
    }

    public HashMap<String, String> getEntryByEmail(String email) {
        for (Map.Entry<HashMap<String, String>, LocalDateTime> entry : verifyCodeMap.entrySet()) {
            HashMap<String, String> verify = entry.getKey();
            if (verify.containsKey(email)) {
                return verify;
            }
        }
        return null;
    }

    public String enVerifyCode(int count, String verifyCode){
        return count +"-"+ verifyCode;
    }
    public int deEnVerifyCode4Count (String verifyCode){
        String[] codeList = verifyCode.split("-");

        int count = Integer.parseInt(codeList[0]);
        return count;
    }

    public String deEnVerifyCode4Code (String verifyCode){
        String[] codeList = verifyCode.split("-");
        return codeList[1];
    }
}
