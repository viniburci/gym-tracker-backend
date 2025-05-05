SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET default_table_access_method = heap;

CREATE TABLE public._user (
    id bigint NOT NULL,
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    password character varying(255),
    role character varying(255),
    CONSTRAINT _user_role_check CHECK (((role)::text = ANY ((ARRAY['USER', 'ADMIN', 'MANAGER'])::text[])))
);

CREATE SEQUENCE public._user_seq START WITH 1 INCREMENT BY 50 CACHE 1;
ALTER TABLE public._user ALTER COLUMN id SET DEFAULT nextval('public._user_seq');

CREATE TABLE public.exercise (
    id bigint NOT NULL,
    name character varying(255),
    type character varying(255),
    image oid,
    CONSTRAINT exercise_type_check CHECK (((type)::text = ANY ((ARRAY['PERNA', 'PEITO', 'BRACO', 'COSTAS', 'CARDIO', 'ALONGAMENTO', 'CORE'])::text[])))
);

CREATE SEQUENCE public.exercise_id_seq START WITH 1 INCREMENT BY 1 CACHE 1;
ALTER TABLE public.exercise ALTER COLUMN id SET DEFAULT nextval('public.exercise_id_seq');

CREATE TABLE public.token (
    expired boolean NOT NULL,
    id integer NOT NULL,
    revoked boolean NOT NULL,
    user_id bigint,
    token character varying(255),
    token_type character varying(255),
    CONSTRAINT token_token_type_check CHECK ((token_type = 'BEARER'))
);

CREATE SEQUENCE public.token_seq START WITH 1 INCREMENT BY 50 CACHE 1;
ALTER TABLE public.token ALTER COLUMN id SET DEFAULT nextval('public.token_seq');

CREATE TABLE public.workout (
    id bigint NOT NULL,
    user_id bigint,
    name character varying(255)
);

CREATE SEQUENCE public.workout_id_seq START WITH 1 INCREMENT BY 1 CACHE 1;
ALTER TABLE public.workout ALTER COLUMN id SET DEFAULT nextval('public.workout_id_seq');

CREATE TABLE public.workout_exercise (
    reps integer NOT NULL,
    sets integer NOT NULL,
    exercise_id bigint,
    id bigint NOT NULL,
    workout_id bigint
);

CREATE SEQUENCE public.workout_exercise_id_seq START WITH 1 INCREMENT BY 1 CACHE 1;
ALTER TABLE public.workout_exercise ALTER COLUMN id SET DEFAULT nextval('public.workout_exercise_id_seq');

ALTER TABLE ONLY public._user ADD CONSTRAINT _user_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.exercise ADD CONSTRAINT exercise_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.token ADD CONSTRAINT token_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.workout ADD CONSTRAINT workout_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.workout_exercise ADD CONSTRAINT workout_exercise_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.workout_exercise
    ADD CONSTRAINT fk_workout_exercise_exercise FOREIGN KEY (exercise_id) REFERENCES public.exercise(id);

ALTER TABLE ONLY public.token
    ADD CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES public._user(id);

ALTER TABLE ONLY public.workout
    ADD CONSTRAINT fk_workout_user FOREIGN KEY (user_id) REFERENCES public._user(id);

ALTER TABLE ONLY public.workout_exercise
    ADD CONSTRAINT fk_workout_exercise_workout FOREIGN KEY (workout_id) REFERENCES public.workout(id);