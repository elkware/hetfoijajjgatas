CREATE TABLE "user" (
  username VARCHAR(255) PRIMARY KEY NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE wail (
  id SERIAL PRIMARY KEY NOT NULL,
  content TEXT NOT NULL,
  link VARCHAR(1024) DEFAULT NULL,
  user_id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (user_id) REFERENCES public.user(username)
);

ALTER TABLE wail ADD thumbs_up INTEGER DEFAULT 0;
ALTER TABLE wail ADD thumbs_down INTEGER DEFAULT 0;

CREATE TABLE comment (
  id SERIAL PRIMARY KEY NOT NULL,
  content TEXT NOT NULL,
  user_id VARCHAR(255) NOT NULL,
  wail_id INTEGER NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (user_id) REFERENCES public.user(username),
  FOREIGN KEY (wail_id) REFERENCES public.wail(id)
);

CREATE TABLE wail_thumbs (
  browser_fingerprint VARCHAR(1024) PRIMARY KEY NOT NULL,
  wail_ids TEXT DEFAULT NULL,
  created_at TIMESTAMP DEFAULT now()
);
