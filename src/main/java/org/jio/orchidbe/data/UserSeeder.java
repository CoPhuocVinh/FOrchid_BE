package org.jio.orchidbe.data;/*  Welcome to Jio word
    @author: Jio
    Date: 4/2/2024
    Time: 1:28 PM
    
    ProjectName: Orchid-BE
    Jio: I wish you always happy with coding <3
*/

import lombok.RequiredArgsConstructor;
import org.jio.orchidbe.models.products.Category;
import org.jio.orchidbe.models.users.User;
import org.jio.orchidbe.models.users.user_enum.Gender;
import org.jio.orchidbe.models.users.user_enum.UserRole;
import org.jio.orchidbe.repositorys.products.CategoryRepository;
import org.jio.orchidbe.repositorys.users.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    final String password = "admin";
    private final CategoryRepository categoryRepository;
    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findAll().size() == 0){
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(UserRole.ADMIN);
            admin.setGender(Gender.MALE);
            admin.setDob(new Date("22/12/2002"));
            admin.setName("admin");
            admin.setDeleted(false);
            userRepository.save(admin);
        }

        if (categoryRepository.findAll().size() == 0){

            List<Category> categoryList = new ArrayList<>();
            Category newCategory1 = Category.builder()
                    .code("biến dị-001")
                    .color("long lanh")
                    .type("Lan biến dị")
                    .build();
            categoryList.add(newCategory1);
            Category newCategory2 = Category.builder()
                    .code("docla-001")
                    .color("sắc sảo")
                    .type("Lan độc lạ")
                    .build();
            categoryList.add(newCategory2);
            categoryRepository.saveAll(categoryList);
        }


    }
}
