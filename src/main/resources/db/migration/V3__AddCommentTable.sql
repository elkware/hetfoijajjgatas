CREATE TABLE public.comment (
  id SERIAL PRIMARY KEY NOT NULL,
  content TEXT NOT NULL,
  user_id VARCHAR(255) NOT NULL,
  wail_id INTEGER NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (user_id) REFERENCES public.user(username),
  FOREIGN KEY (wail_id) REFERENCES public.wail(id)
);