CREATE TABLE wail_thumbs (
  browser_fingerprint VARCHAR(1024) PRIMARY KEY NOT NULL,
  wail_ids TEXT DEFAULT NULL,
  created_at TIMESTAMP DEFAULT now()
);