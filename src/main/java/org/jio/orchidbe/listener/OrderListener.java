package org.jio.orchidbe.listener;/*  Welcome to Jio word
    @author: Jio
    Date: 3/20/2024
    Time: 4:24 AM
    
    ProjectName: Orchid-BE
    Jio: I wish you always happy with coding <3
*/

import jakarta.persistence.PostUpdate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jio.orchidbe.enums.OrderStatus;
import org.jio.orchidbe.mappers.notifi.NotifiMapper;
import org.jio.orchidbe.models.Notification;
import org.jio.orchidbe.models.auctions.Auction;
import org.jio.orchidbe.models.orders.Order;
import org.jio.orchidbe.repositorys.notifis.NotificationRepository;
import org.jio.orchidbe.services.firebase.IFirebaseService;
import org.jio.orchidbe.services.gmail_service.ISendMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class OrderListener {
    @Autowired
    private IFirebaseService firebaseService;
    @Autowired
    private NotifiMapper notifiMapper;
    @Autowired
    private ISendMailService sendMailService;
    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    @PostUpdate
    public void postUpdate(Order entity) {
        // Update Redis cache
        logger.info("postUpdate Order");
        // Tạo một ExecutorService với một luồng
        ExecutorService executor = Executors.newSingleThreadExecutor();
        if (entity.isConfirmed() && entity.getStatus().equals(OrderStatus.CONFIRMED)){

            String title = "Order id: "+entity.getId()+" was confirmed";
            String msg = "Your order has been confirmed, please wait for us to ship to you";
            // Gửi công việc gửi email đến ExecutorService
            executor.submit(() -> {
                sendMailService.sendMail(entity.getUser(), title, msg);
            });
        }
        if (entity.getStatus().equals(OrderStatus.FAILED)){
            String title = "Order id: "+entity.getId()+" was FAILED";
            String msg = "For some reason. Your order has been FAILED. It's very sad to share with you";
            // Gửi công việc gửi email đến ExecutorService
            executor.submit(() -> {
                sendMailService.sendMail(entity.getUser(), title, msg);
            });
        }


        // Đóng ExecutorService sau khi đã sử dụng
        executor.shutdown();
    }

}
