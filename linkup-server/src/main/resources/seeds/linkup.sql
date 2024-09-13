-- Table for users who initiate order (clients)
CREATE TABLE client (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each client
                         username VARCHAR(255) NOT NULL,  -- Username of the client
                         age INT,  -- Age of the client
                         gender VARCHAR(10),  -- Gender of the client
                         avatar VARCHAR(255),
                         openid VARCHAR(255),
                         session_key VARCHAR(255),
                         unionid VARCHAR(255),
                         completed_order_count INT DEFAULT 0,  -- Number of completed orders by the client
                         balance DECIMAL(10, 2) DEFAULT 0.00,  -- Client's balance
                         is_deleted BOOLEAN DEFAULT FALSE COMMENT '0: not deleted; 1: deleted',  -- Flag for soft deletion
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Time when the client was created
);

-- Table for users who receive order (servants)
CREATE TABLE servant (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each servant
                          username VARCHAR(255) NOT NULL,  -- Username of the servant
                          age INT,
                          gender VARCHAR(10),
                          avatar VARCHAR(255),
                          openid VARCHAR(255),
                          session_key VARCHAR(255),
                          unionid VARCHAR(255),
                          bio TEXT,  -- Description of the servant
                          completed_order_count INT DEFAULT 0,
                          balance DECIMAL(10, 2) DEFAULT 0.00,  -- Servant's balance
                          is_deleted BOOLEAN DEFAULT FALSE COMMENT '0: not deleted; 1: deleted',  -- Flag for soft deletion
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table for orders initiated by clients
CREATE TABLE order (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each order
                        title VARCHAR(255) NOT NULL,  -- Title of the order
                        servant_type VARCHAR(255) NOT NULL,  -- Required servant type (e.g., guide, driver)
                        required_gender VARCHAR(10),  -- Client's preferred gender for the servant
                        age_from INT,  -- Minimum age of the required servant
                        age_to INT,  -- Maximum age of the required servant
                        service_duration INT,  -- Duration of the service in hours
                        city VARCHAR(255),
                        address TEXT,
                        price DECIMAL(10, 2) NOT NULL,  -- Price of the service
                        rating DECIMAL(3, 2),
                        candidate_count INT DEFAULT 0,  -- Number of servants who applied to the order
                        status INT DEFAULT 0 COMMENT '0: undone; 1: done',  -- Status of the order
                        is_deleted BOOLEAN DEFAULT FALSE COMMENT '0: not deleted; 1: deleted',  -- Flag for soft deletion
                        completed_at TIMESTAMP,  -- Time when the order was completed
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Time when the order was created
);

-- Table to record which servants have applied to an order
CREATE TABLE order_candidate (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each order candidate
                                  order_id BIGINT NOT NULL,  -- The ID of the related order
                                  servant_id BIGINT NOT NULL,  -- The ID of the servant who applied for the order
                                  is_deleted BOOLEAN DEFAULT FALSE COMMENT '0: not deleted; 1: deleted',  -- Flag for soft deletion
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Time when the candidate applied
);

-- Table for the types of servant (e.g., guide, driver)
CREATE TABLE servant_type (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each servant type
                               type_name VARCHAR(255) NOT NULL,  -- Name of the servant type (e.g., guide)
                               servant_id BIGINT NOT NULL,
                               is_deleted BOOLEAN DEFAULT FALSE COMMENT '0: not deleted; 1: deleted',  -- Flag for soft deletion
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Time when the servant type was created
);

-- Table for tags associated with servants
CREATE TABLE tag (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each tag
                      tag_name VARCHAR(255) NOT NULL,  -- Name of the tag (e.g., 'experienced', 'bilingual')
                      servant_id BIGINT NOT NULL,
                      is_deleted BOOLEAN DEFAULT FALSE COMMENT '0: not deleted; 1: deleted',  -- Flag for soft deletion
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Time when the tag was created
);