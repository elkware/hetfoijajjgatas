CREATE TABLE public.user (
  username VARCHAR(255) PRIMARY KEY NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE public.wail (
  id SERIAL PRIMARY KEY NOT NULL,
  content TEXT NOT NULL,
  link VARCHAR(1024) DEFAULT NULL,
  user_id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (user_id) REFERENCES public.user(username)
);
