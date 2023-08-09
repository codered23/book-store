package com.example.bookstore;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            //TESTING
            Book book = new Book();
            book.setIsbn("978-3-16-148410-0");
            book.setTitle("Harry Potter Part 1");
            book.setAuthor("Joanne Kathleen Rowling");
            book.setPrice(BigDecimal.valueOf(600));
            book.setDescription("Harry Potter and the Sorcerer's Stone");
            book.setCoverImage("https://velociraptor256.files.wordpress.com/2016/01/img_0267.jpg?w=450");
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
