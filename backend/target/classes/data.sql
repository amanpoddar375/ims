INSERT INTO users (id, username, password, email, role, enabled, created_at, updated_at) VALUES
  (1, 'admin', '$2y$12$v.J88ngKnJA7lBjIx3mpeuL.6VDTDiW7DvSA6DTqEZCVMKX3ciRs6', 'admin@example.com', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'user1', '$2y$12$M1XhuZ9.jEX2gG.35Md1ce4VAeyZc0mpMY4rvxrTs8k.27YT4n4A.', 'user1@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'user2', '$2y$12$d1nbo4u0QbYxEhkOPW3RH.pLVIgFtSdn.bAicuH8lt1CPpPb3B6Ne', 'user2@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO posts (title, description, type, status, priority, attachment_url, author_id, assigned_to_id, created_at, updated_at, resolved_at) VALUES
  ('Critical issue sample', 'A critical issue that needs attention', 'ISSUE', 'UNDER_REVIEW', 'HIGH', NULL, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
  ('Feedback sample', 'General feedback from a user', 'GENERAL', 'SUBMITTED', 'MEDIUM', NULL, 3, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
  ('Resolved request', 'A resolved suggestion example', 'SUGGESTION', 'RESOLVED', 'LOW', NULL, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Draft example', 'A draft waiting to be submitted', 'OTHER', 'DRAFT', 'LOW', NULL, 3, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
  ('Complaint sample', 'Complaint that was rejected previously', 'COMPLAINT', 'REJECTED', 'MEDIUM', NULL, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

ALTER TABLE users ALTER COLUMN id RESTART WITH 10;
ALTER TABLE posts ALTER COLUMN id RESTART WITH 100;
