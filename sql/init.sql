CREATE DATABASE IF NOT EXISTS campus_hub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_hub;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY,
  phone VARCHAR(20) NOT NULL,
  nickname VARCHAR(64),
  avatar_url VARCHAR(255),
  points INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_users_phone(phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS file_records (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  original_name VARCHAR(255),
  object_key VARCHAR(255),
  file_url VARCHAR(500),
  file_type VARCHAR(50),
  file_size BIGINT,
  status VARCHAR(32),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_file_user_created(user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ai_tasks (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  file_id BIGINT NOT NULL,
  task_type VARCHAR(32),
  status VARCHAR(32),
  result_text TEXT,
  error_message VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_ai_user_created(user_id, created_at),
  KEY idx_ai_status_created(status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS trade_items (
  id BIGINT PRIMARY KEY,
  seller_id BIGINT NOT NULL,
  title VARCHAR(100),
  description TEXT,
  price DECIMAL(10,2),
  category VARCHAR(50),
  cover_url VARCHAR(500),
  status VARCHAR(32),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_trade_category_status(category, status),
  KEY idx_trade_seller_created(seller_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY,
  order_no VARCHAR(64) NOT NULL,
  buyer_id BIGINT NOT NULL,
  seller_id BIGINT,
  item_id BIGINT,
  item_title VARCHAR(255) NOT NULL DEFAULT '',
  amount DECIMAL(10,2),
  order_type VARCHAR(32),
  status VARCHAR(32),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_orders_order_no(order_no),
  KEY idx_orders_buyer_created(buyer_id, created_at),
  KEY idx_orders_status_created(status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pay_records (
  id BIGINT PRIMARY KEY,
  order_no VARCHAR(64),
  pay_no VARCHAR(64),
  amount DECIMAL(10,2),
  status VARCHAR(32),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_pay_no(pay_no),
  KEY idx_pay_order_no(order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notices (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(100),
  content VARCHAR(1000),
  read_status TINYINT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_notice_user_created(user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
