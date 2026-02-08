CREATE TABLE t_user (
                        id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        email           VARCHAR(255) NOT NULL,
                        password        VARCHAR(255) NOT NULL,
                        role            VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
                        enabled         BOOLEAN DEFAULT TRUE,
                        created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                        updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

                        CONSTRAINT uk_user_email UNIQUE (email),
                        CONSTRAINT ck_user_role CHECK (role IN ('CUSTOMER', 'ADMIN'))
);

CREATE INDEX idx_user_email ON t_user(email);

CREATE TABLE t_customer (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id         UUID NOT NULL,
                          first_name      VARCHAR(100) NOT NULL,
                          last_name       VARCHAR(100) NOT NULL,
                          phone           VARCHAR(20),
                          created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                          updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

                          CONSTRAINT uk_customer_user UNIQUE (user_id),
                          CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_user ON t_customer(user_id);

CREATE TABLE t_product (
                         id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name            VARCHAR(255) NOT NULL,
                         description     TEXT,
                         base_price      DECIMAL(12,2) NOT NULL,
                         active          BOOLEAN DEFAULT TRUE,
                         created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                         updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

                         CONSTRAINT ck_product_price CHECK (base_price > 0)
);

CREATE INDEX idx_product_active ON t_product(active);

CREATE TABLE t_price_tier (
                            id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            product_id      UUID NOT NULL,
                            threshold       INTEGER NOT NULL,
                            discount_pct    DECIMAL(5,2) NOT NULL,
                            created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                            updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

                            CONSTRAINT fk_tier_product FOREIGN KEY (product_id) REFERENCES t_product(id) ON DELETE CASCADE,
                            CONSTRAINT uk_tier_product_threshold UNIQUE (product_id, threshold),
                            CONSTRAINT ck_tier_threshold CHECK (threshold > 0),
                            CONSTRAINT ck_tier_discount CHECK (discount_pct >= 0 AND discount_pct <= 100)
);

CREATE INDEX idx_tier_product ON t_price_tier(product_id);

CREATE TABLE t_group_purchase (
                                id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                product_id      UUID NOT NULL,
                                creator_id      UUID NOT NULL,
                                status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                min_participants INTEGER NOT NULL,
                                max_participants INTEGER NOT NULL,
                                current_count   INTEGER DEFAULT 1,
                                deadline        TIMESTAMP WITH TIME ZONE NOT NULL,
                                version         INTEGER DEFAULT 0,
                                current_price   DECIMAL NOT NULL,
                                created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                                updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

                                CONSTRAINT fk_gp_product FOREIGN KEY (product_id) REFERENCES t_product(id),
                                CONSTRAINT fk_gp_creator FOREIGN KEY (creator_id) REFERENCES t_customer(id),
                                CONSTRAINT ck_gp_status CHECK (status IN ('PENDING', 'FULL', 'FINALIZED', 'CANCELLED')),
                                CONSTRAINT ck_gp_min CHECK (min_participants > 0),
                                CONSTRAINT ck_gp_max CHECK (max_participants >= min_participants),
                                CONSTRAINT ck_gp_count CHECK (current_count >= 0 AND current_count <= max_participants),
                                CONSTRAINT ck_gp_cprice CHECK (current_price > 0)
);

CREATE INDEX idx_gp_product ON t_group_purchase(product_id);
CREATE INDEX idx_gp_creator ON t_group_purchase(creator_id);
CREATE INDEX idx_gp_status ON t_group_purchase(status);
CREATE INDEX idx_gp_deadline ON t_group_purchase(deadline);
CREATE INDEX idx_gp_status_deadline ON t_group_purchase(status, deadline);

CREATE TABLE t_participant (
                             id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             group_id        UUID NOT NULL,
                             customer_id     UUID NOT NULL,
                             created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                             updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

                             CONSTRAINT fk_part_group FOREIGN KEY (group_id) REFERENCES t_group_purchase(id) ON DELETE CASCADE,
                             CONSTRAINT fk_part_customer FOREIGN KEY (customer_id) REFERENCES t_customer(id),
                             CONSTRAINT uk_participant UNIQUE (group_id, customer_id)
);

CREATE INDEX idx_part_group ON t_participant(group_id);
CREATE INDEX idx_part_customer ON t_participant(customer_id);

CREATE TABLE t_processed_message (
                                     message_id VARCHAR(255) PRIMARY KEY,
                                     processed_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                     message_type VARCHAR(100) NOT NULL
);

CREATE INDEX idx_processed_message_processed_at ON t_processed_message(processed_at);