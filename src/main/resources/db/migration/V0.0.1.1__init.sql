CREATE TABLE public.t_local_file
(
    id character varying(36),
    origin_name text,
    file_size bigint,
    sha256 character varying(64),
    upload_time timestamp without time zone,
    PRIMARY KEY (id)
);