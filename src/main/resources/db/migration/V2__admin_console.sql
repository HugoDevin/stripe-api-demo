CREATE TABLE admin_users (
  id uuid PRIMARY KEY,
  email varchar(255) UNIQUE NOT NULL,
  password_hash varchar(255) NOT NULL,
  display_name varchar(255),
  role varchar(32) NOT NULL,
  email_verified boolean NOT NULL DEFAULT false,
  enabled boolean NOT NULL DEFAULT false,
  locked boolean NOT NULL DEFAULT false,
  last_login_at timestamptz,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL
);

CREATE TABLE admin_email_verification_tokens (
  id uuid PRIMARY KEY,
  user_id uuid NOT NULL,
  token varchar(255) UNIQUE NOT NULL,
  expires_at timestamptz NOT NULL,
  used_at timestamptz,
  created_at timestamptz NOT NULL
);

CREATE TABLE admin_email_outbox (
  id uuid PRIMARY KEY,
  to_email varchar(255),
  subject varchar(255),
  body text,
  verify_url varchar(2048),
  created_at timestamptz NOT NULL
);
