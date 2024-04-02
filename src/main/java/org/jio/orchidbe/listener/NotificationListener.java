package org.jio.orchidbe.listener;/*  Welcome to Jio word
    @author: Jio
    Date: 3/19/2024
    Time: 7:08 PM
    
    ProjectName: Orchid-BE
    Jio: I wish you always happy with coding <3
*/

import jakarta.persistence.PostPersist;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jio.orchidbe.constants.BaseConstants;
import org.jio.orchidbe.dtos.notifi.NotifiDetailDTO;
import org.jio.orchidbe.mappers.notifi.NotifiMapper;
import org.jio.orchidbe.models.Notification;
import org.jio.orchidbe.models.auctions.Auction;
import org.jio.orchidbe.models.users.User;
import org.jio.orchidbe.models.users.user_enum.UserRole;
import org.jio.orchidbe.services.firebase.IFirebaseService;
import org.jio.orchidbe.services.gmail_service.ISendMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jio.orchidbe.constants.BaseConstants.COLLECTION_AUCTION;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class NotificationListener {

    @Autowired
    private IFirebaseService firebaseService;
    @Autowired
    private NotifiMapper notifiMapper;
    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    @Autowired
    private ISendMailService sendMailService;

    @PostPersist //save = persis
    public void postPersist(Notification entity) throws ExecutionException, InterruptedException {
        // Update Redis cache
        logger.info("postPersist notification");
        NotifiDetailDTO notifiDetailDTO = notifiMapper.toResponse(entity);
        String input = notifiDetailDTO.getMsg();
        if (input.contains(":")){
            String[] parts = input.split(":"); // Tách chuỗi theo dấu :
            if (parts[0].trim().contains("Auction id")){
                String lastPart = parts[1].trim(); // Lấy phần tử thứ 2 và loại bỏ khoảng trắng ở đầu và cuối
                String[] words = lastPart.split(" "); // Tách phần tử thành các từ
                String auctionId = words[0]; // Lấy từ đầu tiên
                notifiDetailDTO.setAuctionId(auctionId);
            }

        }

        // Tạo một ExecutorService với một luồng
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Gửi công việc gửi email đến ExecutorService
        executor.submit(() -> {
            sendMailService.sendMail(entity.getUser(), entity.getTitle(),entity.getMsg());
        });

        // Đóng ExecutorService sau khi đã sử dụng
        executor.shutdown();


        firebaseService.savev2(notifiDetailDTO,notifiDetailDTO.getId(), BaseConstants.COLLECTION_NOTIFICATION);

    }

}
