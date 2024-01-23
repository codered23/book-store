CREATE TABLE IF NOT EXISTS books_categories (
                                  book_id BIGINT,
                                  category_id BIGINT,
                                  PRIMARY KEY (book_id, category_id),
                                  FOREIGN KEY (book_id) REFERENCES books (id),
                                  FOREIGN KEY (category_id) REFERENCES categories (id)
);
