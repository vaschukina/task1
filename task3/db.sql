CREATE TABLE public."user"
(
    id integer NOT NULL,
    username text NOT NULL,
    displayname text,
    email text,
    PRIMARY KEY (id)
);

CREATE TABLE public.type
(
    id integer NOT NULL,
    codename text NOT NULL,
    description text,
    PRIMARY KEY (id)
);

CREATE TABLE public.status
(
    id integer NOT NULL,
    codename text NOT NULL,
    description text,
    PRIMARY KEY (id)
);

CREATE TABLE public.report
(
    id integer NOT NULL,
    user_id integer NOT NULL,
    type_id integer NOT NULL,
    description text,
    status_id integer NOT NULL,
    application_date text NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO public."user"(
	id, username, displayname, email)
	VALUES (1, 'vaschukina', 'Щукина Виктория', 'vaschukina@edu.hse.ru'),
	(2, 'inkiselev', 'Киселев Игорь', 'inkiselev@mail.ru');

INSERT INTO public.status(
	id, codename, description)
	VALUES (1, 'NEW', 'Новое'),
	(2, 'IN_PROGRESS', 'В работе'),
	(3, 'COMPLETED', 'Завершено');

INSERT INTO public.type(
	id, codename, description)
	VALUES (1, 'SOFTWARE_BUG', 'Заявка о программной ошибке'),
	(2, 'WARRANTY_REPAIR', 'Заявка на гарантийный ремонт');

INSERT INTO public.report(
	id, user_id, type_id, description, status_id, application_date)
	VALUES (1, 1, 1, 'Не открывается боковое меню', 1, '11.01.2024'),
	(2, 2, 2, 'Не работает дисплей', 2, '21.01.2024');

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "user";
GRANT ALL PRIVILEGES ON DATABASE "sdl" to "user";
