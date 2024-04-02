package org.jio.orchidbe.services.gmail_service;/*  Welcome to Jio word
    @author: Jio
    Date: 10/27/2023
    Time: 2:57 PM
    
    ProjectName: fams-backend
    Jio: I wish you always happy with coding <3
*/


import org.jio.orchidbe.models.users.User;

public interface ISendMailService {
    void sendMailCreatedUser(User user, String password);

    void sendMail(User user, String title, String text);

    String sendVerifyCode(String email);
    //boolean verifyCodeIsValid( VerifyDTORequest verifyDTORequest);

    String resendVerifyCode(String email);
}
