-- /sql/create-default-books.sql
INSERT INTO books (id, title, author, isbn, description, price, cover_image, is_deleted)
VALUES (100, 'Special case', 'First Author', '1231244214', 'best practices for Java Platform', 20.00, 'some image', FALSE);

INSERT INTO books (id, title, author, isbn, description, price, cover_image, is_deleted)
VALUES (101, 'Second Book', 'Second Author', '431413241', 'best practices for Java Platform', 30.00, 'some image', FALSE);

INSERT INTO books (id, title, author, isbn, description, price, cover_image, is_deleted)
VALUES (10, 'Third Book', 'Third Author', '764831641', 'best practices for Java Platform', 15.00, 'some image', FALSE);