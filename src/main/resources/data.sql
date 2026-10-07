INSERT INTO publisher (name, country)
VALUES ('Penguin Random House', 'United States');

INSERT INTO publisher (name, country)
VALUES ('HarperCollins', 'United States');

INSERT INTO publisher (name, country)
VALUES ('Hachette Livre', 'France');

INSERT INTO publisher (name, country)
VALUES ('Bloomsbury Publishing', 'United Kingdom');

INSERT INTO publisher (name, country)
VALUES ('Springer Nature', 'Germany');



insert into USERS(id, username, password)
values
    (1, 'user', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'), -- password = user
    (2, 'admin', '$2a$12$INo0nbj40sQrTB7b28KJput/bNltGmFyCfRsUhvy73qcXo5/XdsTG'); -- password = admin

insert into AUTHORITY (id, authority_name)
values
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER');

insert into USERS_AUTHORITY (user_id, authority_id)
values
    (1, 2),
    (2, 1);



INSERT INTO book (title, isbn, pages, price, publisher_id)
VALUES
        ('The Great Gatsby', '9780743273565', 180, 12.99, 1),
        ('1984', '9780451524935', 328, 14.99, 2),
        ('Pride and Prejudice', '9780141439518', 432, 11.99, 3),
        ('The Hobbit', '9780261102217', 310, 15.99, 4),
        ('Brave New World', '9780060850524', 288, 13.99, 5),
        ('To Kill a Mockingbird', '9780061120084', 336, 16.99, 1),
        ('The Catcher in the Rye', '9780316769488', 277, 12.49, 2),
        ('The Picture of Dorian Gray', '9780141439570', 254, 10.99, 3),
        ('The Lord of the Rings', '9780261102385', 1178, 29.99, 4),
        ('Animal Farm', '9780451526342', 112, 9.99, 5);