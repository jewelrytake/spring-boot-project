delete from message;

insert into message(id, text, tag, user_id) values
(1, 'first', 'some', 1),
(2, 'second', 'any', 2),
(3, 'third', 'some', 1),
(4, 'fourth', 'any', 2);
alter sequence hibernate_sequence restart with 10;